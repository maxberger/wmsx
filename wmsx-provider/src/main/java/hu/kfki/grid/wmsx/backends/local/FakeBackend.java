package hu.kfki.grid.wmsx.backends.local;

import hu.kfki.grid.wmsx.backends.Backend;
import hu.kfki.grid.wmsx.backends.JobUid;
import hu.kfki.grid.wmsx.backends.SubmissionResults;
import hu.kfki.grid.wmsx.job.JobWatcher;

import java.io.File;
import java.io.IOException;

public class FakeBackend implements Backend {

    private static FakeBackend instance;

    private int count = 0;

    private FakeBackend() {
    };

    public static synchronized FakeBackend getInstance() {
        if (FakeBackend.instance == null) {
            FakeBackend.instance = new FakeBackend();
        }
        return FakeBackend.instance;
    }

    public int getState(final JobUid uid) {
        return JobWatcher.STATE_SUCCESS;
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
        return new SubmissionResults(new JobUid(this, new Integer(this.count)),
                null, null, null, 0, 0);
    }

    public String toString() {
        return "Fake";
    }
}
