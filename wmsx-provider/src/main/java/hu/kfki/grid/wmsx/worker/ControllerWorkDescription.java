package hu.kfki.grid.wmsx.worker;

import hu.kfki.grid.wmsx.job.description.JobDescription;
import hu.kfki.grid.wmsx.util.FileUtil;

import java.util.List;
import java.util.Map;
import java.util.Vector;

public class ControllerWorkDescription {

    private final WorkDescription workDescription;

    public ControllerWorkDescription(final Object uid,
            final JobDescription jobDesc) {
        final Map<String, byte[]> inputSandbox = FileUtil.createSandbox(jobDesc
                .getListEntry(JobDescription.INPUTSANDBOX), jobDesc
                .getBaseDir());

        final List<String> args = new Vector<String>();
        final String aString = jobDesc.getStringEntry(JobDescription.ARGUMENTS,
                "");
        for (final String sa : aString.split(" ")) {
            final String sa2 = sa.trim();
            if (sa2.length() > 0) {
                args.add(sa2);
            }
        }

        this.workDescription = new WorkDescription(uid, inputSandbox, jobDesc
                .getListEntry(JobDescription.OUTPUTSANDBOX), jobDesc
                .getStringEntry(JobDescription.EXECUTABLE), args, jobDesc
                .getStringEntry(JobDescription.STDOUTPUT), jobDesc
                .getStringEntry(JobDescription.STDERROR));
    }

    public WorkDescription getWorkDescription() {
        return this.workDescription;
    }
}
