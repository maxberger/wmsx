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

/* $Id: vasblasd$ */

package hu.kfki.grid.wmsx.worker;

import hu.kfki.grid.wmsx.backends.Backends;
import hu.kfki.grid.wmsx.backends.JobUid;
import hu.kfki.grid.wmsx.job.JobState;
import hu.kfki.grid.wmsx.job.JobWatcher;
import hu.kfki.grid.wmsx.util.FileUtil;

import java.io.File;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Logger;

import net.jini.id.Uuid;

public class ControllerImpl implements Controller, Runnable {

    private final Map<Object, JobUid> juidMap = new TreeMap<Object, JobUid>();

    private final LinkedList<ControllerWorkDescription> pending = new LinkedList<ControllerWorkDescription>();

    private final Map<Object, ControllerWorkDescription> running = new TreeMap<Object, ControllerWorkDescription>();

    private final Map<Object, Uuid> assignedTo = new TreeMap<Object, Uuid>();

    private final Map<Object, Long> assignedAt = new TreeMap<Object, Long>();

    private final Map<Object, Map<String, byte[]>> success = new TreeMap<Object, Map<String, byte[]>>();

    private final Set<Object> failed = new TreeSet<Object>();

    private final Map<Uuid, Long> lastSeen = new HashMap<Uuid, Long>();

    private boolean pendingCheckRunning;

    private static final Logger LOGGER = Logger.getLogger(ControllerImpl.class
            .toString());

    ControllerImpl() {
    }

    public WorkDescription retrieveWork(final Uuid uuid) {
        final ControllerWorkDescription cwd;
        final Object jobid;
        this.ping(uuid);
        synchronized (this.pending) {
            if (this.pending.isEmpty()) {
                return null;
            }
            cwd = this.pending.removeFirst();
            jobid = cwd.getWorkDescription().getId();
            this.running.put(jobid, cwd);
            this.assignedTo.put(jobid, uuid);
            this.assignedAt.put(jobid, new Long(System.currentTimeMillis()));
        }
        ControllerImpl.LOGGER.info("Assigning job " + jobid + " to worker "
                + uuid);
        JobWatcher.getWatcher().checkWithState(this.getJuidForId(jobid),
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

    public void addWork(final ControllerWorkDescription newWork) {
        synchronized (this.pending) {
            this.pending.add(newWork);
        }
        JobWatcher.getWatcher().checkWithState(
                this.getJuidForId(newWork.getWorkDescription().getId()),
                JobState.STARTUP);
    }

    @SuppressWarnings("unchecked")
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
        }
        if (isNew) {
            ControllerImpl.LOGGER.info("Done with worker Job " + id + " ("
                    + uuid + ")");
            JobWatcher.getWatcher().checkWithState(this.getJuidForId(id),
                    JobState.SUCCESS);
        }
    }

    public JobState getState(final Object id) {
        synchronized (this.pending) {
            if (this.failed.contains(id)) {
                return JobState.FAILED;
            }
            if (this.success.keySet().contains(id)) {
                return JobState.SUCCESS;
            }
            if (this.running.keySet().contains(id)) {
                return JobState.RUNNING;
            }
            for (final ControllerWorkDescription cwd : this.pending) {
                if (cwd.getWorkDescription().getId().equals(id)) {
                    return JobState.STARTUP;
                }
            }
        }
        return JobState.NONE;
    }

    public void retrieveSandbox(final Object id, final File dir) {
        Map<String, byte[]> sandbox;
        synchronized (this.pending) {
            sandbox = this.success.get(id);
        }
        FileUtil.retrieveSandbox(sandbox, dir);
    }

    public JobUid getJuidForId(final Object id) {
        JobUid j;
        synchronized (this.juidMap) {
            j = this.juidMap.get(id);
            if (j == null) {
                j = new JobUid(Backends.WORKER, id);
                this.juidMap.put(id, j);
            }
        }
        return j;
    }

    public void ping(final Uuid uuid) {
        synchronized (this.lastSeen) {
            this.lastSeen.put(uuid, new Long(System.currentTimeMillis()));
        }
    }

    public void run() {
        boolean goon = true;
        while (goon) {
            try {
                Thread.sleep(30 * 1000);
            } catch (final InterruptedException e) {
                // ignore
            }
            synchronized (this.pending) {

                final Set<Object> suspicous = new TreeSet<Object>();

                synchronized (this.lastSeen) {
                    final long now = System.currentTimeMillis();
                    for (final Object id : this.running.keySet()) {
                        final Uuid uuid = this.assignedTo.get(id);
                        final long seen = this.lastSeen.get(uuid).longValue();
                        final long alive = now - seen;
                        if (alive > 90 * 1000) {
                            suspicous.add(id);
                        }
                        final long timerunning = now
                                - this.assignedAt.get(id).longValue();
                        if (timerunning > 120 * 1000) {
                            suspicous.add(id);
                        }
                    }
                }

                for (final Object id : suspicous) {
                    boolean found = false;
                    for (final ControllerWorkDescription w : this.pending) {
                        if (w.getWorkDescription().getId().equals(id)) {
                            found = true;
                        }
                    }
                    if (!found) {
                        ControllerImpl.LOGGER
                                .info("Rescheduling suspicious Job " + id);
                        this.pending.add(this.running.get(id));
                    }
                }

                goon = !this.running.isEmpty();
                if (!goon) {
                    this.pendingCheckRunning = false;
                }
            }
        }
    }
}
