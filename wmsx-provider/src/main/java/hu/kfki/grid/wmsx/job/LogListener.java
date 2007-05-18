package hu.kfki.grid.wmsx.job;

import java.util.logging.Logger;

import edg.workload.userinterface.jclient.JobId;

public class LogListener implements JobListener {
	private static final Logger LOGGER = Logger.getLogger(LogListener.class
			.toString());

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
