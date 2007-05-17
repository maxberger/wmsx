package hu.kfki.grid.wmsx;

import edg.workload.userinterface.jclient.Job;
import edg.workload.userinterface.jclient.JobId;
import edg.workload.userinterface.jclient.JobStatus;
import edg.workload.userinterface.jclient.Result;

public class JobWatcher implements Runnable {

	private final JobId jobId;

	private ShadowListener listener;

	private JobWatcher(JobId id, ShadowListener shadowListener) {
		jobId = id;
		listener = shadowListener;

	}

	public void run() {
		// System.out.println("Started watching job " + jobId);
		try {
			boolean done = false;

			while (!done) {
				// System.out.println("Still watching");
				Thread.sleep(15000);
				if (!isActive(new Job(jobId))) {
					done = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// System.out.println("Job is done " + jobId);
		listener.terminate();
		listener = null;
		System.gc();
	}

	public static boolean isActive(Job job) {
		try {
			Result result = job.getStatus(false);

			JobStatus status = (JobStatus) result.getResult();

			int statusInt = status.code();

			boolean startupPhase = (statusInt == JobStatus.SUBMITTED)
					|| ((statusInt == JobStatus.WAITING))
					|| (statusInt == JobStatus.READY)
					|| (statusInt == JobStatus.SCHEDULED);

			boolean active = (statusInt == JobStatus.RUNNING);

			boolean done = (statusInt == JobStatus.DONE)
					|| (statusInt == JobStatus.CLEARED)
					|| (statusInt == JobStatus.ABORTED)
					|| (statusInt == JobStatus.CANCELLED);

			if ((!startupPhase) && (!active))
				done = true;

			// System.out
			// .println("S-A-D: " + startupPhase + " " + active + " " + done);

			return !done;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static void watch(String jobId, ShadowListener listener) {
		new Thread(new JobWatcher(new JobId(jobId), listener)).start();
	}

}
