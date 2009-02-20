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

package at.ac.uibk.dps.wmsx.util;

import hu.kfki.grid.wmsx.util.FileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.logging.Logger;

import at.ac.uibk.dps.wmsx.util.VirtualFile;

/**
 * Actual Implementation of management for virtual files.
 * 
 * @version $Date$
 */
public class VirtualFileImpl implements Serializable, VirtualFile {

    /**
     * Serial Version for the virtual file.
     */
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = Logger.getLogger(VirtualFileImpl.class
            .toString());

    private final transient File localFile;

    private transient byte[] fileContent;

    private String name;

    /**
     * Create a Virtual File based on an existing local file.
     * 
     * @param source
     *            The actual file.
     */
    public VirtualFileImpl(final File source) {
        this.localFile = source;
        this.name = source.getName();
    }

    private void writeObject(final ObjectOutputStream out) throws IOException {
        this.ensureContentIsLoaded();
        out.writeObject(this.name);
        out.writeObject(this.fileContent);
    }

    private void ensureContentIsLoaded() {
        if (this.fileContent == null) {
            this.fileContent = FileUtil.loadFile(this.localFile);
        }
    }

    private void readObject(final ObjectInputStream in) throws IOException {
        try {
            this.name = (String) in.readObject();
            this.fileContent = (byte[]) in.readObject();
        } catch (final ClassNotFoundException e) {
            VirtualFileImpl.LOGGER.warning(e.getMessage());
        }
    }

    /**
     * Load the actual file content.
     * 
     * @return the content of the file.
     */
    public byte[] getFileContent() {
        this.ensureContentIsLoaded();
        return this.fileContent.clone();
    }

    /** {@inheritDoc} */
    public String getName() {
        return this.name;
    }

    /** {@inheritDoc} */
    public void storeFile(final File dir) {
        if (this.localFile == null) {
            try {
                final File f = new File(dir, this.name);
                final FileOutputStream fos = new FileOutputStream(f);
                fos.write(this.fileContent);
                fos.close();
                FileUtil.makeExecutable(f);
            } catch (final IOException ioe) {
                VirtualFileImpl.LOGGER.warning(ioe.getMessage());
            }
        } else {
            FileUtil.copy(this.localFile, new File(dir, this.name));
        }
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return this.name;
    }
}
