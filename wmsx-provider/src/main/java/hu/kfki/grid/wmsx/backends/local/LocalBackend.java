/*
 * WMSX - Workload Management Extensions for gLite
 * 
 * Copyright (C) 2007-2009 Max Berger
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
 */

/* $Id$ */

package hu.kfki.grid.wmsx.backends.local;

import hu.kfki.grid.wmsx.backends.Backend;
import hu.kfki.grid.wmsx.backends.DelayedExecution;
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
 * @version $Date$
 */
public final class LocalBackend implements Backend {

    /** Id of the local backend. */
    public static final String LOCAL = "local";

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
    public String jobUidToUri(final JobUid uid) {
        return null;
    }

    /** {@inheritDoc} */
    public void retrieveLog(final JobUid id, final File dir) {
        // Ignore
    }

    /** {@inheritDoc} */
    public DelayedExecution retrieveResult(final JobUid id, final File dir) {
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
        return new SubmissionResults(juid);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return LocalBackend.LOCAL;
    }

    /** {@inheritDoc} */
    public boolean supportsDeploy() {
        return false;
    }

    /** {@inheritDoc} */
    public void forgetPassword() {
        // do nothing.
    }

    /** {@inheritDoc} */
    public boolean provideCredentials(final String password, final String vo) {
        // do nothing.
        return true;
    }

    /** {@inheritDoc} */
    public boolean isAvailable() {
        return true;
    }

    /** {@inheritDoc} */
    public void cancelJob(final JobUid id) {
        final LocalProcess p = this.processes.get(id);
        if (p != null) {
            p.tryToDestroy();
        }
    }
}
