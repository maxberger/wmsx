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

package hu.kfki.grid.wmsx.backends.lcg;

import hu.kfki.grid.wmsx.backends.Backend;
import hu.kfki.grid.wmsx.backends.DelayedExecution;
import hu.kfki.grid.wmsx.backends.JobUid;
import hu.kfki.grid.wmsx.backends.ProcessDelayedExecution;
import hu.kfki.grid.wmsx.backends.SubmissionResults;
import hu.kfki.grid.wmsx.job.JobState;
import hu.kfki.grid.wmsx.job.description.JobDescription;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.logging.Logger;

/**
 * Common backend for all LCG (EDG and gLite) targets.
 * 
 * @version $Revision$
 */
public abstract class AbstractLCGBackend implements Backend {

    /**
     * Parameter for non-interactive.
     */
    protected static final String NOINT = "--noint";

    /**
     * Absolute path to the "env" program on most unix system.
     */
    protected static final String ENV = "/usr/bin/env";

    private static final Logger LOGGER = Logger
            .getLogger(AbstractLCGBackend.class.toString());

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
            p.waitFor();
        } catch (final IOException e) {
            AbstractLCGBackend.LOGGER.warning(e.getMessage());
        } catch (final InterruptedException e) {
            // ignore
        }

        // TODO: Re-enable!
        // final Job job = new Job((JobId) id.getBackendId());
        //
        // Result result;
        // try {
        // result = job.getLogInfo();
        // final String logInfo = result.toString(InfoLB.HIGH_LOG_LEVEL);
        // final BufferedWriter logWriter = new BufferedWriter(new FileWriter(
        // new File(dir, "log")));
        // logWriter.write(logInfo);
        // logWriter.close();
        // } catch (final UnsupportedOperationException e) {
        // AbstractLCGBackend.LOGGER.info("UnsupportedOperationException: "
        // + e.getMessage());
        // } catch (final IOException e) {
        // AbstractLCGBackend.LOGGER.warning("IOException" + e.getMessage());
        // } catch (final GlobusCredentialException e) {
        // AbstractLCGBackend.LOGGER.info("GlobusCredentialException"
        // + e.getMessage());
        // }
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
     * Command line to get the status output.
     * 
     * @param jobId
     *            jobId as String
     * @return List of commands to execute.
     */
    protected abstract List<String> getStatusCommand(String jobId);

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
        final SubmissionResults result = InputParser.parseSubmission(p
                .getInputStream(), parserOutput, this);
        AbstractLCGBackend.LOGGER.finer("Submission Results: " + result);
        try {
            p.waitFor();
        } catch (final InterruptedException e) {
            AbstractLCGBackend.LOGGER.log(java.util.logging.Level.FINE,
                    "Error waiting for process", e);
        }
        try {
            p.getInputStream().close();
        } catch (final IOException io) {
            AbstractLCGBackend.LOGGER.finer(io.toString());
        }
        try {
            p.getOutputStream().close();
        } catch (final IOException io) {
            AbstractLCGBackend.LOGGER.finer(io.toString());
        }
        try {
            p.getErrorStream().close();
        } catch (final IOException io) {
            AbstractLCGBackend.LOGGER.finer(io.toString());
        }
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
            try {
                p.waitFor();
            } catch (final InterruptedException e) {
                AbstractLCGBackend.LOGGER.log(java.util.logging.Level.FINE,
                        "Error waiting for process", e);
            }
        } catch (final IOException io) {
            AbstractLCGBackend.LOGGER.warning(io.getMessage());
        }
        return retVal;

        // final Job job = new Job(new JobId(uid.getBackendId().toString()));
        // JobState retVal = JobState.FAILED;
        // try {
        //
        // final Result result = job.getStatus(false);
        //
        // final JobStatus status = (JobStatus) result.getResult();
        //
        // final int statusInt = status.code();
        //
        // final boolean startupPhase = statusInt == JobStatus.SUBMITTED
        // || statusInt == JobStatus.WAITING
        // || statusInt == JobStatus.READY
        // || statusInt == JobStatus.SCHEDULED;
        //
        // final boolean active = statusInt == JobStatus.RUNNING;
        //
        // // boolean done = (statusInt == JobStatus.DONE)
        // // || (statusInt == JobStatus.CLEARED)
        // // || (statusInt == JobStatus.ABORTED)
        // // || (statusInt == JobStatus.CANCELLED);
        // final boolean success = statusInt == JobStatus.DONE
        // || statusInt == JobStatus.CLEARED;
        //
        // if (startupPhase) {
        // retVal = JobState.STARTUP;
        // } else if (active) {
        // retVal = JobState.RUNNING;
        // } else if (success) {
        // retVal = JobState.SUCCESS;
        // }
        //
        // } catch (final IOException e) {
        // AbstractLCGBackend.LOGGER.warning(e.getMessage());
        // retVal = JobState.FAILED;
        // } catch (final GlobusCredentialException e) {
        // AbstractLCGBackend.LOGGER.warning(e.getMessage());
        // retVal = JobState.FAILED;
        // }
        // return retVal;
    }

    /** {@inheritDoc} */
    public boolean supportsDeploy() {
        return false;
    }

}
