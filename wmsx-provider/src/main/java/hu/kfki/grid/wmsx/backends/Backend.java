package hu.kfki.grid.wmsx.backends;

import hu.kfki.grid.wmsx.job.JobState;

import java.io.File;
import java.io.IOException;

public interface Backend {

    void retrieveLog(final JobUid id, final File dir);

    Process retrieveResult(final JobUid id, final File dir);

    SubmissionResults submitJdl(final String jdlFile, final String vo)
            throws IOException;

    boolean jobIdIsURI();

    JobState getState(final JobUid uid);

}
