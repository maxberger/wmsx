package hu.kfki.grid.wmsx.worker;

import java.io.Serializable;

public class WorkDescription implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final Object id;

    public WorkDescription(final Object _id) {
        this.id = _id;
    }

    public Object getId() {
        return this.id;
    }

}
