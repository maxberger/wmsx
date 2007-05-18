package hu.kfki.grid.wmsx;

import edg.workload.userinterface.jclient.Job;
import edg.workload.userinterface.jclient.JobId;
import edg.workload.userinterface.jclient.JobStatus;
import edg.workload.userinterface.jclient.Result;

public class JobWatcher implements Runnable {

	private final JobId jobId;

	private ShadowListener listener;

	private JobWatcher(final JobId id, final ShadowListener shadowListener) {
		this.jobId = id;
		this.listener = shadowListener;

	}

	public void run() {
		// System.out.println("Started watching job " + jobId);
		try {
			boolean done = false;

			while (!done) {
				// System.out.println("Still watching");
				Thread.sleep(15000);
				if (!JobWatcher.isActive(new Job(this.jobId))) {
					done = true;
				}
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
		// System.out.println("Job is done " + jobId);
		this.listener.terminate();
		this.listener = null;
		System.gc();
	}

	public static boolean isActive(final Job job) {
		try {
			final Result result = job.getStatus(false);

			final JobStatus status = (JobStatus) result.getResult();

			final int statusInt = status.code();

			boolean startupPhase = (statusInt == JobStatus.SUBMITTED)
					|| ((statusInt == JobStatus.WAITING))
					|| (statusInt == JobStatus.READY)
					|| (statusInt == JobStatus.SCHEDULED);

			boolean active = (statusInt == JobStatus.RUNNING);

			boolean done = (statusInt == JobStatus.DONE)
					|| (statusInt == JobStatus.CLEARED)
					|| (statusInt == JobStatus.ABORTED)
					|| (statusInt == JobStatus.CANCELLED);

			if ((!startupPhase) && (!active)) {
				done = true;
			}

			// System.out
			// .println("S-A-D: " + startupPhase + " " + active + " " + done);

			return !done;
		} catch (final Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static void watch(final String jobId, final ShadowListener listener) {
		new Thread(new JobWatcher(new JobId(jobId), listener)).start();
	}

}
