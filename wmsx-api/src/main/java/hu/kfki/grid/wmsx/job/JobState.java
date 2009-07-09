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

package hu.kfki.grid.wmsx.job;

import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * Represents possible states of a job.
 * 
 * @version $Date$
 */
public final class JobState implements Serializable {

    /**
     * Startup phase. The job is committed, but not yet running.
     */
    public static final JobState STARTUP = new JobState(JobState.STR_STARTUP);

    /**
     * Running phase. The job is being executed on the backend.
     */
    public static final JobState RUNNING = new JobState(JobState.STR_RUNNING);

    /**
     * The job has terminated successfully.
     */
    public static final JobState SUCCESS = new JobState(JobState.STR_SUCCESS);

    /**
     * The job has terminated abnormally.
     */
    public static final JobState FAILED = new JobState(JobState.STR_FAILED);

    /**
     * The job state information is not available.
     */
    public static final JobState NONE = new JobState(JobState.STR_NONE);

    private static final long serialVersionUID = 1L;

    private static final String STR_STARTUP = "STARTUP";

    private static final String STR_RUNNING = "RUNNING";

    private static final String STR_SUCCESS = "SUCCESS";

    private static final String STR_FAILED = "FAILED";

    private static final String STR_NONE = "NONE";

    private final String value;

    private JobState(final String val) {
        this.value = val;
    };

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return this.value;
    }

    private Object readResolve() throws ObjectStreamException {
        final Object retVal;
        if (this.value.equals(JobState.STR_STARTUP)) {
            retVal = JobState.STARTUP;
        } else if (this.value.equals(JobState.STR_RUNNING)) {
            retVal = JobState.RUNNING;
        } else if (this.value.equals(JobState.STR_SUCCESS)) {
            retVal = JobState.SUCCESS;
        } else if (this.value.equals(JobState.STR_FAILED)) {
            retVal = JobState.FAILED;
        } else {
            retVal = JobState.NONE;
        }
        return retVal;
    }

}
