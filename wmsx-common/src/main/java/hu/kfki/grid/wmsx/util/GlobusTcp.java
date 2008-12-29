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

package hu.kfki.grid.wmsx.util;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.logging.Logger;

/**
 * Helper class for globus tcp port range.
 * 
 * @version $Revision$
 */
public final class GlobusTcp {

    private static final int UNPRIVELEDGED_MIN = 1024;

    private static final int DEFAULT_MAX = 25000;

    private static final int DEFAULT_MIN = 20000;

    private static GlobusTcp instance;

    private static final Logger LOGGER = Logger.getLogger(GlobusTcp.class
            .toString());

    private final int mintcp;

    private final int maxtcp;

    private GlobusTcp() {
        int min = GlobusTcp.DEFAULT_MIN;
        int max = GlobusTcp.DEFAULT_MAX;
        try {
            final String rangeStr = System.getenv("GLOBUS_TCP_PORT_RANGE");
            if (rangeStr != null) {
                final StringTokenizer tk = new StringTokenizer(rangeStr,
                        " \t\n\r\f,;:");
                min = Integer.parseInt(tk.nextToken());
                max = Integer.parseInt(tk.nextToken());
            }
        } catch (final NumberFormatException nfe) {
            // IGNORE
        } catch (final NoSuchElementException nse) {
            // IGNORE
        } catch (final Error e) {
            // Ignore. Stupid JDK 1.4
        }

        if (min < GlobusTcp.UNPRIVELEDGED_MIN) {
            min = GlobusTcp.DEFAULT_MIN;
        }
        if (max < min) {
            max = min;
        }
        this.mintcp = min;
        this.maxtcp = max;
        GlobusTcp.LOGGER.fine("TCP Port Range: " + min + " to " + max);
    }

    /**
     * @return the Singleton instance.
     */
    public static synchronized GlobusTcp getInstance() {
        if (GlobusTcp.instance == null) {
            GlobusTcp.instance = new GlobusTcp();
        }
        return GlobusTcp.instance;
    }

    /**
     * @return Lowest possible port to use.
     */
    public int getMinTcp() {
        return this.mintcp;
    }

    /**
     * @return Highest possible port to use.
     */
    public int getMaxTcp() {
        return this.maxtcp;
    }

}
