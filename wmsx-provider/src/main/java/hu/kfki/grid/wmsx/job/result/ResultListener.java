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

package hu.kfki.grid.wmsx.job.result;

import hu.kfki.grid.wmsx.backends.DelayedExecution;
import hu.kfki.grid.wmsx.backends.JobUid;
import hu.kfki.grid.wmsx.job.JobListener;
import hu.kfki.grid.wmsx.provider.JdlJob;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Listens to job status and receives the result or the error log once the job
 * is done.
 * 
 * @version $Revision$
 * 
 */
public final class ResultListener implements JobListener {

    private static ResultListener resultListener;

    private static final Logger LOGGER = Logger.getLogger(ResultListener.class
            .toString());

    private final Map<JobUid, JdlJob> resultJobs = new HashMap<JobUid, JdlJob>();

    private ResultListener() {
    }

    /**
     * @return The Singleton Instance
     */
    public static synchronized ResultListener getInstance() {
        if (ResultListener.resultListener == null) {
            ResultListener.resultListener = new ResultListener();
        }
        return ResultListener.resultListener;
    }

    /**
     * Associate the given jobid with the appropriate job description.
     * 
     * @param id
     *            JobUID
     * @param job
     *            Job Description.
     * @return true if the job description was not empty.
     */
    public boolean setJob(final JobUid id, final JdlJob job) {
        if (job != null) {
            this.resultJobs.put(id, job);
            return true;
        }
        return false;
    }

    /** {@inheritDoc} */
    public void done(final JobUid id, final boolean success) {
        final JdlJob job = this.resultJobs.get(id);
        if (job == null) {
            return;
        }
        if (job.getResultDir() == null) {
            return;
        }
        try {
            final File dir = this.prepareResultDir(job);
            if (success) {
                this.retrieveResult(id, job, dir);
            } else {
                id.getBackend().retrieveLog(id, dir);
            }
        } catch (final IOException e) {
            ResultListener.LOGGER.warning("Error accessing result directory: "
                    + job.getResultDir());
        }
    }

    private File prepareResultDir(final JdlJob job) throws IOException {
        final File dir = new File(job.getResultDir()).getCanonicalFile();
        if (!dir.mkdirs() && !dir.exists()) {
            throw new IOException(dir + " could not be created");
        }
        return dir;
    }

    private void retrieveResult(final JobUid id, final JdlJob job,
            final File dir) {
        final DelayedExecution p = id.getBackend().retrieveResult(id, dir);
        new Thread(new ResultMoverAndPostexec(p, dir, job)).start();
    }

    /** {@inheritDoc} */
    public void running(final JobUid id) {
        // Empty
    }

    /** {@inheritDoc} */
    public void startup(final JobUid id) {
        // Empty
    }

}
