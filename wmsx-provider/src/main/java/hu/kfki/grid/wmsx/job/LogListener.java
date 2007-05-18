package hu.kfki.grid.wmsx.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edg.workload.userinterface.jclient.JobId;

public class LogListener implements JobListener {
	private static final Log LOGGER = LogFactory.getLog(LogListener.class);

	final JobId jobId;

	public LogListener(final JobId id) {
		this.jobId = id;
	}

	public void done() {
		LogListener.LOGGER.info("DONE: " + this.jobId);
	}

	public void running() {
		LogListener.LOGGER.info("RUNNING: " + this.jobId);
	}

	public void startup() {
		LogListener.LOGGER.info("STARTUP: " + this.jobId);
	}

}
