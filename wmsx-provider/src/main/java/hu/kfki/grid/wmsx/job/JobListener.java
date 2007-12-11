package hu.kfki.grid.wmsx.job;

import hu.kfki.grid.wmsx.backends.Backend;
import edg.workload.userinterface.jclient.JobId;

public interface JobListener {

    void startup(JobId id, Backend backend);

    void running(JobId id, Backend backend);

    void done(JobId id, Backend backend, boolean success);
}
