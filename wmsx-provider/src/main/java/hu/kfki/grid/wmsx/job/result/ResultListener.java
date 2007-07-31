package hu.kfki.grid.wmsx.job.result;

import hu.kfki.grid.wmsx.job.JobListener;
import hu.kfki.grid.wmsx.provider.JdlJob;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import edg.workload.userinterface.jclient.JobId;

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

    public void done(final JobId id) {
        final JdlJob job = (JdlJob) this.resultJobs.get(id);
        if (job == null) {
            return;
        }
        this.retrieveResult(id, job);
    }

    private void retrieveResult(final JobId id, final JdlJob job) {
        File dir;
        try {
            dir = new File(job.getResultDir()).getCanonicalFile();
        } catch (final IOException e1) {
            dir = null;
        }
        if (dir != null) {
            dir.mkdirs();
            final List commandLine = new Vector();
            commandLine.add("/opt/edg/bin/edg-job-get-output");
            commandLine.add("--dir");
            commandLine.add(dir.getAbsolutePath());
            commandLine.add("--noint");
            commandLine.add(id.toString());
            try {
                final Process p = Runtime.getRuntime().exec(
                        (String[]) commandLine.toArray(new String[commandLine
                                .size()]), null, dir);
                new Thread(new ResultMoverAndPostexec(p, dir, job)).start();
            } catch (final IOException e) {
                ResultListener.LOGGER.warning(e.getMessage());
            }
        }
    }

    public void running(final JobId id) {
        // Empty
    }

    public void startup(final JobId id) {
        // Empty
    }

}
