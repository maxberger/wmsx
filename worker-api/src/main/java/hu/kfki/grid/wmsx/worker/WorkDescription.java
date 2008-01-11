package hu.kfki.grid.wmsx.worker;

import java.io.Serializable;
import java.util.Map;

public class WorkDescription implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final Object id;

    /* <String,byte[]> */
    private final Map inputSandbox;

    public WorkDescription(final Object _id, final Map input) {
        this.id = _id;
        this.inputSandbox = input;
    }

    public Object getId() {
        return this.id;
    }

    public Map getInputSandbox() {
        return this.inputSandbox;
    }

}
