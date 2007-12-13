package hu.kfki.grid.wmsx.backends.lcg;

import hu.kfki.grid.wmsx.backends.Backend;
import hu.kfki.grid.wmsx.backends.JobUid;
import hu.kfki.grid.wmsx.backends.SubmissionResults;
import hu.kfki.grid.wmsx.job.JobState;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.logging.Logger;

import org.globus.gsi.GlobusCredentialException;

import edg.workload.userinterface.jclient.InfoLB;
import edg.workload.userinterface.jclient.Job;
import edg.workload.userinterface.jclient.JobId;
import edg.workload.userinterface.jclient.JobStatus;
import edg.workload.userinterface.jclient.Result;

public abstract class AbstractLCGBackend implements Backend {

    private static final Logger LOGGER = Logger
            .getLogger(AbstractLCGBackend.class.toString());

    public void retrieveLog(final JobUid id, final File dir) {
        final Job job = new Job((JobId) id.getBackendId());

        Result result;
        try {
            result = job.getLogInfo();
            final String logInfo = result.toString(InfoLB.HIGH_LOG_LEVEL);
            final BufferedWriter logWriter = new BufferedWriter(new FileWriter(
                    new File(dir, "log")));
            logWriter.write(logInfo);
            logWriter.close();
        } catch (final UnsupportedOperationException e) {
            AbstractLCGBackend.LOGGER.info("UnsupportedOperationException: "
                    + e.getMessage());
        } catch (final IOException e) {
            AbstractLCGBackend.LOGGER.warning("IOException" + e.getMessage());
        } catch (final GlobusCredentialException e) {
            AbstractLCGBackend.LOGGER.info("GlobusCredentialException"
                    + e.getMessage());
        }
    }

    abstract protected List jobOutputCommand(String absolutePath, String string);

    abstract protected List submitJdlCommand(String jdlFile, String vo);

    public Process retrieveResult(final JobUid id, final File dir) {
        try {
            final List commandLine = this.jobOutputCommand(dir
                    .getAbsolutePath(), id.toString());
            final Process p = Runtime.getRuntime().exec(
                    (String[]) commandLine.toArray(new String[commandLine
                            .size()]), null, dir);
            return p;
        } catch (final IOException e) {
            AbstractLCGBackend.LOGGER.warning(e.getMessage());
            return null;
        }

    }

    public SubmissionResults submitJdl(final String jdlFile, final String vo)
            throws IOException {
        final List commandLine = this.submitJdlCommand(jdlFile, vo);
        final Process p = Runtime.getRuntime().exec(
                (String[]) commandLine.toArray(new String[commandLine.size()]),
                null, new File(jdlFile).getParentFile());
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final PrintStream parserOutput = new PrintStream(baos);
        // final PrintStream parserOutput = System.out;
        final SubmissionResults result = InputParser.parse(p.getInputStream(),
                parserOutput);

        if (result == null) {
            AbstractLCGBackend.LOGGER.warning("Failed to submit Job.");
            AbstractLCGBackend.LOGGER.info(baos.toString());
            return null;
        } else {
            AbstractLCGBackend.LOGGER.fine(baos.toString());
        }
        return result;
    }

    public boolean jobIdIsURI() {
        return true;
    }

    public JobState getState(final JobUid uid) {
        final Job job = new Job((JobId) uid.getBackendId());
        JobState retVal = JobState.FAILED;
        try {

            final Result result = job.getStatus(false);

            final JobStatus status = (JobStatus) result.getResult();

            final int statusInt = status.code();

            final boolean startupPhase = statusInt == JobStatus.SUBMITTED
                    || statusInt == JobStatus.WAITING
                    || statusInt == JobStatus.READY
                    || statusInt == JobStatus.SCHEDULED;

            final boolean active = statusInt == JobStatus.RUNNING;

            // boolean done = (statusInt == JobStatus.DONE)
            // || (statusInt == JobStatus.CLEARED)
            // || (statusInt == JobStatus.ABORTED)
            // || (statusInt == JobStatus.CANCELLED);
            final boolean success = statusInt == JobStatus.DONE;

            if (startupPhase) {
                retVal = JobState.STARTUP;
            } else if (active) {
                retVal = JobState.RUNNING;
            } else if (success) {
                retVal = JobState.SUCCESS;
            }

        } catch (final Exception e) {
            AbstractLCGBackend.LOGGER.warning(e.getMessage());
            retVal = JobState.FAILED;
        }
        return retVal;
    }
}
