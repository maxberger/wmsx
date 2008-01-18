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
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Logger;

public class ControllerImpl implements Controller {

    private final Map<Object, JobUid> juidMap = new TreeMap<Object, JobUid>();

    private final LinkedList<ControllerWorkDescription> pending = new LinkedList<ControllerWorkDescription>();

    private final Map<Object, ControllerWorkDescription> running = new TreeMap<Object, ControllerWorkDescription>();

    private final Map<Object, Map<String, byte[]>> success = new TreeMap<Object, Map<String, byte[]>>();

    private final Set<Object> failed = new TreeSet<Object>();

    private static final Logger LOGGER = Logger.getLogger(ControllerImpl.class
            .toString());

    ControllerImpl() {
    }

    public WorkDescription retrieveWork() {
        final ControllerWorkDescription cwd;
        synchronized (this.pending) {
            if (this.pending.isEmpty()) {
                return null;
            }
            cwd = this.pending.removeFirst();
            this.running.put(cwd.getWorkDescription().getId(), cwd);
        }
        JobWatcher.getWatcher().checkWithState(
                this.getJuidForId(cwd.getWorkDescription().getId()),
                JobState.RUNNING);
        return cwd.getWorkDescription();

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
    public void doneWith(final Object id, final ResultDescription result)
            throws RemoteException {
        synchronized (this.pending) {
            this.running.remove(id);
            this.success.put(id, result.getOutputSandbox());
        }
        ControllerImpl.LOGGER.info("Done with worker Job " + id);
        JobWatcher.getWatcher().checkWithState(this.getJuidForId(id),
                JobState.SUCCESS);
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
}
