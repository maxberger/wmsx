package hu.kfki.grid.wmsx.job.result;

import hu.kfki.grid.wmsx.backends.Backend;
import hu.kfki.grid.wmsx.job.JobListener;
import hu.kfki.grid.wmsx.provider.JdlJob;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.globus.gsi.GlobusCredentialException;

import edg.workload.userinterface.jclient.InfoLB;
import edg.workload.userinterface.jclient.Job;
import edg.workload.userinterface.jclient.JobId;
import edg.workload.userinterface.jclient.Result;

public class ResultListener implements JobListener {

    private static ResultListener resultListener;

    private static final Logger LOGGER = Logger.getLogger(ResultListener.class
            .toString());

    private final Map resultJobs = new HashMap();

    private ResultListener() {
    }

    public static synchronized ResultListener getResultListener() {
        if (ResultListener.resultListener == null) {
            ResultListener.resultListener = new ResultListener();
        }
        return ResultListener.resultListener;
    }

    public boolean setJob(final JobId id, final JdlJob job) {
        if (job != null) {
            this.resultJobs.put(id, job);
            return true;
        }
        return false;
    }

    public void done(final JobId id, final Backend back, final boolean success) {
        final JdlJob job = (JdlJob) this.resultJobs.get(id);
        if (job == null) {
            return;
        }
        try {
            final File dir = this.prepareResultDir(job);
            if (success) {
                this.retrieveResult(id, job, dir, back);
            } else {
                this.retrieveLog(id, dir);
            }
        } catch (final IOException e) {
            ResultListener.LOGGER.warning("Error accessing result directory: "
                    + job.getResultDir());
        }
    }

    private void retrieveLog(final JobId id, final File dir) {
        final Job job = new Job(id);

        Result result;
        try {
            result = job.getLogInfo();
            final String logInfo = result.toString(InfoLB.HIGH_LOG_LEVEL);
            final BufferedWriter logWriter = new BufferedWriter(new FileWriter(
                    new File(dir, "log")));
            logWriter.write(logInfo);
            logWriter.close();
        } catch (final UnsupportedOperationException e) {
            ResultListener.LOGGER.info("UnsupportedOperationException: "
                    + e.getMessage());
        } catch (final IOException e) {
            ResultListener.LOGGER.warning("IOException" + e.getMessage());
        } catch (final GlobusCredentialException e) {
            ResultListener.LOGGER.info("GlobusCredentialException"
                    + e.getMessage());
        }
    }

    private File prepareResultDir(final JdlJob job) throws IOException {
        final File dir = new File(job.getResultDir()).getCanonicalFile();
        dir.mkdirs();
        return dir;
    }

    private void retrieveResult(final JobId id, final JdlJob job,
            final File dir, final Backend backend) {
        final List commandLine = backend.jobOutputCommand(
                dir.getAbsolutePath(), id.toString());
        try {
            final Process p = Runtime.getRuntime().exec(
                    (String[]) commandLine.toArray(new String[commandLine
                            .size()]), null, dir);
            new Thread(new ResultMoverAndPostexec(p, dir, job)).start();
        } catch (final IOException e) {
            ResultListener.LOGGER.warning(e.getMessage());
        }
    }

    public void running(final JobId id, final Backend back) {
        // Empty
    }

    public void startup(final JobId id, final Backend back) {
        // Empty
    }

}
