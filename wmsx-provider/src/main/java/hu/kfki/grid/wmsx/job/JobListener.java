package hu.kfki.grid.wmsx.job;

import edg.workload.userinterface.jclient.JobId;

public interface JobListener {

    void startup(JobId id);

    void running(JobId id);

    void done(JobId id, boolean success);
}
