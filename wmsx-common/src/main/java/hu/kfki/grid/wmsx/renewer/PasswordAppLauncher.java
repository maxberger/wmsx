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

package hu.kfki.grid.wmsx.renewer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.logging.Logger;

/**
 * Launches Apps which ask for a password.
 * 
 * @version $Date: 1/1/2000$
 */
public final class PasswordAppLauncher {

    /**
     * 
     */
    private static final int APP_STARTUP_DELAY = 1000;

    private static final Logger LOGGER = Logger
            .getLogger(PasswordAppLauncher.class.toString());

    private static final class SingletonHolder {
        private static final PasswordAppLauncher INSTANCE = new PasswordAppLauncher();

        private SingletonHolder() {
        }
    }

    private PasswordAppLauncher() {
    }

    /**
     * @return the Singleton Instance.
     */
    public static synchronized PasswordAppLauncher getInstance() {
        return PasswordAppLauncher.SingletonHolder.INSTANCE;

    }

    /**
     * Launches the Password app.
     * 
     * @param cmdarray
     *            List of commands to execute
     * @param password
     *            Password to send
     * @return true if the executions was sucessfull and returned 0.
     */
    public boolean launch(final String[] cmdarray, final String password) {
        boolean retVal = false;
        BufferedWriter w = null;
        try {
            final Process p = Runtime.getRuntime().exec(cmdarray);
            if (password != null) {
                Thread.sleep(PasswordAppLauncher.APP_STARTUP_DELAY);
                w = new BufferedWriter(new OutputStreamWriter(p
                        .getOutputStream()));
                w.write(password);
                w.newLine();
                w.flush();
            }
            retVal = p.waitFor() == 0;

        } catch (final IOException e) {
            PasswordAppLauncher.LOGGER.warning(e.getMessage());
        } catch (final InterruptedException e) {
            PasswordAppLauncher.LOGGER.warning(e.getMessage());
        } finally {
            try {
                if (w != null) {
                    w.close();
                }
            } catch (final IOException e) {
                // ignore.
            }
        }
        return retVal;
    }
}
