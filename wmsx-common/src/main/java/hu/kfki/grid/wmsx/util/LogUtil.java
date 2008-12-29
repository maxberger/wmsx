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

/**
 * Convenience methods for logging.
 * 
 * @version $Revision$
 */
public final class LogUtil {
    private static final int MAX_STACKTRACE_ELEMENTS = 10;

    private LogUtil() {
        // empty on purpose.
    }

    /**
     * Prepare a log message for an exception.
     * 
     * @param t
     *            Exception for this message
     * @return a string to be logged.
     */
    public static String logException(final Throwable t) {
        final StringBuilder b = new StringBuilder();
        b.append(t.getMessage());
        int count = 0;
        for (final StackTraceElement e : t.getStackTrace()) {
            if (count > LogUtil.MAX_STACKTRACE_ELEMENTS) {
                b.append("\n...");
                break;
            }
            b.append('\n').append(e.toString());
            count++;
        }
        return b.toString();
    }

}
