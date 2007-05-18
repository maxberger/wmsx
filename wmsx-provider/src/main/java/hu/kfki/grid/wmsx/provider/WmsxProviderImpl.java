package hu.kfki.grid.wmsx.provider;

import hu.kfki.grid.wmsx.job.JobWatcher;
import hu.kfki.grid.wmsx.job.LogListener;
import hu.kfki.grid.wmsx.job.submit.ParseResult;
import hu.kfki.grid.wmsx.job.submit.Submitter;

import java.io.IOException;
import java.util.logging.Logger;

import edg.workload.userinterface.jclient.JobId;

/**
 * My Jini Service Implementation!
 * 
 */
public class WmsxProviderImpl implements IRemoteWmsxProvider {

	private static final long serialVersionUID = 2L;

	private static final Logger LOGGER = Logger
			.getLogger(WmsxProviderImpl.class.toString());

	public WmsxProviderImpl() {
		// default constructor
	}

	public String hello() {
		return "Hello, World!";
	}

	public String submitJdl(final String jdlFile) {
		WmsxProviderImpl.LOGGER.info("Submitting " + jdlFile);
		ParseResult result;
		try {
			result = Submitter.getSubmitter().submitJdl(jdlFile);
			final String jobStr = result.getJobId();
			final JobId id = new JobId(jobStr);
			WmsxProviderImpl.LOGGER.info("Job id is: " + id);
			JobWatcher.getWatcher().addWatch(id, new LogListener(id));
			return jobStr;
		} catch (final IOException e) {
			WmsxProviderImpl.LOGGER.warning(e.getMessage());
		} catch (final NullPointerException e) {
			WmsxProviderImpl.LOGGER.warning(e.getMessage());
		}
		return null;
	}

}