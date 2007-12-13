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

    public static final int STATE_STARTUP = 1;

    public static final int STATE_RUNNING = 2;

    public static final int STATE_SUCCESS = 3;

    public static final int STATE_FAILED = 4;

    public static final int STATE_NONE = 0;

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

                final int stateNow;
                if (!this.shutdown) {
                    stateNow = jobId.getBackend().getState(jobId);
                } else {
                    stateNow = JobWatcher.STATE_FAILED;
                }
                boolean differs;
                synchronized (this.jobstate) {
                    Integer oldState = (Integer) this.jobstate.get(jobId);
                    if (oldState == null) {
                        oldState = new Integer(JobWatcher.STATE_NONE);
                    }
                    differs = oldState.intValue() != stateNow;
                    if (differs) {
                        this.jobstate.put(jobId, new Integer(stateNow));
                    }
                }

                if (differs) {
                    Set listeners;
                    synchronized (this) {
                        listeners = new HashSet((Set) this.joblisteners
                                .get(jobId));
                        if (stateNow == JobWatcher.STATE_SUCCESS
                                || stateNow == JobWatcher.STATE_FAILED) {
                            this.joblisteners.remove(jobId);
                        }
                    }

                    final Iterator li = listeners.iterator();
                    while (li.hasNext()) {
                        final JobListener listener = (JobListener) li.next();
                        switch (stateNow) {
                        case STATE_STARTUP:
                            listener.startup(jobId);
                            break;
                        case STATE_RUNNING:
                            listener.running(jobId);
                            break;
                        case STATE_SUCCESS:
                            listener.done(jobId, true);
                            break;
                        case STATE_FAILED:
                            listener.done(jobId, false);
                            break;
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
