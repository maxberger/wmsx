package hu.kfki.grid.wmsx.backends;

public class JobUid {
    private final Backend backend;

    private final Object realId;

    public JobUid(final Backend back, final Object id) {
        this.backend = back;
        this.realId = id;
    }

    public Backend getBackend() {
        return this.backend;
    }

    public Object getBackendId() {
        return this.realId;
    }

    public String toString() {
        return this.backend + "/" + this.realId;
    }

}
