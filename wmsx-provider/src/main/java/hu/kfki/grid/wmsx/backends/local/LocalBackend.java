package hu.kfki.grid.wmsx.backends.local;

import hu.kfki.grid.wmsx.backends.Backend;
import hu.kfki.grid.wmsx.backends.JobUid;
import hu.kfki.grid.wmsx.backends.SubmissionResults;
import hu.kfki.grid.wmsx.job.JobState;
import hu.kfki.grid.wmsx.job.description.JDLJobDescription;
import hu.kfki.grid.wmsx.job.description.JobDescription;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

public class LocalBackend implements Backend {

    private int count = 0;

    private final Map<Object, JobState> state;

    private final Map<Object, LocalProcess> processes;

    private static LocalBackend instance;

    private LocalBackend() {
        this.state = new Hashtable<Object, JobState>();
        this.processes = new Hashtable<Object, LocalProcess>();
    };

    public static synchronized LocalBackend getInstance() {
        if (LocalBackend.instance == null) {
            LocalBackend.instance = new LocalBackend();
        }
        return LocalBackend.instance;
    }

    public JobState getState(final JobUid uid) {
        return this.state.get(uid.getBackendId());
    }

    public boolean jobIdIsURI() {
        return false;
    }

    public void retrieveLog(final JobUid id, final File dir) {
        // Ignore
    }

    public Process retrieveResult(final JobUid id, final File dir) {
        final LocalProcess lp = this.processes.get(id.getBackendId());
        // System.out.println("LP is" + lp);
        if (lp != null) {
            // System.out.println("Moving to " + dir);
            lp.retrieveOutput(dir);
            // System.out.println("Done");
        }
        return null;
    }

    public SubmissionResults submitJdl(final String jdlFile, final String vo)
            throws IOException {
        this.count++;
        final Object id = new Integer(this.count);
        final JobDescription desc = new JDLJobDescription(jdlFile);
        final LocalProcess p = new LocalProcess(this.state, id, desc);
        this.processes.put(id, p);
        new Thread(p).start();
        return new SubmissionResults(new JobUid(this, id), null, null, null, 0,
                0);
    }

    @Override
    public String toString() {
        return "Local";
    }
}
