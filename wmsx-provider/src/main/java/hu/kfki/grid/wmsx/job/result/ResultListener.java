package hu.kfki.grid.wmsx.job.result;

import hu.kfki.grid.wmsx.job.JobListener;

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

    private final Map resultDirs = new HashMap();

    private ResultListener() {
    }

    public static synchronized ResultListener getResultListener() {
        if (ResultListener.resultListener == null) {
            ResultListener.resultListener = new ResultListener();
        }
        return ResultListener.resultListener;
    }

    public boolean setOutputDir(final JobId id, final String outputDir) {
        if (outputDir != null) {
            final File f = new File(outputDir).getAbsoluteFile();
            f.mkdirs();
            try {
                this.resultDirs.put(id, f.getCanonicalFile());
                return true;
            } catch (final IOException e) {
                ResultListener.LOGGER.warning(e.getMessage());
            }
        }
        return false;
    }

    public void done(final JobId id) {
        final File dir = (File) this.resultDirs.get(id);
        if (dir != null) {
            final List commandLine = new Vector();
            commandLine.add("/opt/edg/bin/edg-job-get-output");
            commandLine.add("--dir");
            commandLine.add(dir.getAbsolutePath());
            commandLine.add(id.toString());
            try {
                Runtime.getRuntime().exec(
                        (String[]) commandLine.toArray(new String[commandLine
                                .size()]), null, dir);
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
