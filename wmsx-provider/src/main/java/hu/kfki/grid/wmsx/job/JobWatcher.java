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

    private final Map joblisteners = new HashMap();

    private final Map jobstate = new HashMap();

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

    public synchronized void addWatch(final JobUid jobId,
            final JobListener listener) {
        if (!this.shutdown) {
            Set listeners = (Set) this.joblisteners.get(jobId);
            if (listeners == null) {
                listeners = new HashSet();
                this.joblisteners.put(jobId, listeners);
            }
            listeners.add(listener);
            this.doStart();
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

            Set jobs;
            synchronized (this) {
                jobs = new HashSet(this.joblisteners.keySet());
            }

            final Iterator it = jobs.iterator();
            while (it.hasNext()) {
                final JobUid jobId = (JobUid) it.next();

                final JobState stateNow;
                if (!this.shutdown) {
                    stateNow = jobId.getBackend().getState(jobId);
                } else {
                    stateNow = JobState.FAILED;
                }
                boolean differs;
                synchronized (this.jobstate) {
                    JobState oldState = (JobState) this.jobstate.get(jobId);
                    if (oldState == null) {
                        oldState = JobState.NONE;
                    }
                    differs = !oldState.equals(stateNow);
                    if (differs) {
                        this.jobstate.put(jobId, stateNow);
                    }
                }

                if (differs) {
                    Set listeners;
                    synchronized (this) {
                        listeners = new HashSet((Set) this.joblisteners
                                .get(jobId));
                        if (JobState.SUCCESS.equals(stateNow)
                                || JobState.FAILED.equals(stateNow)) {
                            this.joblisteners.remove(jobId);
                        }
                    }

                    final Iterator li = listeners.iterator();
                    while (li.hasNext()) {
                        final JobListener listener = (JobListener) li.next();
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

    public synchronized int getNumJobsRunning() {
        return this.joblisteners.size();
    }

}
