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

package at.ac.uibk.dps.wmsx.gat;

import java.util.logging.Logger;

import org.gridlab.gat.AdaptorInfo;
import org.gridlab.gat.GAT;
import org.gridlab.gat.GATInvocationException;

/**
 * Common functionality for GAT Adaptors.
 * 
 * @version $Date$
 */
public final class GatCommon {
    private static final Logger LOGGER = Logger.getLogger(GatCommon.class
            .toString());

    private GatCommon() {
        // do not instantiate.
    }

    /**
     * Checks if the given Gat adapter is available.
     * 
     * @param type
     *            adator type
     * @param name
     *            Adaptor Name.
     * @return true if the adaptor is available.
     */
    public static boolean isAvailable(final String type, final String name) {
        try {
            for (final AdaptorInfo ai : GAT.getAdaptorInfos(type)) {
                if (name.equalsIgnoreCase(ai.getShortName())) {
                    return true;
                }
            }
        } catch (final GATInvocationException e) {
            GatCommon.LOGGER.warning(e.getMessage());
        }
        return false;
    }

}
