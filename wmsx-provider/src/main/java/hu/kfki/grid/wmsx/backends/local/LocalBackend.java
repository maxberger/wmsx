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

package hu.kfki.grid.wmsx.backends.local;

import hu.kfki.grid.wmsx.backends.Backend;
import hu.kfki.grid.wmsx.backends.JobUid;
import hu.kfki.grid.wmsx.backends.SubmissionResults;
import hu.kfki.grid.wmsx.job.JobState;
import hu.kfki.grid.wmsx.job.description.JobDescription;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

/**
 * Local backend.
 * 
 * @version $Revision$
 */
public final class LocalBackend implements Backend {

    private int count;

    private final Map<JobUid, JobState> state;

    private final Map<JobUid, LocalProcess> processes;

    /**
     * Default constructor.
     */
    public LocalBackend() {
        this.state = new Hashtable<JobUid, JobState>();
        this.processes = new Hashtable<JobUid, LocalProcess>();
    };

    /** {@inheritDoc} */
    public JobState getState(final JobUid uid) {
        return this.state.get(uid);
    }

    /** {@inheritDoc} */
    public boolean jobIdIsURI() {
        return false;
    }

    /** {@inheritDoc} */
    public void retrieveLog(final JobUid id, final File dir) {
        // Ignore
    }

    /** {@inheritDoc} */
    public Process retrieveResult(final JobUid id, final File dir) {
        final LocalProcess lp = this.processes.get(id);
        if (lp != null) {
            lp.retrieveOutput(dir);
        }
        return null;
    }

    /** {@inheritDoc} */
    public SubmissionResults submitJob(final JobDescription job, final String vo)
            throws IOException {
        this.count++;
        final Object id = Integer.valueOf(this.count);
        final JobUid juid = new JobUid(this, id);
        final JobDescription desc = job;
        final LocalProcess p = new LocalProcess(this.state, juid, desc);
        this.processes.put(juid, p);
        new Thread(p).start();
        return new SubmissionResults(juid, null, null, null, 0, 0);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "Local";
    }

    /** {@inheritDoc} */
    public boolean supportsDeploy() {
        return false;
    }

}
