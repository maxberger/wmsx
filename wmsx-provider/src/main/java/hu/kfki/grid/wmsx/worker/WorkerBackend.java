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

import hu.kfki.grid.wmsx.backends.Backend;
import hu.kfki.grid.wmsx.backends.JobUid;
import hu.kfki.grid.wmsx.backends.SubmissionResults;
import hu.kfki.grid.wmsx.job.JobState;
import hu.kfki.grid.wmsx.job.description.JDLJobDescription;
import hu.kfki.grid.wmsx.job.description.JobDescription;

import java.io.File;
import java.io.IOException;

public class WorkerBackend implements Backend {

    private int count = 0;

    private static WorkerBackend instance;

    private final ControllerImpl controllerImpl;

    private WorkerBackend() {
        this.controllerImpl = ControllerServer.getInstance()
                .getControllerImpl();
    }

    public static synchronized WorkerBackend getInstance() {
        if (WorkerBackend.instance == null) {
            WorkerBackend.instance = new WorkerBackend();
        }
        return WorkerBackend.instance;
    }

    public JobState getState(final JobUid uid) {
        return this.controllerImpl.getState(uid.getBackendId());
    }

    public boolean jobIdIsURI() {
        return false;
    }

    public void retrieveLog(final JobUid id, final File dir) {
        // TODO Auto-generated method stub

    }

    public Process retrieveResult(final JobUid id, final File dir) {
        this.controllerImpl.retrieveSandbox(id.getBackendId(), dir);
        return null;
    }

    public SubmissionResults submitJdl(final String jdlFile, final String vo)
            throws IOException {
        this.count++;
        final Object id = new Integer(this.count);
        final JobDescription desc = new JDLJobDescription(jdlFile);
        this.controllerImpl.addWork(new ControllerWorkDescription(id, desc));
        return new SubmissionResults(this.controllerImpl.getJuidForId(id),
                null, null, null, 0, 0);
    }

    @Override
    public String toString() {
        return "Worker";
    }

}
