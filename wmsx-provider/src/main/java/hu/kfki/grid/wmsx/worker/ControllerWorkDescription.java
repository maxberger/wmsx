package hu.kfki.grid.wmsx.worker;

import hu.kfki.grid.wmsx.job.description.JobDescription;

public class ControllerWorkDescription {

    private final WorkDescription workDescription;

    public ControllerWorkDescription(final Object uid,
            final JobDescription jobDesc) {
        this.workDescription = new WorkDescription(uid);
    }

    public WorkDescription getWorkDescription() {
        return this.workDescription;
    }
}
