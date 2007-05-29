package hu.kfki.grid.wmsx.job;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import edg.workload.userinterface.jclient.Job;
import edg.workload.userinterface.jclient.JobId;
import edg.workload.userinterface.jclient.JobStatus;
import edg.workload.userinterface.jclient.Result;

public class JobWatcher implements Runnable {
	private static final Logger LOGGER = Logger.getLogger(JobWatcher.class
			.toString());

	private final Map joblisteners = new HashMap();

	private final Map jobstate = new HashMap();

	private Thread runThread;

	private static JobWatcher jobWatcher;

	private static final int STATE_STARTUP = 1;

	private static final int STATE_RUNNING = 2;

	private static final int STATE_DONE = 3;

	private static final int STATE_NONE = 0;

	private boolean shutdown;

	private JobWatcher() {
		this.runThread = null;
		this.shutdown = false;
	}

	private synchronized void doStart() {
		if (this.runThread == null) {
			JobWatcher.LOGGER.info("Starting new Listener");
			this.runThread = new Thread(this);
			this.runThread.start();
		}
	}

	public static synchronized JobWatcher getWatcher() {
		if (JobWatcher.jobWatcher == null) {
			JobWatcher.jobWatcher = new JobWatcher();
		}
		return JobWatcher.jobWatcher;
	}

	public synchronized void addWatch(final JobId jobId,
			final JobListener listener) {
		if (!this.shutdown) {
			Set listeners = (Set) this.joblisteners.get(jobId);
			if (listeners == null) {
				listeners = new HashSet();
				this.joblisteners.put(jobId, listeners);
			}
			listeners.add(listener);
			this.doStart();
		}
	}

	public synchronized void shutdown() {
		this.shutdown = true;
		if (this.runThread != null) {
			this.runThread.interrupt();
		}
	}

	public void run() {

		boolean done = false;

		while (!done) {
			try {
				Thread.sleep(10000);
			} catch (final InterruptedException e) {
				JobWatcher.LOGGER.fine(e.getMessage());
			}

			Set jobs;
			synchronized (this) {
				jobs = new HashSet(this.joblisteners.keySet());
			}

			final Iterator it = jobs.iterator();
			while (it.hasNext()) {
				final JobId jobId = (JobId) it.next();

				final int stateNow;
				if (!this.shutdown) {
					stateNow = JobWatcher.getState(new Job(jobId));
				} else {
					stateNow = JobWatcher.STATE_DONE;
				}
				boolean differs;
				synchronized (this.jobstate) {
					Integer oldState = (Integer) this.jobstate.get(jobId);
					if (oldState == null) {
						oldState = new Integer(JobWatcher.STATE_NONE);
					}
					differs = oldState.intValue() != stateNow;
					if (differs) {
						this.jobstate.put(jobId, new Integer(stateNow));
					}
				}

				if (differs) {
					Set listeners;
					synchronized (this) {
						listeners = new HashSet((Set) this.joblisteners
								.get(jobId));
						if (stateNow == JobWatcher.STATE_DONE) {
							this.joblisteners.remove(jobId);
						}
					}

					final Iterator li = listeners.iterator();
					while (li.hasNext()) {
						final JobListener listener = (JobListener) li.next();
						switch (stateNow) {
						case STATE_STARTUP:
							listener.startup();
							break;
						case STATE_RUNNING:
							listener.running();
							break;
						case STATE_DONE:
							listener.done();
							break;
						}
					}
				}
			}
			synchronized (this) {
				if (this.joblisteners.isEmpty()) {
					done = true;
					this.runThread = null;
					JobWatcher.LOGGER.info("No more jobs to listen to.");
				}
			}
		}
		System.gc();
	}

	private static int getState(final Job job) {
		int retVal = JobWatcher.STATE_DONE;
		try {

			final Result result = job.getStatus(false);

			final JobStatus status = (JobStatus) result.getResult();

			final int statusInt = status.code();

			final boolean startupPhase = (statusInt == JobStatus.SUBMITTED)
					|| ((statusInt == JobStatus.WAITING))
					|| (statusInt == JobStatus.READY)
					|| (statusInt == JobStatus.SCHEDULED);

			final boolean active = (statusInt == JobStatus.RUNNING);

			// boolean done = (statusInt == JobStatus.DONE)
			// || (statusInt == JobStatus.CLEARED)
			// || (statusInt == JobStatus.ABORTED)
			// || (statusInt == JobStatus.CANCELLED);

			if (startupPhase) {
				retVal = JobWatcher.STATE_STARTUP;
			} else if (active) {
				retVal = JobWatcher.STATE_RUNNING;
			}

		} catch (final Exception e) {
			JobWatcher.LOGGER.warning(e.getMessage());
			retVal = JobWatcher.STATE_DONE;
		}
		return retVal;
	}

	public synchronized int getNumJobsRunning() {
		return joblisteners.size();
	}

}
