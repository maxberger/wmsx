/*
 * WMSX - Workload Management Extensions for gLite
 * 
 * Copyright (C) 2007-2008 Max Berger
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see http://www.gnu.org/licenses/.
 */

/* $Id$ */

package hu.kfki.grid.wmsx.backends;

import hu.kfki.grid.wmsx.TransportJobUID;

/**
 * Uniquely describe a job and its backend.
 * 
 * @version $Date$
 */
public class JobUid {
    private final Backend backend;

    private final Object realId;

    /**
     * Default constructor.
     * 
     * @param back
     *            backend for this job.
     * @param id
     *            backend dependent id.
     */
    public JobUid(final Backend back, final Object id) {
        this.backend = back;
        this.realId = id;
    }

    /**
     * @return the backend for this job.
     */
    public Backend getBackend() {
        return this.backend;
    }

    /**
     * @return the backend dependent id.
     */
    public Object getBackendId() {
        return this.realId;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder(this.backend.toString());
        b.append('/');
        final String backendUri = this.backend.jobUidToUri(this);
        if (backendUri == null) {
            b.append(this.realId.toString());
        } else {
            b.append(backendUri);
        }
        return b.toString();
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
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

    public TransportJobUID toTransportJobUid() {
        return new TransportJobUID(backend.toString(), realId.toString());
    }

}
