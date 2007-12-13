package hu.kfki.grid.wmsx.backends.local;

import hu.kfki.grid.wmsx.backends.Backend;
import hu.kfki.grid.wmsx.backends.JobUid;
import hu.kfki.grid.wmsx.backends.SubmissionResults;
import hu.kfki.grid.wmsx.job.JobState;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FakeBackend implements Backend {

    private static FakeBackend instance;

    private int count = 0;

    /** <Integer,JobState> */
    private final Map state;

    private FakeBackend() {
        this.state = new HashMap();
    };

    public static synchronized FakeBackend getInstance() {
        if (FakeBackend.instance == null) {
            FakeBackend.instance = new FakeBackend();
        }
        return FakeBackend.instance;
    }

    public JobState getState(final JobUid uid) {
        JobState newState = JobState.SUCCESS;
        final Object key = uid.getBackendId();
        final JobState nowState = (JobState) this.state.get(key);
        if (JobState.NONE.equals(nowState)) {
            newState = JobState.STARTUP;
        } else if (JobState.STARTUP.equals(nowState)) {
            newState = JobState.RUNNING;
        }
        this.state.put(key, newState);
        return newState;
    }

    public boolean jobIdIsURI() {
        return false;
    }

    public void retrieveLog(final JobUid id, final File dir) {
        // Do nothing
    }

    public Process retrieveResult(final JobUid id, final File dir) {
        // Do nothing
        return null;
    }

    public SubmissionResults submitJdl(final String jdlFile, final String vo)
            throws IOException {
        this.count++;
        final Integer in = new Integer(this.count);
        this.state.put(in, JobState.NONE);
        return new SubmissionResults(new JobUid(this, in), null, null, null, 0,
                0);
    }

    public String toString() {
        return "Fake";
    }
}
