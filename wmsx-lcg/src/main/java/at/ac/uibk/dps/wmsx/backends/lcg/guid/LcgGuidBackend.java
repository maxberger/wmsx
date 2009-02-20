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

package at.ac.uibk.dps.wmsx.backends.lcg.guid;

import at.ac.uibk.dps.wmsx.backends.guid.GuidBackend;
import at.ac.uibk.dps.wmsx.backends.lcg.LcgCommon;

/**
 * Implementation for GuidBackend which works by wrapping the glite command line
 * tools.
 * 
 * @version $Date$
 */
public class LcgGuidBackend implements GuidBackend {

    /**
     * Default constructor.
     */
    public LcgGuidBackend() {
        // nothing to be done.
    }

    /** {@inheritDoc} */
    public boolean isAvailable() {
        return LcgCommon.isAvailable("lcg-cr");
    }

}
