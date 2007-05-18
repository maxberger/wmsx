package hu.kfki.grid.wmsx.provider;

import hu.kfki.grid.wmsx.job.JobWatcher;
import hu.kfki.grid.wmsx.job.LogListener;
import hu.kfki.grid.wmsx.job.submit.ParseResult;
import hu.kfki.grid.wmsx.job.submit.Submitter;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edg.workload.userinterface.jclient.JobId;

/**
 * My Jini Service Implementation!
 * 
 */
public class WmsxProviderImpl implements IRemoteWmsxProvider {

	private static final long serialVersionUID = 2L;

	private static final Log LOGGER = LogFactory.getLog(WmsxProviderImpl.class);

	public WmsxProviderImpl() {
		// default constructor
	}

	public String hello() {
		return "Hello, World!";
	}

	public void submitJdl(final String jdlFile) {
		WmsxProviderImpl.LOGGER.info("Submitting " + jdlFile);
		ParseResult result;
		try {
			result = Submitter.getSubmitter().submitJdl(jdlFile);
			final JobId id = new JobId(result.getJobId());
			WmsxProviderImpl.LOGGER.info("Job id is: " + id);
			JobWatcher.getWatcher().addWatch(id, new LogListener(id));
		} catch (final IOException e) {
			WmsxProviderImpl.LOGGER.warn(e);
		} catch (final NullPointerException e) {
			WmsxProviderImpl.LOGGER.warn(e);
		}
	}

}