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
package at.ac.uibk.dps.wmsx.backends.guid;

import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.discovery.tools.Service;

/**
 * Service Provider discoverer for GuidBackends.
 * 
 * @version $Date$
 */
public final class GuidBackends {

    private final Map<String, GuidBackend> backends = new TreeMap<String, GuidBackend>();

    private static final class SingletonHolder {
        protected static final GuidBackends INSTANCE = new GuidBackends();

        private SingletonHolder() {
        }
    }

    @SuppressWarnings("unchecked")
    private GuidBackends() {
        final Enumeration<GuidBackend> e = Service.providers(GuidBackend.class);
        while (e.hasMoreElements()) {
            final GuidBackend b = e.nextElement();
            if (b.isAvailable()) {
                this.backends.put(b.toString().toLowerCase(Locale.ENGLISH), b);
            }
        }
    }

    /**
     * @return the Singleton instance.
     */
    public static GuidBackends getInstance() {
        return GuidBackends.SingletonHolder.INSTANCE;
    }

    /**
     * Retrieve a backend.
     * 
     * @param name
     *            name of backend (must be lower case!)
     * @return the backend.
     */
    public GuidBackend get(final String name) {
        return this.backends.get(name);
    }

    /**
     * @return the set of loaded backends.
     */
    public Set<String> listBackends() {
        return this.backends.keySet();
    }
}
