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

package at.ac.uibk.dps.wmsx.backends.lcg;

import hu.kfki.grid.wmsx.util.ProcessHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Common functionality used for Lcg and LcgGuid.
 * 
 * @version $Date$
 */
public final class LcgCommon {
    /**
     * Absolute path to the "env" program on most unix system.
     */
    public static final String ENV = "/usr/bin/env";

    private static final Logger LOGGER = Logger.getLogger(LcgCommon.class
            .toString());

    private LcgCommon() {
        // do not instantiate!
    }

    /**
     * Check if the given command is available for execution.
     * 
     * @param command
     *            The command to check
     * @return true if the command can be executed.
     */
    public static boolean isAvailable(final String command) {
        final List<String> cmds = new ArrayList<String>(3);
        cmds.add(LcgCommon.ENV);
        cmds.add("which");
        cmds.add(command);
        int rv = -1;
        try {
            final Process process = Runtime.getRuntime().exec(
                    cmds.toArray(new String[0]));
            rv = process.waitFor();
            ProcessHelper.cleanupProcess(process);
        } catch (final IOException io) {
            LcgCommon.LOGGER.warning(io.getMessage());
        } catch (final InterruptedException e) {
            LcgCommon.LOGGER.warning(e.getMessage());
        }
        return rv == 0;
    }
}
