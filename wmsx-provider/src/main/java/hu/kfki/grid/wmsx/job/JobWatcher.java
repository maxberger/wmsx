package hu.kfki.grid.wmsx.job;

import hu.kfki.grid.wmsx.backends.JobUid;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class JobWatcher implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(JobWatcher.class
            .toString());

    private final Map<JobUid, Set<JobListener>> joblisteners = new HashMap<JobUid, Set<JobListener>>();

    private final Map<JobUid, JobState> jobstate = new HashMap<JobUid, JobState>();

    private Thread runThread;

    private static JobWatcher jobWatcher;

    private boolean shutdown;

    private JobWatcher() {
        this.runThread = null;
        this.shutdown = false;
    }

    private synchronized void doStart() {
        if (this.runThread == null) {
            JobWatcher.LOGGER.info("Starting new Listener");
            this.runThread = new Thread(this);
            this.runThread.start();
        }
    }

    public static synchronized JobWatcher getWatcher() {
        if (JobWatcher.jobWatcher == null) {
            JobWatcher.jobWatcher = new JobWatcher();
        }
        return JobWatcher.jobWatcher;
    }

    public void addWatch(final JobUid jobId, final JobListener listener) {
        JobState stateNow;
        synchronized (this) {
            if (!this.shutdown) {
                Set<JobListener> listeners = this.joblisteners.get(jobId);
                if (listeners == null) {
                    listeners = new HashSet<JobListener>();
                    this.joblisteners.put(jobId, listeners);
                }
                listeners.add(listener);

                stateNow = this.jobstate.get(jobId);

                this.doStart();
            } else {
                stateNow = null;

            }
        }
        if (stateNow != null) {
            if (JobState.STARTUP.equals(stateNow)) {
                listener.startup(jobId);
            } else if (JobState.RUNNING.equals(stateNow)) {
                listener.startup(jobId);
                listener.running(jobId);
            } else if (JobState.SUCCESS.equals(stateNow)) {
                listener.startup(jobId);
                listener.running(jobId);
                listener.done(jobId, true);
            } else if (JobState.FAILED.equals(stateNow)) {
                listener.startup(jobId);
                listener.running(jobId);
                listener.done(jobId, false);
            }
        }
    }

    public synchronized void shutdown() {
        this.shutdown = true;
        if (this.runThread != null) {
            this.runThread.interrupt();
        }
    }

    public void run() {

        boolean done = false;

        while (!done) {
            try {
                Thread.sleep(10000);
            } catch (final InterruptedException e) {
                JobWatcher.LOGGER.fine(e.getMessage());
            }

            Set<JobUid> jobs;
            synchronized (this) {
                jobs = new HashSet<JobUid>(this.joblisteners.keySet());
            }

            final Iterator<JobUid> it = jobs.iterator();
            while (it.hasNext()) {
                final JobUid jobId = it.next();

                final JobState stateNow;
                if (!this.shutdown) {
                    stateNow = jobId.getBackend().getState(jobId);
                } else {
                    stateNow = JobState.FAILED;
                }

                this.checkWithState(jobId, stateNow);
            }
            synchronized (this) {
                if (this.joblisteners.isEmpty()) {
                    done = true;
                    this.runThread = null;
                    JobWatcher.LOGGER.info("No more jobs to listen to.");
                }
            }
        }
        System.gc();
    }

    public void checkWithState(final JobUid jobId, final JobState stateNow) {
        boolean differs;
        synchronized (this.jobstate) {
            JobState oldState = this.jobstate.get(jobId);
            if (oldState == null) {
                oldState = JobState.NONE;
            }
            differs = !oldState.equals(stateNow);
            if (differs) {
                this.jobstate.put(jobId, stateNow);
            }
        }

        if (differs) {
            Set<JobListener> listeners;
            synchronized (this) {
                final Set<JobListener> gset = this.joblisteners.get(jobId);
                if (gset == null) {
                    listeners = new HashSet<JobListener>();
                } else {
                    listeners = new HashSet<JobListener>(gset);
                }
            }

            final Iterator<JobListener> li = listeners.iterator();
            while (li.hasNext()) {
                final JobListener listener = li.next();
                if (JobState.STARTUP.equals(stateNow)) {
                    listener.startup(jobId);
                } else if (JobState.RUNNING.equals(stateNow)) {
                    listener.running(jobId);
                } else if (JobState.SUCCESS.equals(stateNow)) {
                    listener.done(jobId, true);
                } else if (JobState.FAILED.equals(stateNow)) {
                    listener.done(jobId, false);
                }
            }
        }
    }

    public synchronized int getNumJobsRunning() {
        return this.joblisteners.size();
    }

}
