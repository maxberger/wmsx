package hu.kfki.grid.wmsx.worker;

import hu.kfki.grid.wmsx.backends.Backend;
import hu.kfki.grid.wmsx.backends.JobUid;
import hu.kfki.grid.wmsx.backends.SubmissionResults;
import hu.kfki.grid.wmsx.job.JobState;
import hu.kfki.grid.wmsx.job.description.JDLJobDescription;
import hu.kfki.grid.wmsx.job.description.JobDescription;

import java.io.File;
import java.io.IOException;

public class WorkerBackend implements Backend {

    private int count = 0;

    private static WorkerBackend instance;

    private final ControllerImpl controllerImpl;

    private WorkerBackend() {
        this.controllerImpl = ControllerServer.getInstance()
                .getControllerImpl();
    }

    public static synchronized WorkerBackend getInstance() {
        if (WorkerBackend.instance == null) {
            WorkerBackend.instance = new WorkerBackend();
        }
        return WorkerBackend.instance;
    }

    public JobState getState(final JobUid uid) {
        return this.controllerImpl.getState(uid.getBackendId());
    }

    public boolean jobIdIsURI() {
        return false;
    }

    public void retrieveLog(final JobUid id, final File dir) {
        // TODO Auto-generated method stub

    }

    public Process retrieveResult(final JobUid id, final File dir) {
        // TODO Auto-generated method stub
        return null;
    }

    public SubmissionResults submitJdl(final String jdlFile, final String vo)
            throws IOException {
        this.count++;
        final Object id = new Integer(this.count);
        final JobDescription desc = new JDLJobDescription(jdlFile);
        this.controllerImpl.addWork(new ControllerWorkDescription(id, desc));
        return new SubmissionResults(new JobUid(this, id), null, null, null, 0,
                0);
    }

    @Override
    public String toString() {
        return "Worker";
    }

}
