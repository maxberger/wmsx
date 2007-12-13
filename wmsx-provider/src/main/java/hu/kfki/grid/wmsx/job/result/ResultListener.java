package hu.kfki.grid.wmsx.job.result;

import hu.kfki.grid.wmsx.backends.JobUid;
import hu.kfki.grid.wmsx.job.JobListener;
import hu.kfki.grid.wmsx.provider.JdlJob;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

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

    public boolean setJob(final JobUid id, final JdlJob job) {
        if (job != null) {
            this.resultJobs.put(id, job);
            return true;
        }
        return false;
    }

    public void done(final JobUid id, final boolean success) {
        final JdlJob job = (JdlJob) this.resultJobs.get(id);
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
        dir.mkdirs();
        return dir;
    }

    private void retrieveResult(final JobUid id, final JdlJob job,
            final File dir) {
        final Process p = id.getBackend().retrieveResult(id, dir);
        new Thread(new ResultMoverAndPostexec(p, dir, job)).start();
    }

    public void running(final JobUid id) {
        // Empty
    }

    public void startup(final JobUid id) {
        // Empty
    }

}
