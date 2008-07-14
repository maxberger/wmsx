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
import java.util.HashMap;
import java.util.Map;

/**
 * Fake backend used for testing.
 * 
 * @version $Revision$
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
        JobState newState = JobState.SUCCESS;
        final Object key = uid.getBackendId();
        final JobState nowState = this.state.get(key);
        if (JobState.NONE.equals(nowState)) {
            newState = JobState.STARTUP;
        } else if (JobState.STARTUP.equals(nowState)) {
            newState = JobState.RUNNING;
        }
        this.state.put(key, newState);
        return newState;
    }

    /** {@inheritDoc} */
    public boolean jobIdIsURI() {
        return false;
    }

    /** {@inheritDoc} */
    public void retrieveLog(final JobUid id, final File dir) {
        // Do nothing
    }

    /** {@inheritDoc} */
    public Process retrieveResult(final JobUid id, final File dir) {
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
}
