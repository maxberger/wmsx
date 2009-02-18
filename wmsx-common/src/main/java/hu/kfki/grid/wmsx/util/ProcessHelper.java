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

import java.io.IOException;

/**
 * Launches scripts and binaries.
 * 
 * @version $Date$
 */
public final class ProcessHelper {

    private ProcessHelper() {
        // empty on purpose.
    }

    /**
     * Cleanup after a process.
     * 
     * @param process
     *            The process to clean up after.
     */
    public static void cleanupProcess(final Process process) {
        if (process == null) {
            return;
        }
        try {
            process.waitFor();
        } catch (final InterruptedException e) {
            // ignore
        }
        try {
            process.getInputStream().close();
        } catch (final IOException e) {
            // ignore
        }
        try {
            process.getOutputStream().close();
        } catch (final IOException e) {
            // ignore
        }
        try {
            process.getErrorStream().close();
        } catch (final IOException e) {
            // ignore
        }
    }
}
