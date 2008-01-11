package hu.kfki.grid.wmsx.worker;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class WorkDescription implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final Object id;

    /* <String,byte[]> */
    private final Map inputSandbox;

    private final String executable;

    private final String stdout;

    private final String stderr;

    /* <String> */
    private final List outputSandbox;

    public WorkDescription(final Object _id, final Map input,
            final List output, final String exec, final String out,
            final String err) {
        this.id = _id;
        this.inputSandbox = input;
        this.outputSandbox = output;
        this.executable = exec;
        this.stdout = out;
        this.stderr = err;
    }

    public Object getId() {
        return this.id;
    }

    public Map getInputSandbox() {
        return this.inputSandbox;
    }

    public String getExecutable() {
        return this.executable;
    }

    public String getStdout() {
        return this.stdout;
    }

    public String getStderr() {
        return this.stderr;
    }

    public List getOutputSandbox() {
        return this.outputSandbox;
    }

}
