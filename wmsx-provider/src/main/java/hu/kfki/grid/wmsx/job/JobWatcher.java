package hu.kfki.grid.wmsx.job;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edg.workload.userinterface.jclient.Job;
import edg.workload.userinterface.jclient.JobId;
import edg.workload.userinterface.jclient.JobStatus;
import edg.workload.userinterface.jclient.Result;

public class JobWatcher implements Runnable {
	private static final Log LOGGER = LogFactory.getLog(JobWatcher.class);

	private final Map joblisteners = new HashMap();

	private final Map jobstate = new HashMap();

	private boolean isRunning;

	private static JobWatcher jobWatcher;

	private static final int STATE_STARTUP = 1;

	private static final int STATE_RUNNING = 2;

	private static final int STATE_DONE = 3;

	private static final int STATE_NONE = 0;

	private JobWatcher() {
		isRunning = false;
	}

	private synchronized void doStart() {
		if (!isRunning) {
			isRunning = true;
			new Thread(this).start();
		}
	}

	public static synchronized JobWatcher getWatcher() {
		if (jobWatcher == null) {
			jobWatcher = new JobWatcher();
		}
		return jobWatcher;
	}

	public synchronized void addWatch(JobId jobId, JobListener listener) {
		Set listeners = (Set) joblisteners.get(jobId);
		if (listeners == null) {
			listeners = new HashSet();
			joblisteners.put(jobId, listeners);
		}
		listeners.add(listener);
		this.doStart();
	}

	public void run() {

		boolean done = false;

		while (!done) {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				LOGGER.debug(e);
			}

			Set jobs;
			synchronized (this) {
				jobs = new HashSet(joblisteners.keySet());
			}

			Iterator it = jobs.iterator();
			while (it.hasNext()) {
				JobId jobId = (JobId) it.next();

				int stateNow = getState(new Job(jobId));
				boolean differs;
				synchronized (jobstate) {
					Integer oldState = (Integer) jobstate.get(jobId);
					if (oldState == null)
						oldState = new Integer(STATE_NONE);
					differs = oldState.intValue() != stateNow;
					if (differs) {
						jobstate.put(jobId, new Integer(stateNow));
					}
				}

				if (differs) {
					Set listeners;
					synchronized (this) {
						listeners = new HashSet((Set) joblisteners.get(jobId));
					}

					Iterator li = listeners.iterator();
					while (li.hasNext()) {
						JobListener listener = (JobListener) li.next();
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
					if (stateNow == STATE_DONE) {
						synchronized (this) {
							joblisteners.remove(jobId);
						}
					}
				}
			}
			synchronized (this) {
				if (joblisteners.isEmpty()) {
					done = true;
					isRunning = false;
				}
			}

		}
		System.gc();
	}

	private static int getState(Job job) {
		int retVal = STATE_DONE;
		try {

			Result result = job.getStatus(false);

			JobStatus status = (JobStatus) result.getResult();

			int statusInt = status.code();

			boolean startupPhase = (statusInt == JobStatus.SUBMITTED)
					|| ((statusInt == JobStatus.WAITING))
					|| (statusInt == JobStatus.READY)
					|| (statusInt == JobStatus.SCHEDULED);

			boolean active = (statusInt == JobStatus.RUNNING);

			// boolean done = (statusInt == JobStatus.DONE)
			// || (statusInt == JobStatus.CLEARED)
			// || (statusInt == JobStatus.ABORTED)
			// || (statusInt == JobStatus.CANCELLED);

			if (startupPhase)
				retVal = STATE_STARTUP;
			else if (active)
				retVal = STATE_RUNNING;

		} catch (Exception e) {
			LOGGER.warn(e);
			retVal = STATE_DONE;
		}
		return retVal;
	}

}
