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

package hu.kfki.grid.wmsx.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import at.ac.uibk.dps.wmsx.util.VirtualFile;
import at.ac.uibk.dps.wmsx.util.VirtualFileImpl;

/**
 * Utilities for file management.
 * 
 * @version $Date$
 */
public final class FileUtil {
    // private static final int SLEEP_IF_FILE_DOES_NOT_EXIST = 1000;

    private static final String FAILED_TO_DELETE = "Failed to delete: ";

    private static final String BIN_CHMOD = "/bin/chmod";

    private static final int BUFSIZE = 4096;

    private static final Logger LOGGER = Logger.getLogger(FileUtil.class
            .toString());

    private FileUtil() {
    };

    /**
     * Copy from one Stream to another.
     * 
     * @param fin
     *            Input Stream
     * @param fout
     *            Output Stream
     * @param ia
     *            IOException to throw in case the operation fails, may be null
     * @throws IOException
     *             if the operation fails.
     */
    private static void copy(final InputStream fin, final OutputStream fout,
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

    /**
     * Store Data from an Input Stream info a file.
     * 
     * @param fin
     *            InputStream to read from.
     * @param out
     *            File to store into.
     * @throws IOException
     *             if anything goes wrong.
     */
    public static void copy(final InputStream fin, final File out)
            throws IOException {
        final FileOutputStream fout = new FileOutputStream(out);
        FileUtil.copy(fin, fout, null);
    }

    /**
     * Efficiently copy from file A to file B.
     * 
     * @param in
     *            File to read.
     * @param out
     *            File to write.
     * @return true on success, false on failure.
     */
    public static boolean copy(final File in, final File out) {

        FileInputStream fin = null;
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(out);
            fin = new FileInputStream(in);
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
        } catch (final IOException io) {
            FileUtil.LOGGER.warning(io.getMessage());
            return false;
        } finally {
            try {
                if (fout != null) {
                    fout.close();
                }
            } catch (final IOException e2) {
                FileUtil.LOGGER.warning(e2.getMessage());
            }
            try {
                if (fin != null) {
                    fin.close();
                }
            } catch (final IOException e2) {
                FileUtil.LOGGER.warning(e2.getMessage());
            }
        }
        return true;
    }

    /**
     * Smartly resolve a given filename against a given directory.
     * 
     * @param dir
     *            directory to resolve to
     * @param fileName
     *            filename to resolve
     * @return an absolute path describing the file.
     */
    public static File resolveFile(final File dir, final String fileName) {
        if (fileName == null) {
            return null;
        }
        final File fileNameFile = new File(fileName);
        final File inputFile;
        if (dir == null) {
            inputFile = fileNameFile.getAbsoluteFile();
        } else if (fileNameFile.isAbsolute()) {
            inputFile = fileNameFile;
        } else {
            inputFile = new File(dir, fileName);
        }
        return inputFile;
    }

    /**
     * Tries to make a particular file executable.
     * 
     * @param file
     *            the file to modify
     * @throws IOException
     *             if the file could not be found.
     */
    public static void makeExecutable(final File file) throws IOException {
        try {
            Runtime.getRuntime().exec(
                    new String[] { FileUtil.BIN_CHMOD, "+x",
                            file.getCanonicalPath(), }).waitFor();
        } catch (final InterruptedException e) {
            // Ignore
        }

    }

    /**
     * Create a Sandbox from a list of given files. Note that all pathnames are
     * erased.
     * 
     * @param files
     *            list of Files to load
     * @param dir
     *            basedir for relative file names
     * @return a Sandbox List.
     */
    public static List<VirtualFile> createSandbox(final List<String> files,
            final File dir) {
        final List<VirtualFile> sandbox = new ArrayList<VirtualFile>(files
                .size());

        for (final String fileName : files) {
            final File f = FileUtil.resolveFile(dir, fileName);
            sandbox.add(new VirtualFileImpl(f));
        }
        return sandbox;
    }

    /**
     * Try to load the contents of a given file.
     * 
     * @param f
     *            File handle
     * @return a byte[] with the file contents.
     */
    public static byte[] loadFile(final File f) {
        final int len = (int) f.length();
        final byte[] buf = new byte[len];
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
            fis.read(buf);
        } catch (final IOException e) {
            FileUtil.LOGGER.warning(e.getMessage());
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (final IOException e) {
                FileUtil.LOGGER.warning(e.getMessage());
            }
        }
        return buf;
    }

    /**
     * Store files from SandBox into a given directory.
     * 
     * @param sandbox
     *            the SandBox.
     * @param dir
     *            Directory to store into.
     */
    public static void retrieveSandbox(final List<VirtualFile> sandbox,
            final File dir) {
        for (final VirtualFile entry : sandbox) {
            entry.storeFile(dir);
        }
    }

    /**
     * Recursively clean the given directory. Use with caution!
     * 
     * @param dir
     *            Directory to clean.
     * @param removeOnExit
     *            if true, deletion will be delayed until program exits
     */
    public static void cleanDir(final File dir, final boolean removeOnExit) {
        final File[] entries = dir.listFiles();
        if (entries != null) {
            for (int i = 0; i < entries.length; i++) {
                final File f = entries[i];
                if (f.isDirectory()) {
                    FileUtil.cleanDir(f, removeOnExit);
                } else if (f.isFile()) {
                    FileUtil.remove(f, removeOnExit);
                }
            }
        }
        FileUtil.remove(dir, removeOnExit);
    }

    private static void remove(final File f, final boolean removeOnExit) {
        if (removeOnExit) {
            f.deleteOnExit();
        } else {
            if (!f.delete()) {
                FileUtil.LOGGER.warning(FileUtil.FAILED_TO_DELETE
                        + f.getAbsolutePath());
            }
        }
    }

    /**
     * Creates a temporary directory for use. Note: You need to delete it after
     * use
     * 
     * @return a File object pointing to the new directory.
     * @throws IOException
     *             if anything goes wrong.
     * @see {@link #cleanDir(File, boolean)}
     */
    public static File createTempDir() throws IOException {
        final File wd = File.createTempFile("wmsx", null);
        if (!wd.delete()) {
            throw new IOException(FileUtil.FAILED_TO_DELETE + wd);
        }
        if (!wd.mkdirs()) {
            throw new IOException("Failed to create tempdir: " + wd);
        }
        return wd;
    }

    /**
     * Copy a list of files from a to b.
     * 
     * @param inputList
     *            list of filenames, may contain path elements
     * @param from
     *            source directory
     * @param to
     *            target directory
     * @throws IOException
     *             if anything goes wrong.
     */
    public static void copyList(final List<String> inputList, final File from,
            final File to) throws IOException {
        final Iterator<String> it = inputList.iterator();
        boolean success = true;
        while (it.hasNext()) {
            final String fileName = it.next();
            final File inputFile = FileUtil.resolveFile(from, fileName);
            final File toFile = new File(to, inputFile.getName());
            success = FileUtil.copy(inputFile, toFile) && success;
        }
        if (!success) {
            throw new IOException("Error copying some files");
        }
    }

}
