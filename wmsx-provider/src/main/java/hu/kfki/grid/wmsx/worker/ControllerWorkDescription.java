package hu.kfki.grid.wmsx.worker;

import hu.kfki.grid.wmsx.job.description.JobDescription;
import hu.kfki.grid.wmsx.util.FileUtil;

import java.util.Map;

public class ControllerWorkDescription {

    private final WorkDescription workDescription;

    public ControllerWorkDescription(final Object uid,
            final JobDescription jobDesc) {
        final Map<String, byte[]> inputSandbox = FileUtil.createSandbox(jobDesc
                .getListEntry(JobDescription.INPUTSANDBOX), jobDesc
                .getBaseDir());
        this.workDescription = new WorkDescription(uid, inputSandbox, jobDesc
                .getListEntry(JobDescription.OUTPUTSANDBOX), jobDesc
                .getStringEntry(JobDescription.EXECUTABLE), jobDesc
                .getStringEntry(JobDescription.STDOUTPUT), jobDesc
                .getStringEntry(JobDescription.STDERROR));
    }

    public WorkDescription getWorkDescription() {
        return this.workDescription;
    }
}
