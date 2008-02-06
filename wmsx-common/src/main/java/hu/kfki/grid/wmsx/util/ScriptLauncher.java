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
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

/**
 * Launches scripts and binaries.
 * 
 * @version $Revision$
 */
public class ScriptLauncher {

    private final Map<String, OutputStream> streamMap = new TreeMap<String, OutputStream>();

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

    private OutputStream prepareOutput(final String stdout, final File workdir)
            throws IOException {
        final File stdoutfile = FileUtil.resolveFile(workdir, stdout)
                .getCanonicalFile();
        if (stdoutfile == null) {
            return null;
        }
        synchronized (this.streamMap) {
            final String path = stdoutfile.getPath();
            OutputStream out = this.streamMap.get(path);
            if (out == null) {
                stdoutfile.getParentFile().mkdirs();
                out = new BufferedOutputStream(new FileOutputStream(stdout));
                this.streamMap.put(path, out);
            }
            return out;
        }
    }

    public int launchScript(final String cmdString, final File dir,
            final String stdout, final String stderr) {
        int retVal = 0;
        try {
            final OutputStream o = this.prepareOutput(stdout, dir);
            final OutputStream e = this.prepareOutput(stderr, dir);
            final Process p = Runtime.getRuntime().exec(cmdString, null, dir);
            retVal = this.wrapProcess(p, o, e);
        } catch (final IOException e) {
            ScriptLauncher.LOGGER.warning("IOException launching script: "
                    + e.getMessage());
        }
        return retVal;
    }

    public int launchScript(final String[] cmdarray, final String stdout,
            final String stderr, final File workdir) {
        int retVal = 0;
        if (new File(cmdarray[0]).exists()) {
            try {
                final OutputStream o = this.prepareOutput(stdout, workdir);
                final OutputStream e = this.prepareOutput(stderr, workdir);
                retVal = this.launchScript(cmdarray, o, e, workdir);
            } catch (final IOException e) {
                ScriptLauncher.LOGGER.warning("IOException launching script: "
                        + e.getMessage());
            }
        }
        return retVal;
    }

    public int wrapProcess(final Process p, final OutputStream out,
            final OutputStream err) {
        int retVal = 0;
        if (p != null) {
            final InputStream i = new BufferedInputStream(p.getInputStream());
            StreamListener.listen(i, out);
            StreamListener.listen(new BufferedInputStream(p.getErrorStream()),
                    err);
            try {
                retVal = p.waitFor();
            } catch (final InterruptedException e) {
                // Ignore
            }
        }
        return retVal;
    }

    public int launchScript(final String[] cmdarray, final OutputStream out,
            final OutputStream err, final File workdir) {
        int retVal = 0;
        if (new File(cmdarray[0]).exists()) {
            try {
                final Process p = Runtime.getRuntime().exec(cmdarray, null,
                        workdir);
                retVal = this.wrapProcess(p, out, err);
            } catch (final IOException e) {
                ScriptLauncher.LOGGER.warning("IOException launching script: "
                        + e.getMessage());
            }
        }
        return retVal;
    }

}
