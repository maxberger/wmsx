package hu.kfki.grid.wmsx.job;

import hu.kfki.grid.wmsx.backends.JobUid;

public interface JobListener {

    void startup(JobUid id);

    void running(JobUid id);

    void done(JobUid id, boolean success);
}
