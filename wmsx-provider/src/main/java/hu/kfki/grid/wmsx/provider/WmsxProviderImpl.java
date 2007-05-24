package hu.kfki.grid.wmsx.provider;

import hu.kfki.grid.wmsx.job.JobWatcher;
import hu.kfki.grid.wmsx.job.LogListener;
import hu.kfki.grid.wmsx.job.shadow.ShadowListener;
import hu.kfki.grid.wmsx.job.submit.ParseResult;
import hu.kfki.grid.wmsx.job.submit.Submitter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.WritableByteChannel;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.logging.Logger;

import com.sun.jini.admin.DestroyAdmin;

import edg.workload.userinterface.jclient.JobId;

/**
 * My Jini Service Implementation!
 * 
 */
public class WmsxProviderImpl implements IRemoteWmsxProvider, DestroyAdmin,
		Remote {

	private static final long serialVersionUID = 2L;

	private static final Logger LOGGER = Logger
			.getLogger(WmsxProviderImpl.class.toString());

	public WmsxProviderImpl() {
		// default constructor
	}

	public String hello() {
		return "Hello, World!";
	}

	public String submitJdl(final String jdlFile, final String output) {
		WmsxProviderImpl.LOGGER.info("Submitting " + jdlFile);
		ParseResult result;
		try {
			result = Submitter.getSubmitter().submitJdl(jdlFile);
			final String jobStr = result.getJobId();
			final JobId id = new JobId(jobStr);
			WmsxProviderImpl.LOGGER.info("Job id is: " + id);
			JobWatcher.getWatcher().addWatch(id, new LogListener(id));
			final WritableByteChannel oChannel;
			if (output != null) {
				oChannel = new FileOutputStream(output).getChannel();
			} else {
				oChannel = null;
			}
			JobWatcher.getWatcher().addWatch(id,
					ShadowListener.listen(result, oChannel));
			return jobStr;
		} catch (final IOException e) {
			WmsxProviderImpl.LOGGER.warning(e.getMessage());
		} catch (final NullPointerException e) {
			WmsxProviderImpl.LOGGER.warning(e.getMessage());
		}
		return null;
	}

	public void destroy() throws RemoteException {
		JobWatcher.getWatcher().shutdown();
	}

	public void setMaxJobs(final int maxJobs) throws RemoteException {
		// TODO Auto-generated method stub

	}

}