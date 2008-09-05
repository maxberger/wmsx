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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

/**
 * Launches scripts and binaries.
 * 
 * @version $Revision$
 */
public final class ScriptLauncher {

    private static final String IOEXCEPTION_LAUNCHING_SCRIPT = "IOException launching script: ";

    private static final Logger LOGGER = Logger.getLogger(ScriptLauncher.class
            .toString());

    private final Map<String, OutputStream> streamMap = new TreeMap<String, OutputStream>();

    private static final class SingletonHolder {
        private static final ScriptLauncher INSTANCE = new ScriptLauncher();

        private SingletonHolder() {
        }
    }

    private ScriptLauncher() {
    }

    /**
     * @return the Singleton Instance.
     */
    public static ScriptLauncher getInstance() {
        return ScriptLauncher.SingletonHolder.INSTANCE;
    }

    private OutputStream prepareOutput(final String stdout, final File workdir)
            throws IOException {
        final File stdoutfileAbs = FileUtil.resolveFile(workdir, stdout);
        if (stdoutfileAbs == null) {
            return null;
        }
        final File stdoutfile = stdoutfileAbs.getCanonicalFile();

        synchronized (this.streamMap) {
            final String path = stdoutfile.getPath();
            OutputStream out = this.streamMap.get(path);
            if (out == null) {
                final File parent = stdoutfile.getParentFile();
                if (!parent.exists() && !parent.mkdirs()) {
                    throw new FileNotFoundException(parent.getAbsolutePath());
                }
                out = new BufferedOutputStream(new FileOutputStream(stdoutfile,
                        true));
                this.streamMap.put(path, out);
            }
            return out;
        }
    }

    /**
     * Launch a script.
     * 
     * @param cmdString
     *            command to execute
     * @param stdout
     *            name of file for stdout, relative to dir
     * @param stderr
     *            name of file for stderr, relative to dir
     * @param dir
     *            directory to work in
     * @return return value of the execution.
     */
    public int launchScript(final String cmdString, final File dir,
            final String stdout, final String stderr) {
        int retVal = 0;
        try {
            final OutputStream o = this.prepareOutput(stdout, dir);
            final OutputStream e = this.prepareOutput(stderr, dir);
            final Process p = Runtime.getRuntime().exec(cmdString, null, dir);
            retVal = this.wrapProcess(p, o, e);
        } catch (final IOException e) {
            ScriptLauncher.LOGGER
                    .warning(ScriptLauncher.IOEXCEPTION_LAUNCHING_SCRIPT
                            + e.getMessage());
        }
        return retVal;
    }

    /**
     * Launch a script.
     * 
     * @param cmdarray
     *            array of executable and arguments
     * @param stdout
     *            name of file for stdout, relative to workdir
     * @param stderr
     *            name of file for stderr, relative to workdir
     * @param workdir
     *            directory to work in
     * @return return value of the execution.
     */
    public int launchScript(final String[] cmdarray, final String stdout,
            final String stderr, final File workdir) {
        int retVal = 0;
        if (new File(cmdarray[0]).exists()) {
            try {
                final OutputStream o = this.prepareOutput(stdout, workdir);
                final OutputStream e = this.prepareOutput(stderr, workdir);
                retVal = this.launchScript(cmdarray, o, e, workdir);
            } catch (final IOException e) {
                ScriptLauncher.LOGGER
                        .warning(ScriptLauncher.IOEXCEPTION_LAUNCHING_SCRIPT
                                + e.getMessage());
            }
        }
        return retVal;
    }

    /**
     * Wrap a process with stream listeners and wait for its execution.
     * 
     * @param p
     *            the process to wrap
     * @param out
     *            OutputStream to listen to
     * @param err
     *            ErrorStream to listen to
     * @return return value of the process' execution.
     */
    public int wrapProcess(final Process p, final OutputStream out,
            final OutputStream err) {
        int retVal = 0;
        if (p != null) {
            final InputStream i = new BufferedInputStream(p.getInputStream());
            StreamListener.listen(i, out, this);
            StreamListener.listen(new BufferedInputStream(p.getErrorStream()),
                    err, this);
            try {
                retVal = p.waitFor();
            } catch (final InterruptedException e) {
                // Ignore
            }
        }
        try {
            if (out != null) {
                out.flush();
            }
        } catch (final IOException io) {
            // ignore
        }
        try {
            if (err != null) {
                err.flush();
            }
        } catch (final IOException io) {
            // ignore
        }
        return retVal;
    }

    /**
     * Launch a script.
     * 
     * @param cmdarray
     *            array of executable and arguments
     * @param out
     *            OutputStream for StdOut
     * @param err
     *            OutputStream for StdErr
     * @param workdir
     *            directory to work in
     * @return return value of the execution.
     */
    public int launchScript(final String[] cmdarray, final OutputStream out,
            final OutputStream err, final File workdir) {
        int retVal = 0;
        if (new File(cmdarray[0]).exists()) {
            try {
                final Process p = Runtime.getRuntime().exec(cmdarray, null,
                        workdir);
                retVal = this.wrapProcess(p, out, err);
            } catch (final IOException e) {
                ScriptLauncher.LOGGER
                        .warning(ScriptLauncher.IOEXCEPTION_LAUNCHING_SCRIPT
                                + e.getMessage());
            }
        }
        return retVal;
    }

    /**
     * Forget about the given stream.
     * 
     * @param out
     *            Stream to forget.
     */
    public void forgetStream(final OutputStream out) {
        synchronized (this.streamMap) {
            final Iterator<Map.Entry<String, OutputStream>> it = this.streamMap
                    .entrySet().iterator();
            String key = null;
            while (it.hasNext()) {
                final Map.Entry<String, OutputStream> entry = it.next();
                if (out == entry.getValue()) {
                    key = entry.getKey();
                    break;
                }
            }
            if (key != null) {
                this.streamMap.remove(key);
            }
        }
    }

}
