package hu.kfki.grid.wmsx.job;

import hu.kfki.grid.wmsx.backends.Backend;

import java.util.logging.Logger;

import edg.workload.userinterface.jclient.JobId;

public class LogListener implements JobListener {
    private static final Logger LOGGER = Logger.getLogger(LogListener.class
            .toString());

    private static LogListener logListener;

    private LogListener() {
    }

    static synchronized public LogListener getLogListener() {
        if (LogListener.logListener == null) {
            LogListener.logListener = new LogListener();
        }
        return LogListener.logListener;
    }

    public void done(final JobId jobId, final Backend back, final boolean success) {
        if (success) {
            LogListener.LOGGER.info("DONE/SUCCESS: " + jobId);
        } else {
            LogListener.LOGGER.info("DONE/FAILED: " + jobId);
        }
    }

    public void running(final JobId jobId, final Backend back) {
        LogListener.LOGGER.info("RUNNING: " + jobId);
    }

    public void startup(final JobId jobId, final Backend back) {
        LogListener.LOGGER.info("STARTUP: " + jobId);
    }

}
