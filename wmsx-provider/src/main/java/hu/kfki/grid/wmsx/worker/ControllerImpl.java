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

package hu.kfki.grid.wmsx.worker;

import hu.kfki.grid.wmsx.backends.Backends;
import hu.kfki.grid.wmsx.backends.JobUid;
import hu.kfki.grid.wmsx.job.JobState;
import hu.kfki.grid.wmsx.job.JobWatcher;
import hu.kfki.grid.wmsx.job.description.EmptyJobDescription;
import hu.kfki.grid.wmsx.util.FileUtil;

import java.io.File;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Logger;

import net.jini.id.Uuid;

/**
 * Controller for worker jobs.
 * 
 * @version $Revision$
 */
public class ControllerImpl implements Controller, Runnable {

    private static final int STARTUP_DELAY = 30;

    private static final int MAX_TIME_WITHOUT_PING = 120;

    private static final int MAX_TIME_RUNNING = 600;

    private static final int MSEC_TO_SEC = 1000;

    private static final Logger LOGGER = Logger.getLogger(ControllerImpl.class
            .toString());

    private final Map<Object, JobUid> juidMap = new TreeMap<Object, JobUid>();

    private final List<ControllerWorkDescription> pending = new LinkedList<ControllerWorkDescription>();

    private final Map<Object, ControllerWorkDescription> running = new TreeMap<Object, ControllerWorkDescription>();

    private final Map<Object, Uuid> assignedTo = new TreeMap<Object, Uuid>();

    private final Map<Object, Long> assignedAt = new TreeMap<Object, Long>();

    private final Map<Object, Map<String, byte[]>> success = new TreeMap<Object, Map<String, byte[]>>();

    private final Set<Object> failed = new TreeSet<Object>();

    private final Map<Uuid, Long> lastSeen = new HashMap<Uuid, Long>();

    private final WorkDescription shutdownWorkDescription;

    private boolean pendingCheckRunning;

    private boolean shutdownState;

    ControllerImpl() {
        this.shutdownState = false;
        this.shutdownWorkDescription = new ControllerWorkDescription(
                "shutdown", new EmptyJobDescription()).getWorkDescription();
    }

    /** {@inheritDoc} */
    public WorkDescription retrieveWork(final Uuid uuid) {
        if (this.shutdownState) {
            return this.shutdownWorkDescription;
        }
        final ControllerWorkDescription cwd;
        final Object jobid;
        this.ping(uuid);
        synchronized (this.pending) {
            if (this.pending.isEmpty()) {
                return null;
            }
            cwd = this.pending.remove(0);
            jobid = cwd.getWorkDescription().getId();
            this.running.put(jobid, cwd);
            this.assignedTo.put(jobid, uuid);
            this.assignedAt
                    .put(jobid, Long.valueOf(System.currentTimeMillis()));
        }
        ControllerImpl.LOGGER.info("Assigning job " + jobid + " to worker "
                + uuid);
        JobWatcher.getInstance().checkWithState(this.getJuidForId(jobid),
                JobState.RUNNING);
        this.startPendingCheck();
        return cwd.getWorkDescription();
    }

    private void startPendingCheck() {
        synchronized (this.pending) {
            if (!this.pendingCheckRunning) {
                this.pendingCheckRunning = true;
                new Thread(this).start();
            }
        }
    }

    /**
     * Add new work to be done.
     * 
     * @param newWork
     *            work description.
     */
    public void addWork(final ControllerWorkDescription newWork) {
        synchronized (this.pending) {
            this.pending.add(newWork);
        }
        JobWatcher.getInstance().checkWithState(
                this.getJuidForId(newWork.getWorkDescription().getId()),
                JobState.STARTUP);
    }

    /** {@inheritDoc} */
    public void doneWith(final Object id, final ResultDescription result,
            final Uuid uuid) throws RemoteException {
        final boolean isNew;
        synchronized (this.pending) {
            if (this.running.containsKey(id)) {
                this.running.remove(id);
                this.assignedTo.remove(id);
                this.success.put(id, result.getOutputSandbox());
                isNew = true;
            } else {
                isNew = false;
                ControllerImpl.LOGGER
                        .info("Retrieved duplicate result for job " + id + " ("
                                + uuid + ")");
            }
            this.pending.remove(id);
        }
        if (isNew) {
            ControllerImpl.LOGGER.info("Done with worker Job " + id + " ("
                    + uuid + ")");
            JobWatcher.getInstance().checkWithState(this.getJuidForId(id),
                    JobState.SUCCESS);
        }
    }

    /**
     * Check job status.
     * 
     * @param id
     *            Internal Id
     * @return current {@link JobState}.
     */
    public JobState getState(final Object id) {
        JobState retVal = JobState.NONE;
        synchronized (this.pending) {
            if (this.failed.contains(id)) {
                retVal = JobState.FAILED;
            } else if (this.success.keySet().contains(id)) {
                retVal = JobState.SUCCESS;
            } else if (this.running.keySet().contains(id)) {
                retVal = JobState.RUNNING;
            } else {
                for (final ControllerWorkDescription cwd : this.pending) {
                    if (cwd.getWorkDescription().getId().equals(id)) {
                        retVal = JobState.STARTUP;
                        break;
                    }
                }
            }
        }
        return retVal;
    }

    /**
     * Store the Sandbox from a given job into a given directory.
     * 
     * @param id
     *            Id of the (finished) job.
     * @param dir
     *            Directory to store into.
     */
    public void retrieveSandbox(final Object id, final File dir) {
        final Map<String, byte[]> sandbox;
        synchronized (this.pending) {
            sandbox = this.success.get(id);
        }
        FileUtil.retrieveSandbox(sandbox, dir);
        synchronized (this.pending) {
            this.success.remove(id);
        }
    }

    /**
     * convert internal id to JobUid.
     * 
     * @param id
     *            internal id
     * @return a JobUid
     */
    public JobUid getJuidForId(final Object id) {
        JobUid j;
        synchronized (this.juidMap) {
            j = this.juidMap.get(id);
            if (j == null) {
                j = new JobUid(Backends.getInstance().get("worker"), id);
                this.juidMap.put(id, j);
            }
        }
        return j;
    }

    /** {@inheritDoc} */
    public void ping(final Uuid uuid) {
        synchronized (this.lastSeen) {
            this.lastSeen.put(uuid, Long.valueOf(System.currentTimeMillis()));
        }
    }

    /** pending checker. */
    public void run() {
        boolean goon = true;
        while (goon) {
            try {
                Thread.sleep(ControllerImpl.STARTUP_DELAY
                        * ControllerImpl.MSEC_TO_SEC);
            } catch (final InterruptedException e) {
                // ignore
            }
            synchronized (this.pending) {
                final Set<Object> suspicous = this.findSuspicious();
                this.rescheduleSuspicous(suspicous);
                goon = !this.running.isEmpty();
                if (!goon) {
                    this.pendingCheckRunning = false;
                }
            }
        }
    }

    private void rescheduleSuspicous(final Set<Object> suspicous) {
        for (final Object id : suspicous) {
            boolean found = false;
            for (final ControllerWorkDescription w : this.pending) {
                if (w.getWorkDescription().getId().equals(id)) {
                    found = true;
                }
            }
            if (!found) {
                ControllerImpl.LOGGER.info("Rescheduling suspicious Job " + id);
                this.pending.add(this.running.get(id));
            }
        }
    }

    private Set<Object> findSuspicious() {
        final Set<Object> suspicous = new TreeSet<Object>();

        synchronized (this.lastSeen) {
            final long now = System.currentTimeMillis();
            for (final Object id : this.running.keySet()) {
                final Uuid uuid = this.assignedTo.get(id);
                final long seen = this.lastSeen.get(uuid).longValue();
                final long alive = now - seen;
                if (alive > ControllerImpl.MAX_TIME_WITHOUT_PING
                        * ControllerImpl.MSEC_TO_SEC) {
                    suspicous.add(id);
                }
                final long timerunning = now
                        - this.assignedAt.get(id).longValue();
                if (timerunning > ControllerImpl.MAX_TIME_RUNNING
                        * ControllerImpl.MSEC_TO_SEC) {
                    suspicous.add(id);
                }
            }
        }
        return suspicous;
    }

    /**
     * set shutdown state.
     * 
     * @param newShutdown
     *            new state. If true it shuts down.
     */
    public void setShutdownState(final boolean newShutdown) {
        this.shutdownState = newShutdown;
    }
}
