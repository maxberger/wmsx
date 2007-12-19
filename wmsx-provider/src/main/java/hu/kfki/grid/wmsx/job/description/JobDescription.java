package hu.kfki.grid.wmsx.job.description;

import java.util.List;

public interface JobDescription {

    static final String EXECUTABLE = "Executable";

    static final String JOBTYPE = "JobType";

    static final String INPUTSANDBOX = "InputSandBox";

    static final String OUTPUTSANDBOX = "OutputSandBox";

    static final String ARGUMENTS = "Arguments";

    static final String STDOUTPUT = "StdOutput";

    static final String STDERROR = "StdError";

    static final String RESULTDIR = "ResultDir";

    static final String POSTEXEC = "PostExec";

    static final String CHAIN = "Chain";

    String getStringEntry(final String key, final String defaultValue);

    String getStringEntry(final String key);

    List<String> getListEntry(final String key);

    String toJDL();

    void removeEntry(String entry);

    void replaceEntry(String entry, String value);

}
