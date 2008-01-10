package hu.kfki.grid.wmsx.worker;

import java.io.Serializable;

public class WorkDescription implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final String id;

    public WorkDescription(final String _id) {
        this.id = _id;
    }

    public String getId() {
        return this.id;
    }

}
