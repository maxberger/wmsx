package hu.kfki.grid.wmsx.backends;


import java.io.File;
import java.io.IOException;

public interface Backend {

    void retrieveLog(final JobUid id, final File dir);

    Process retrieveResult(final JobUid id, final File dir);

    SubmissionResults submitJdl(final String jdlFile, final String vo)
            throws IOException;

    boolean jobIdIsURI();

    int getState(final JobUid uid);

}
