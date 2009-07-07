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
package hu.kfki.grid.wmsx.backends.local;

import hu.kfki.grid.wmsx.backends.Backend;
import hu.kfki.grid.wmsx.backends.JobUid;

/**
 * Provides Utilities for backends with counters.
 * 
 * @version $Date: 1/1/2000$
 */
public final class BackendWithCounterUtils {

    private BackendWithCounterUtils() {
        // empty on purpose.
    }

    /**
     * Parse an integer id and return a valid backend accorrding to
     * {@link hu.kfki.grid.wmsx.backends.Backend#getJobUidForBackendId(String)}.
     * 
     * @param backend
     *            the backend to set
     * @param backendIdString
     *            the backend id, represented as an integer.
     * @return a JobUid.
     */
    public static JobUid getIntegerJobUid(final Backend backend,
            final String backendIdString) {
        try {
            return new JobUid(backend, Integer.parseInt(backendIdString));
        } catch (final NumberFormatException nfe) {
            return null;
        }
    }

}
