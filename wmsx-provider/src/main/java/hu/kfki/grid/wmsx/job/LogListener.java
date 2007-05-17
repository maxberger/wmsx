package hu.kfki.grid.wmsx.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edg.workload.userinterface.jclient.JobId;

public class LogListener implements JobListener {
	private static final Log LOGGER = LogFactory.getLog(LogListener.class);

	final JobId jobId;

	public LogListener(JobId id) {
		this.jobId = id;
	}

	public void done() {
		LOGGER.info("DONE: " + jobId);
	}

	public void running() {
		LOGGER.info("RUNNING: " + jobId);
	}

	public void startup() {
		LOGGER.info("STARTUP: " + jobId);
	}

}
