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

package hu.kfki.grid.wmsx.backends;

import hu.kfki.grid.wmsx.job.JobState;
import hu.kfki.grid.wmsx.job.description.JobDescription;

import java.io.File;
import java.io.IOException;

/**
 * Generic interface for all backends.
 * 
 * @version $Revision$
 */
public interface Backend {

    /**
     * Retrieve the log file.
     * 
     * @param id
     *            Job Id.
     * @param dir
     *            directory where to store the log file to.
     */
    void retrieveLog(final JobUid id, final File dir);

    /**
     * Retrieve the results.
     * 
     * @param id
     *            Job Id.
     * @param dir
     *            directory where to store the results file to.
     * @return a process to wait for, or null
     */
    DelayedExecution retrieveResult(final JobUid id, final File dir);

    /**
     * Submit a JDL file.
     * 
     * @param job
     *            the job to submit
     * @param vo
     *            VO to submit to, or null
     * @return the {@link SubmissionResults}.
     * @throws IOException
     *             if the submission fails.
     */
    SubmissionResults submitJob(final JobDescription job, final String vo)
            throws IOException;

    /**
     * @return true if the jobId represents a URI
     */
    boolean jobIdIsURI();

    /**
     * Retrieves the current state of the job.
     * 
     * @param uid
     *            Job Id.
     * @return {@link JobState} of the job.
     */
    JobState getState(final JobUid uid);

    /**
     * @return true if this backend supports
     *         {@link hu.kfki.grid.wmsx.job.description.JobDescription#DEPLOY}.
     */
    boolean supportsDeploy();

}
