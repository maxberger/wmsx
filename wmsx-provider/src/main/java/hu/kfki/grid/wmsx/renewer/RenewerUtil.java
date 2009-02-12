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

package hu.kfki.grid.wmsx.renewer;

/**
 * @version $Revision$
 */
public final class RenewerUtil {

    private RenewerUtil() {
        // empty on purpose.
    }

    /**
     * Start a new Renewer.
     * 
     * @param renewer
     *            the Renewer to start.
     * @return True if the renewer returns success.
     */
    public static boolean startupRenewer(final Renewer renewer) {
        final boolean success = renewer.renew();
        if (success) {
            final Thread t = new Thread(renewer);
            t.setDaemon(true);
            t.start();
        }
        return success;
    }

}
