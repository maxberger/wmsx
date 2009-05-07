/*
 * WMSX - Workload Management Extensions for gLite
 * 
 * Copyright (C) 2007-2009 Max Berger
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

package hu.kfki.grid.wmsx;

import java.io.Serializable;

/**
 * A transportable (String) JobUID.
 * 
 * @version $Date: 1/1/2000$
 */
public class TransportJobUID implements Serializable {

    private static final long serialVersionUID = 1L;
    private final String backend;
    private final String localId;

    /**
     * Default constructor.
     * 
     * @param back
     *            Name of the backend.
     * @param local
     *            Local Id at the backend.
     */
    public TransportJobUID(final String back, final String local) {
        this.backend = back;
        this.localId = local;
    }

    /**
     * @return the backend
     */
    public String getBackend() {
        return this.backend;
    }

    /**
     * @return the localId
     */
    public String getLocalId() {
        return this.localId;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder(this.backend.toString());
        b.append('/');
        b.append(localId);
        return b.toString();
    }
}
