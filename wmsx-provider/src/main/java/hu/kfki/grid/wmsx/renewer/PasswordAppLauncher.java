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
 * 
 */

/* $Id: vasblasd$ */

package hu.kfki.grid.wmsx.renewer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.logging.Logger;

public class PasswordAppLauncher {
    private static PasswordAppLauncher instance;

    private static final Logger LOGGER = Logger
            .getLogger(PasswordAppLauncher.class.toString());

    private PasswordAppLauncher() {
    }

    static public synchronized PasswordAppLauncher getInstance() {
        if (PasswordAppLauncher.instance == null) {
            PasswordAppLauncher.instance = new PasswordAppLauncher();
        }
        return PasswordAppLauncher.instance;
    }

    public boolean launch(final String[] cmdarray, final String password) {
        boolean retVal = false;
        try {
            final Process p = Runtime.getRuntime().exec(cmdarray);
            // final BufferedInputStream bi = new BufferedInputStream(p
            // .getInputStream());
            // final byte[] b = new byte[4096];
            // bi.read(b);
            if (password != null) {
                Thread.sleep(1000);
                final BufferedWriter w = new BufferedWriter(
                        new OutputStreamWriter(p.getOutputStream()));
                w.write(password);
                w.newLine();
                w.flush();
            }
            retVal = p.waitFor() == 0;

        } catch (final IOException e) {
            PasswordAppLauncher.LOGGER.warning(e.getMessage());
        } catch (final InterruptedException e) {
            PasswordAppLauncher.LOGGER.warning(e.getMessage());
        }
        return retVal;
    }
}
