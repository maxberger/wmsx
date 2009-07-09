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

import net.jini.core.entry.Entry;

/**
 * Entry for registering WMSX to a JINI Registry.
 * 
 * @version $Date$ *
 */
public class WmsxEntry implements Entry {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Current user name - used to identify the WMSX Service.
     */
    // CHECKSTYLE:OFF
    // Entries MUST contain public variables.
    public String userName;

    // CHECKSTYLE:ON

    /**
     * Default Constructor.
     */
    public WmsxEntry() {
        this.userName = "";
    };

    /**
     * Constructor with user name.
     * 
     * @param uName
     *            userName to set.
     */
    public WmsxEntry(final String uName) {
        this.userName = uName;
    };

}
