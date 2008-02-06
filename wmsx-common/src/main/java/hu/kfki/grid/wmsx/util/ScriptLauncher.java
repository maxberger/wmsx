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

package hu.kfki.grid.wmsx.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

/**
 * Launches scripts and binaries.
 * 
 * @version $Revision$
 */
public class ScriptLauncher {

    private static ScriptLauncher instance;

    private static final Logger LOGGER = Logger.getLogger(ScriptLauncher.class
            .toString());

    private ScriptLauncher() {
    }

    public static synchronized ScriptLauncher getInstance() {
        if (ScriptLauncher.instance == null) {
            ScriptLauncher.instance = new ScriptLauncher();
        }
        return ScriptLauncher.instance;
    }

    private OutputStream prepareOutput(final String stdout) throws IOException {
        if (stdout == null) {
            return null;
        }
        final File stdoutfile = new File(stdout);
        stdoutfile.getAbsoluteFile().getParentFile().mkdirs();
        return new BufferedOutputStream(new FileOutputStream(stdout));
    }

    public int launchScript(final String cmdString, final File dir,
            final String stdout) {
        int retVal = 0;
        try {
            final OutputStream o = this.prepareOutput(stdout);
            final Process p = Runtime.getRuntime().exec(cmdString, null, dir);
            retVal = this.wrapProcess(p, o);
        } catch (final IOException e) {
            ScriptLauncher.LOGGER.warning("IOException launching script: "
                    + e.getMessage());
        }
        return retVal;
    }

    public int launchScript(final String[] cmdarray, final String stdout) {
        int retVal = 0;
        if (new File(cmdarray[0]).exists()) {
            try {
                final OutputStream o = this.prepareOutput(stdout);
                retVal = this.launchScript(cmdarray, o);
            } catch (final IOException e) {
                ScriptLauncher.LOGGER.warning("IOException launching script: "
                        + e.getMessage());
            }
        }
        return retVal;
    }

    public int wrapProcess(final Process p, final OutputStream out) {
        int retVal = 0;
        if (p != null) {
            final InputStream i = new BufferedInputStream(p.getInputStream());
            StreamListener.listen(i, out);
            // TODO!
            StreamListener.listen(new BufferedInputStream(p.getErrorStream()),
                    out);
            try {
                retVal = p.waitFor();
            } catch (final InterruptedException e) {
                // Ignore
            }
        }
        return retVal;
    }

    public int launchScript(final String[] cmdarray, final OutputStream out) {
        int retVal = 0;
        if (new File(cmdarray[0]).exists()) {
            try {
                final Process p = Runtime.getRuntime().exec(cmdarray);
                retVal = this.wrapProcess(p, out);
            } catch (final IOException e) {
                ScriptLauncher.LOGGER.warning("IOException launching script: "
                        + e.getMessage());
            }
        }
        return retVal;
    }

}
