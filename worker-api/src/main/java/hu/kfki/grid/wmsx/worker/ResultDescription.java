package hu.kfki.grid.wmsx.worker;

import java.io.Serializable;
import java.util.Map;

public class ResultDescription implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /* <String,byte[]> */
    private final Map outputSandbox;

    public ResultDescription(final Map output) {
        this.outputSandbox = output;
    }

    public Map getOutputSandbox() {
        return this.outputSandbox;
    }

}
