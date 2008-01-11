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

    @Override
    public String toString() {
        return this.backend + "/" + this.realId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + (this.backend == null ? 0 : this.backend.hashCode());
        result = prime * result
                + (this.realId == null ? 0 : this.realId.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof JobUid)) {
            return false;
        }
        final JobUid other = (JobUid) obj;
        if (this.backend == null) {
            if (other.backend != null) {
                return false;
            }
        } else if (!this.backend.equals(other.backend)) {
            return false;
        }
        if (this.realId == null) {
            if (other.realId != null) {
                return false;
            }
        } else if (!this.realId.equals(other.realId)) {
            return false;
        }
        return true;
    }

}
