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

package hu.kfki.grid.wmsx.backends.lcg;

import hu.kfki.grid.wmsx.backends.Backend;
import hu.kfki.grid.wmsx.backends.DelayedExecution;
import hu.kfki.grid.wmsx.backends.JobUid;
import hu.kfki.grid.wmsx.backends.ProcessDelayedExecution;
import hu.kfki.grid.wmsx.backends.SubmissionResults;
import hu.kfki.grid.wmsx.job.JobState;
import hu.kfki.grid.wmsx.job.description.JobDescription;
import hu.kfki.grid.wmsx.renewer.Renewer;
import hu.kfki.grid.wmsx.renewer.RenewerUtil;
import hu.kfki.grid.wmsx.renewer.VOMS;
import hu.kfki.grid.wmsx.util.ProcessHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.logging.Logger;

import at.ac.uibk.dps.wmsx.backends.lcg.LcgCommon;

/**
 * Common backend for all LCG (EDG and gLite) targets.
 * 
 * @version $Date$
 */
public abstract class AbstractLCGBackend implements Backend {

    /**
     * Parameter for non-interactive.
     */
    protected static final String NOINT = "--noint";

    private static final Logger LOGGER = Logger
            .getLogger(AbstractLCGBackend.class.toString());

    private static Renewer lcgRenewer;

    /** {@inheritDoc} */
    public void retrieveLog(final JobUid id, final File dir) {
        try {
            final String jobId = (String) id.getBackendId();
            if (!dir.exists() && !dir.mkdirs()) {
                throw new IOException("Failed to create: " + dir);
            }
            final File logFile = new File(dir, "log");
            final List<String> commandLine = this.retreiveLogCommand(jobId,
                    logFile.getAbsolutePath());
            final Process p = Runtime.getRuntime().exec(
                    commandLine.toArray(new String[commandLine.size()]), null,
                    dir);
            ProcessHelper.cleanupProcess(p);
        } catch (final IOException e) {
            AbstractLCGBackend.LOGGER.warning(e.getMessage());
        }

    }

    /** {@inheritDoc} */
    public void cancelJob(final JobUid id) {
        try {
            final String jobId = (String) id.getBackendId();
            final List<String> commandLine = this.cancelJobCommand(jobId);
            final Process p = Runtime.getRuntime().exec(
                    commandLine.toArray(new String[commandLine.size()]));
            ProcessHelper.cleanupProcess(p);
        } catch (final IOException e) {
            AbstractLCGBackend.LOGGER.warning(e.getMessage());
        }

    }

    /**
     * Command line to run to retrieve the job output.
     * 
     * @param absolutePath
     *            target directory. A subdir will be created here.
     * @param string
     *            jobId as string.
     * @return List of commands to execute.
     */
    protected abstract List<String> jobOutputCommand(String absolutePath,
            String string);

    /**
     * Command line to run submit a jdl file.
     * 
     * @param jdlFile
     *            jdl to submit
     * @param vo
     *            vo parameter or null.
     * @return List of commands to execute.
     */
    protected abstract List<String> submitJdlCommand(String jdlFile, String vo);

    /**
     * Command line to run to retrieve the log file.
     * 
     * @param filename
     *            where to store the log to.
     * @param jobId
     *            jobId as string.
     * @return List of commands to execute.
     */
    protected abstract List<String> retreiveLogCommand(String jobId,
            String filename);

    /**
     * Command line to cancel a job.
     * 
     * @param jobId
     *            jobId as string.
     * @return List of commands to execute.
     */
    protected abstract List<String> cancelJobCommand(String jobId);

    /**
     * Command line to get the status output.
     * 
     * @param jobId
     *            jobId as String
     * @return List of commands to execute.
     */
    protected abstract List<String> getStatusCommand(String jobId);

    /**
     * @return true if this type contains error streams for interactive jobs.
     */
    protected abstract boolean needsError();

    /** {@inheritDoc} */
    public DelayedExecution retrieveResult(final JobUid id, final File dir) {
        try {
            final List<String> commandLine = this.jobOutputCommand(dir
                    .getAbsolutePath(), id.getBackendId().toString());
            final Process p = Runtime.getRuntime().exec(
                    commandLine.toArray(new String[commandLine.size()]), null,
                    dir);
            return new ProcessDelayedExecution(p);
        } catch (final IOException e) {
            AbstractLCGBackend.LOGGER.warning(e.getMessage());
            return null;
        }

    }

    /** {@inheritDoc} */
    public SubmissionResults submitJob(final JobDescription jobDesc,
            final String vo) throws IOException {
        final String jdlFile = jobDesc.toJdl().getAbsolutePath();
        final List<String> commandLine = this.submitJdlCommand(jdlFile, vo);
        final Process p = Runtime.getRuntime().exec(
                commandLine.toArray(new String[commandLine.size()]), null,
                new File(jdlFile).getParentFile());
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final PrintStream parserOutput = new PrintStream(baos);
        // final PrintStream parserOutput = System.out;
        final boolean interactive = JobDescription.INTERACTIVE
                .equalsIgnoreCase(jobDesc.getStringEntry(
                        JobDescription.JOBTYPE, JobDescription.NORMAL));
        final SubmissionResults result = InputParser.parseSubmission(p
                .getInputStream(), parserOutput, this, interactive, this
                .needsError());
        AbstractLCGBackend.LOGGER.finer("Submission Results: " + result);
        ProcessHelper.cleanupProcess(p);
        if (result == null) {
            AbstractLCGBackend.LOGGER.warning("Failed to submit Job.");
            AbstractLCGBackend.LOGGER.info(baos.toString());
            return null;
        } else {
            AbstractLCGBackend.LOGGER.fine(baos.toString());
        }
        return result;
    }

    /** {@inheritDoc} */
    public String jobUidToUri(final JobUid uid) {
        return uid.getBackendId().toString();
    }

    /** {@inheritDoc} */
    public JobState getState(final JobUid uid) {

        JobState retVal = JobState.NONE;
        final List<String> commandLine = this.getStatusCommand(uid
                .getBackendId().toString());
        try {
            final Process p = Runtime.getRuntime().exec(
                    commandLine.toArray(new String[commandLine.size()]));
            retVal = InputParser.parseStatus(p.getInputStream());
            ProcessHelper.cleanupProcess(p);
        } catch (final IOException io) {
            AbstractLCGBackend.LOGGER.warning(io.getMessage());
        }
        return retVal;
    }

    /** {@inheritDoc} */
    public boolean supportsDeploy() {
        return false;
    }

    /** {@inheritDoc} */
    public void forgetPassword() {
        synchronized (AbstractLCGBackend.class) {
            if (AbstractLCGBackend.lcgRenewer != null) {
                AbstractLCGBackend.LOGGER.info("Forgetting Grid Password");
                AbstractLCGBackend.lcgRenewer.shutdown();
                AbstractLCGBackend.lcgRenewer = null;
            }
        }
    }

    /** {@inheritDoc} */
    public boolean provideCredentials(final String pass, final String vo) {
        synchronized (AbstractLCGBackend.class) {
            AbstractLCGBackend.LOGGER.info("New Grid Password Rememberer");
            this.forgetPassword();
            AbstractLCGBackend.lcgRenewer = new VOMS(pass, vo);
            final boolean success = RenewerUtil
                    .startupRenewer(AbstractLCGBackend.lcgRenewer);
            if (!success) {
                AbstractLCGBackend.LOGGER.info("Grid Password failed");
                AbstractLCGBackend.lcgRenewer = null;
            }
            return success;
        }
    }

    /** {@inheritDoc} */
    public boolean isAvailable() {
        final List<String> submitCmds = this.submitJdlCommand("", "");
        return LcgCommon.isAvailable(submitCmds.get(1));
    }

    /** {@inheritDoc} */
    public JobUid getJobUidForBackendId(final String backendIdString) {
        return new JobUid(this, backendIdString);
    }

}
