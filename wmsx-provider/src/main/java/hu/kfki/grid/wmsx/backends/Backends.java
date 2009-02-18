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

import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.discovery.tools.Service;

/**
 * Constant list of backends implemented.
 * 
 * @version $Date$
 */
public final class Backends {

    private final Map<String, Backend> backends = new TreeMap<String, Backend>();

    private static final class SingletonHolder {
        protected static final Backends INSTANCE = new Backends();

        private SingletonHolder() {
        }
    }

    @SuppressWarnings("unchecked")
    private Backends() {
        final Enumeration<Backend> e = Service.providers(Backend.class);
        while (e.hasMoreElements()) {
            final Backend b = e.nextElement();
            this.backends.put(b.toString().toLowerCase(Locale.ENGLISH), b);
        }
    }

    /**
     * @return the Singleton instance.
     */
    public static Backends getInstance() {
        return Backends.SingletonHolder.INSTANCE;
    }

    /**
     * Retrieve a backend.
     * 
     * @param name
     *            name of backend (must be lower case!)
     * @return the backend.
     */
    public Backend get(final String name) {
        return this.backends.get(name);
    }

    /**
     * @return the set of loaded backends.
     */
    public Set<String> listBackends() {
        return this.backends.keySet();
    }
}
