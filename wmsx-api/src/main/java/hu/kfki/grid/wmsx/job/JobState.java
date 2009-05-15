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

/**
 * Represents possible states of a job.
 * 
 * @version $Date: 1/1/2000$
 */
public final class JobState {

    /**
     * Startup phase. The job is committed, but not yet running.
     */
    public static final JobState STARTUP = new JobState();

    /**
     * Running phase. The job is being executed on the backend.
     */
    public static final JobState RUNNING = new JobState();

    /**
     * The job has terminated successfully.
     */
    public static final JobState SUCCESS = new JobState();

    /**
     * The job has terminated abnormally.
     */
    public static final JobState FAILED = new JobState();

    /**
     * The job state information is not available.
     */
    public static final JobState NONE = new JobState();

    private JobState() {
    };

    /** {@inheritDoc} */
    @Override
    public String toString() {
        final String result;
        if (JobState.STARTUP.equals(this)) {
            result = "STARTUP";
        } else if (JobState.RUNNING.equals(this)) {
            result = "RUNNING";
        } else if (JobState.SUCCESS.equals(this)) {
            result = "SUCCESS";
        } else if (JobState.FAILED.equals(this)) {
            result = "FAILED";
        } else if (JobState.NONE.equals(this)) {
            result = "NONE";
        } else {
            result = super.toString();
        }
        return result;
    }

}
