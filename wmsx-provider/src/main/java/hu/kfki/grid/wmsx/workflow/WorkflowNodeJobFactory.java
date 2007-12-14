package hu.kfki.grid.wmsx.workflow;

import hu.kfki.grid.wmsx.provider.JdlJob;
import hu.kfki.grid.wmsx.provider.JobFactory;

import java.io.File;

public class WorkflowNodeJobFactory implements JobFactory {

    private final Workflow workflow;

    private final String name;

    public WorkflowNodeJobFactory(final Workflow workflo, final String node) {
        this.workflow = workflo;
        this.name = node;
    }

    public JdlJob createJdlJob() {
        final File workdir = this.workflow.getDirectory();
        return new JdlJob(new File(workdir, this.name).getAbsolutePath(), null,
                workdir.getAbsolutePath(), this.workflow);
    }
}
