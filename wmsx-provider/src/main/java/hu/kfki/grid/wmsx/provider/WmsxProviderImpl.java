package hu.kfki.grid.wmsx.provider;

import hu.kfki.grid.wmsx.job.JobListener;
import hu.kfki.grid.wmsx.job.JobWatcher;
import hu.kfki.grid.wmsx.job.LogListener;
import hu.kfki.grid.wmsx.job.shadow.ShadowListener;
import hu.kfki.grid.wmsx.job.submit.ParseResult;
import hu.kfki.grid.wmsx.job.submit.Submitter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.WritableByteChannel;
import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import com.sun.jini.admin.DestroyAdmin;

import edg.workload.userinterface.jclient.JobId;

/**
 * My Jini Service Implementation!
 * 
 */
public class WmsxProviderImpl implements IRemoteWmsxProvider, RemoteDestroy,
		JobListener {

	private static final long serialVersionUID = 2L;

	private static final Logger LOGGER = Logger
			.getLogger(WmsxProviderImpl.class.toString());

	private final DestroyAdmin destroyAdmin;

	private int maxJobs = Integer.MAX_VALUE;

	private List pendingJobs = new LinkedList();

	public WmsxProviderImpl(DestroyAdmin dadm) {
		this.destroyAdmin = dadm;
	}

	public String hello() {
		return "Hello, World!";
	}

	static class JobDesc {
		final String jdlFile;

		final String output;

		public JobDesc(final String jdlFile, final String output) {
			this.jdlFile = jdlFile;
			this.output = output;
		}

		public String getJdlFile() {
			return jdlFile;
		}

		public String getOutput() {
			return output;
		}
	}

	synchronized public String submitJdl(final String jdlFile,
			final String output) {
		final int current = JobWatcher.getWatcher().getNumJobsRunning();
		int avail = (this.maxJobs - current);
		if (avail > 0) {
			return reallySubmitJdl(jdlFile, output);
		} else {
			pendingJobs.add(new JobDesc(jdlFile, output));
			return "pending";
		}
	}

	private String reallySubmitJdl(final String jdlFile, final String output) {
		WmsxProviderImpl.LOGGER.info("Submitting " + jdlFile);
		ParseResult result;
		try {
			result = Submitter.getSubmitter().submitJdl(jdlFile);
			final String jobStr = result.getJobId();
			final JobId id = new JobId(jobStr);
			WmsxProviderImpl.LOGGER.info("Job id is: " + id);
			JobWatcher.getWatcher().addWatch(id, new LogListener(id));
			JobWatcher.getWatcher().addWatch(id, this);
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
		new Thread(new Runnable() {

			public void run() {
				try {
					JobWatcher.getWatcher().shutdown();
					Thread.sleep(1000);
					destroyAdmin.destroy();
				} catch (RemoteException e) {
					// ignore
				} catch (InterruptedException e) {
					// ignore
				}
			}
		}).start();
	}

	public void setMaxJobs(final int maxj) throws RemoteException {
		WmsxProviderImpl.LOGGER.info("setMaxJobs to " + maxj);
		this.maxJobs = maxj;
		this.investigateNumJobs();
	}

	private synchronized void investigateNumJobs() {
		while ((!pendingJobs.isEmpty())
				&& ((this.maxJobs - JobWatcher.getWatcher().getNumJobsRunning()) > 0)) {
			JobDesc jd = (JobDesc) pendingJobs.remove(0);
			this.reallySubmitJdl(jd.getJdlFile(), jd.getOutput());
			try {
				this.wait(100);
			} catch (InterruptedException e) {
				// Ignore
			}
		}
	}

	public void done() {
		this.investigateNumJobs();
	}

	public void running() {
		// ignore
	}

	public void startup() {
		// ignore
	}

}