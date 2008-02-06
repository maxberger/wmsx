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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

/**
 * Utilities for file management.
 * 
 * @version $Revision$
 */
public final class FileUtil {
    private static final String BIN_CHMOD = "/bin/chmod";

    private static final int BUFSIZE = 4096;

    private static final Logger LOGGER = Logger.getLogger(FileUtil.class
            .toString());

    private FileUtil() {
    };

    public static void copy(final InputStream fin, final OutputStream fout,
            final IOException ia) throws IOException {
        try {
            final byte[] b = new byte[FileUtil.BUFSIZE];
            int count;
            do {
                count = fin.read(b);
                if (count > 0) {
                    fout.write(b, 0, count);
                }
            } while (count > 0);
        } catch (final IOException o) {
            if (ia == null) {
                throw o;
            } else {
                throw ia;
            }
        } finally {
            if (fin != null) {
                fin.close();
            }
            if (fout != null) {
                fout.close();
            }
        }
    }

    public static void copy(final InputStream fin, final File out)
            throws IOException {
        final FileOutputStream fout = new FileOutputStream(out);
        FileUtil.copy(fin, fout, null);
    }

    public static void copy(final File in, final File out) throws IOException {

        final FileInputStream fin = new FileInputStream(in);
        final FileOutputStream fout = new FileOutputStream(out);
        final FileChannel inChannel = fin.getChannel();
        final FileChannel outChannel = fout.getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } catch (final IOException ia) {
            // This is due to a bug in Java 1.4
            FileUtil.copy(fin, fout, ia);
        } finally {
            if (inChannel != null) {
                inChannel.close();
            }
            if (outChannel != null) {
                outChannel.close();
            }
        }

        try {
            Runtime.getRuntime().exec(
                    new String[] { FileUtil.BIN_CHMOD,
                            "--reference=" + in.getCanonicalPath(),
                            out.getCanonicalPath(), }).waitFor();
        } catch (final InterruptedException e) {
            // Ignore
        }
    }

    public static File resolveFile(final File dir, final String fileName) {
        final File fileNameFile = new File(fileName);
        final File inputFile;
        if (fileNameFile.isAbsolute()) {
            inputFile = fileNameFile;
        } else {
            inputFile = new File(dir, fileName);
        }
        return inputFile;
    }

    public static void makeExecutable(final File file) throws IOException {
        try {
            Runtime.getRuntime().exec(
                    new String[] { FileUtil.BIN_CHMOD, "+x",
                            file.getCanonicalPath(), }).waitFor();
        } catch (final InterruptedException e) {
            // Ignore
        }

    }

    public static Map<String, byte[]> createSandbox(final List<String> files,
            final File dir) {
        final Map<String, byte[]> sandbox = new TreeMap<String, byte[]>();

        for (final String fileName : files) {
            try {
                final File f = FileUtil.resolveFile(dir, fileName);
                sandbox.put(f.getName(), FileUtil.loadFile(f));
            } catch (final IOException io) {
                FileUtil.LOGGER.warning(io.getMessage());
            }
        }
        return sandbox;
    }

    private static byte[] loadFile(final File f) throws IOException {
        final int len = (int) f.length();
        final byte[] buf = new byte[len];
        final FileInputStream fis = new FileInputStream(f);
        fis.read(buf);
        fis.close();
        return buf;
    }

    public static void retrieveSandbox(final Map<String, byte[]> sandbox,
            final File dir) {
        for (final Map.Entry<String, byte[]> entry : sandbox.entrySet()) {
            try {
                final File f = new File(dir, entry.getKey());
                final FileOutputStream fos = new FileOutputStream(f);
                fos.write(entry.getValue());
                fos.close();
                FileUtil.makeExecutable(f);
            } catch (final IOException ioe) {
                FileUtil.LOGGER.warning(ioe.getMessage());
            }
        }
    }

    public static void cleanDir(final File dir) {
        final File[] entries = dir.listFiles();
        if (entries != null) {
            for (int i = 0; i < entries.length; i++) {
                final File f = entries[i];
                if (f.isDirectory()) {
                    FileUtil.cleanDir(f);
                } else if (f.isFile()) {
                    f.delete();
                }
            }
        }
        dir.delete();
    }

}
