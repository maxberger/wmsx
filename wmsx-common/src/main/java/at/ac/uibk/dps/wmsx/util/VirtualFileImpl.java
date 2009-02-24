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

import at.ac.uibk.dps.wmsx.backends.guid.GuidBackend;
import at.ac.uibk.dps.wmsx.backends.guid.GuidBackends;

/**
 * Actual Implementation of management for virtual files.
 * 
 * @version $Date$
 */
public final class VirtualFileImpl implements Serializable, VirtualFile {

    /**
     * Serial Version for the virtual file.
     */
    private static final long serialVersionUID = 2L;

    private static final Logger LOGGER = Logger.getLogger(VirtualFileImpl.class
            .toString());

    private transient File localFile;

    private transient byte[] fileContent;

    private String name;

    private transient GuidBackend guidBackend;

    private transient VirtualFileUploader uploader;

    private transient Thread uploaderThread;

    private transient String guid;

    /**
     * Create a Virtual file based on an existing local file.
     * 
     * @param source
     *            The actual file.
     */
    public VirtualFileImpl(final File source) {
        this(source, null);
    }

    /**
     * Create a Virtual file based on an existing local file.
     * 
     * @param source
     *            the local file
     * @param vo
     *            if not null, upload to LFC of this VO.
     */
    public VirtualFileImpl(final File source, final String vo) {
        this.localFile = source;
        this.name = source.getName();
        this.guidBackend = GuidBackends.getInstance().get();
        if (vo != null && this.guidBackend != null) {
            // TODO: Check file size.
            this.uploader = new VirtualFileUploader(source, this.guidBackend);
            this.uploaderThread = new Thread(this.uploader);
            this.uploaderThread.start();
        }
    }

    private synchronized void writeObject(final ObjectOutputStream out)
            throws IOException {
        out.writeObject(this.name);
        this.finalizeUpload();
        if (this.guid == null) {
            this.ensureContentIsLoaded();
            out.writeBoolean(false);
            out.writeObject(this.fileContent);
        } else {
            out.writeBoolean(true);
            out.writeObject(this.guid);
        }
    }

    private void finalizeUpload() {
        if (this.uploaderThread != null) {
            try {
                this.uploaderThread.join();
            } catch (final InterruptedException e) {
                VirtualFileImpl.LOGGER.warning(e.getMessage());
            }
            this.guid = this.uploader.getGuid();
            this.uploaderThread = null;
            this.uploader = null;
        }
    }

    private synchronized void ensureContentIsLoaded() {
        if (this.fileContent == null) {
            if (this.localFile == null && this.guid != null) {
                try {
                    final File content = File.createTempFile("WMSX", null);
                    content.deleteOnExit();
                    this.storeFileAs(content);
                } catch (final IOException e) {
                    VirtualFileImpl.LOGGER.warning(e.getMessage());
                }
            }
            if (this.localFile != null) {
                this.fileContent = FileUtil.loadFile(this.localFile);
            }
        }
    }

    private void readObject(final ObjectInputStream in) throws IOException {
        this.guidBackend = GuidBackends.getInstance().get();
        try {
            this.name = (String) in.readObject();
            final boolean isGuid = in.readBoolean();
            if (isGuid) {
                this.guid = (String) in.readObject();
            } else {
                this.fileContent = (byte[]) in.readObject();
            }
        } catch (final ClassNotFoundException e) {
            VirtualFileImpl.LOGGER.warning(e.getMessage());
        }
    }

    /**
     * Load the actual file content.
     * 
     * @return the content of the file.
     */
    public synchronized byte[] getFileContent() {
        this.ensureContentIsLoaded();
        return this.fileContent.clone();
    }

    /** {@inheritDoc} */
    public String getName() {
        return this.name;
    }

    /** {@inheritDoc} */
    public synchronized void storeFile(final File dir) {
        final File f = new File(dir, this.name);
        this.storeFileAs(f);
    }

    private void storeFileAs(final File f) {
        if (this.localFile == null) {
            if (this.guid == null) {
                try {
                    final FileOutputStream fos = new FileOutputStream(f);
                    fos.write(this.fileContent);
                    fos.close();
                    FileUtil.makeExecutable(f);
                } catch (final IOException ioe) {
                    VirtualFileImpl.LOGGER.warning(ioe.getMessage());
                }
            } else {
                this.guidBackend.download(this.guid, f);
            }
            this.localFile = f;
        } else {
            FileUtil.copy(this.localFile, f);
        }
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return this.name;
    }

    private static final class VirtualFileUploader implements Runnable {

        private String guid;

        private final File lFile;

        private final GuidBackend guidBackend;

        private VirtualFileUploader(final File local, final GuidBackend gBackend) {
            this.lFile = local;
            this.guidBackend = gBackend;
        }

        public String getGuid() {
            return this.guid;
        }

        /** {@inheritDoc} */
        public void run() {
            VirtualFileImpl.LOGGER.finer("Uploading " + this.lFile);
            this.guid = this.guidBackend.upload(this.lFile);
            VirtualFileImpl.LOGGER.finer("Guid: " + this.guid);
        }
    }

    /** {@inheritDoc} */
    public synchronized void deleteTemp() {
        this.finalizeUpload();
        if (this.guid != null) {
            this.guidBackend.delete(this.guid);
            this.guid = null;
        }
    }
}
