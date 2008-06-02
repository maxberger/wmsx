/*
 * WMSX - Workload Management Extensions for gLite
 * 
 * Copyright (C) 2007-2008 Max Berger
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see http://www.gnu.org/licenses/.
 * 
 */

/* $Id$ */

package hu.kfki.grid.wmsx.job;

import hu.kfki.grid.wmsx.backends.JobUid;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Watches currently running jobs.
 * 
 * @version $Revision$
 */
public final class JobWatcher implements Runnable {
    private static final int TIME_BETWEEN_CHECKS_MS = 10000;

    private static final Logger LOGGER = Logger.getLogger(JobWatcher.class
            .toString());

    private final Map<JobUid, Set<JobListener>> joblisteners = new HashMap<JobUid, Set<JobListener>>();

    private final Map<JobUid, JobState> jobstate = new HashMap<JobUid, JobState>();

    private Thread runThread;

    private boolean shutdown;

    private static final class SingletonHolder {
        private static final JobWatcher INSTANCE = new JobWatcher();

        private SingletonHolder() {
        }
    }

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

    /**
     * @return Singleton instance.
     */
    public static JobWatcher getInstance() {
        return JobWatcher.SingletonHolder.INSTANCE;
    }

    /**
     * Add a watcher for the given JobId.
     * 
     * @param jobId
     *            JobId to watcher
     * @param listener
     *            The watcher to add.
     */
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

    /**
     * Initiate shutdown.
     */
    public synchronized void shutdown() {
        this.shutdown = true;
        if (this.runThread != null) {
            this.runThread.interrupt();
        }
    }

    /** Constantly watches jobs. */
    public void run() {

        boolean done = false;

        while (!done) {
            try {
                Thread.sleep(JobWatcher.TIME_BETWEEN_CHECKS_MS);
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

                JobState stateNow;
                final boolean terminate;
                synchronized (this) {
                    terminate = this.shutdown;
                }
                if (terminate) {
                    stateNow = JobState.FAILED;
                } else {
                    stateNow = jobId.getBackend().getState(jobId);
                }
                if (stateNow == null) {
                    stateNow = JobState.NONE;
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
    }

    /**
     * Push method if state has changed.
     * 
     * @param jobId
     *            Id of the job
     * @param stateNow
     *            new state.
     */
    public void checkWithState(final JobUid jobId, final JobState stateNow) {
        final boolean differs = this.hasStateChanged(jobId, stateNow);
        if (differs) {
            final Set<JobListener> listeners = this
                    .getSafeCopyOfJobListeners(jobId);
            this.notifyListeners(jobId, stateNow, listeners);
            this.removeDoneStates(jobId, stateNow);
        }
    }

    private void removeDoneStates(final JobUid jobId, final JobState stateNow) {
        if (JobState.SUCCESS.equals(stateNow)
                || JobState.FAILED.equals(stateNow)) {
            synchronized (this) {
                this.joblisteners.remove(jobId);
            }
        }
    }

    private void notifyListeners(final JobUid jobId, final JobState stateNow,
            final Set<JobListener> listeners) {
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

    private synchronized Set<JobListener> getSafeCopyOfJobListeners(
            final JobUid jobId) {
        Set<JobListener> listeners;
        final Set<JobListener> gset = this.joblisteners.get(jobId);
        if (gset == null) {
            listeners = new HashSet<JobListener>();
        } else {
            listeners = new HashSet<JobListener>(gset);
        }
        return listeners;
    }

    private boolean hasStateChanged(final JobUid jobId, final JobState stateNow) {
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
        return differs;
    }

    /**
     * get the number of running jobs.
     * 
     * @return number of running jobs.
     */
    public synchronized int getNumJobsRunning() {
        return this.joblisteners.size();
    }

}
