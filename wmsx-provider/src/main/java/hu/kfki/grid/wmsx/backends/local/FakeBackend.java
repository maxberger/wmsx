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
import java.util.HashMap;
import java.util.Map;

/**
 * Fake backend used for testing.
 * 
 * @version $Date$
 */
public final class FakeBackend implements Backend {

    private int count;

    private final Map<Object, JobState> state;

    /**
     * Default constructor.
     */
    public FakeBackend() {
        this.state = new HashMap<Object, JobState>();
    };

    /** {@inheritDoc} */
    public JobState getState(final JobUid uid) {
        final JobState newState;
        final Object key = uid.getBackendId();
        final JobState nowState = this.state.get(key);
        if (nowState == null || JobState.NONE.equals(nowState)) {
            newState = JobState.STARTUP;
        } else if (JobState.STARTUP.equals(nowState)) {
            newState = JobState.RUNNING;
        } else if (JobState.RUNNING.equals(nowState)) {
            newState = JobState.SUCCESS;
        } else {
            newState = nowState;
        }
        this.state.put(key, newState);
        return newState;
    }

    /** {@inheritDoc} */
    public String jobUidToUri(final JobUid uid) {
        return null;
    }

    /** {@inheritDoc} */
    public void retrieveLog(final JobUid id, final File dir) {
        // Do nothing
    }

    /** {@inheritDoc} */
    public DelayedExecution retrieveResult(final JobUid id, final File dir) {
        // Do nothing
        return null;
    }

    /** {@inheritDoc} */
    public SubmissionResults submitJob(final JobDescription job, final String vo)
            throws IOException {
        this.count++;
        final Integer in = Integer.valueOf(this.count);
        this.state.put(in, JobState.NONE);
        return new SubmissionResults(new JobUid(this, in));
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "Fake";
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
        this.state.put(id, JobState.FAILED);
    }

    /** {@inheritDoc} */
    public JobUid getJobUidForBackendId(final String backendIdString) {
        return BackendWithCounterUtils.getIntegerJobUid(this, backendIdString);
    }
}
