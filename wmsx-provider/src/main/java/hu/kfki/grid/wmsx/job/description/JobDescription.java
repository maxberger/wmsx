package hu.kfki.grid.wmsx.job.description;

import java.util.List;

public interface JobDescription {

    static final String EXECUTABLE = "Executable";

    static final String JOBTYPE = "JobType";

    static final String INPUTSANDBOX = "InputSandBox";

    static final String OUTPUTSANDBOX = "InputSandBox";

    String getStringEntry(final String key, final String defaultValue);

    String getStringEntry(final String key);

    List getListEntry(final String key);

}
