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
import java.io.FileNotFoundException;
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

    private static final int MIN_GUID_FILESIZE = 100 * 1024;

    /**
     * Serial Version for the virtual file.
     */
    private static final long serialVersionUID = 3L;

    private static final Logger LOGGER = Logger.getLogger(VirtualFileImpl.class
            .toString());

    private transient File localFile;

    private transient byte[] fileContent;

    private String name;

    private transient GuidBackend guidBackend;

    private transient VirtualFileUploader uploader;

    private transient Thread uploaderThread;

    private transient String guid;

    private transient FileServer fileServer;

    private final GuidBackends guidBackends = GuidBackends.getInstance();

    private final FileServerImpl fileServerImpl = FileServerImpl.getInstance();

    /**
     * Create a Virtual file based on an existing local file.
     * 
     * @param source
     *            The actual file.
     * @throws FileNotFoundException
     *             if the file cannot be found.
     */
    public VirtualFileImpl(final File source) throws FileNotFoundException {
        if (!source.exists()) {
            throw new FileNotFoundException(source.toString());
        }
        this.localFile = source;
        this.name = source.getName();
        this.guidBackend = this.guidBackends.get();

        if (this.fileServerImpl.isAvailable()) {
            this.fileServer = this.fileServerImpl.serveFile(this);
        } else if (this.guidBackend != null
                && this.guidBackends.isUploadSupported()
                && source.length() > VirtualFileImpl.MIN_GUID_FILESIZE) {
            this.uploader = new VirtualFileUploader(source, this.guidBackend);
            this.uploaderThread = new Thread(this.uploader);
            this.uploaderThread.start();
        }
    }

    private static enum ContentLocation {
        INLINE, GUID, SERVER,
    }

    private synchronized void writeObject(final ObjectOutputStream out)
            throws IOException {
        out.writeObject(this.name);
        this.finalizeUpload();
        if (this.fileServer != null) {
            out.writeObject(VirtualFileImpl.ContentLocation.SERVER);
            out.writeObject(this.fileServer);
        } else if (this.guid == null) {
            this.ensureContentIsLoaded();
            out.writeObject(VirtualFileImpl.ContentLocation.INLINE);
            out.writeObject(this.fileContent);
        } else {
            out.writeObject(VirtualFileImpl.ContentLocation.GUID);
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
            if (this.localFile == null) {
                this.loadFileFromRemote();
            } else {
                this.fileContent = FileUtil.loadFile(this.localFile);
            }
        }
    }

    private void loadFileFromRemote() {
        if (this.fileServer != null) {
            try {
                this.fileContent = this.fileServer.retrieveFile(this.name);
            } catch (final IOException io) {
                VirtualFileImpl.LOGGER.warning("Failed to load remote file "
                        + this.name);
            }
        } else if (this.guid != null) {
            try {
                final File content = File.createTempFile("WMSX", null);
                content.deleteOnExit();
                this.storeFileAs(content);
            } catch (final IOException e) {
                VirtualFileImpl.LOGGER.warning(e.getMessage());
            }
        }
    }

    private void readObject(final ObjectInputStream in) throws IOException {
        this.guidBackend = GuidBackends.getInstance().get();
        try {
            this.name = (String) in.readObject();
            final ContentLocation location = (ContentLocation) in.readObject();
            switch (location) {
            case GUID:
                this.guid = (String) in.readObject();
                break;
            case INLINE:
                this.fileContent = (byte[]) in.readObject();
                break;
            case SERVER:
                this.fileServer = (FileServer) in.readObject();
                break;
            default:
                throw new IOException("Failed to deserialize VirtualFile");
            }
        } catch (final ClassNotFoundException e) {
            VirtualFileImpl.LOGGER.warning(e.getMessage());
        }
    }

    /** {@inheritDoc} */
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
                    this.ensureContentIsLoaded();
                    final FileOutputStream fos = new FileOutputStream(f);
                    fos.write(this.fileContent);
                    fos.close();
                } catch (final IOException ioe) {
                    VirtualFileImpl.LOGGER.warning(ioe.getMessage());
                }
            } else {
                VirtualFileImpl.LOGGER.info("Downloading File GUID: "
                        + this.guid + " to " + f);
                this.guidBackend.download(this.guid, f);
            }
            try {
                FileUtil.makeExecutable(f);
            } catch (final IOException e) {
                VirtualFileImpl.LOGGER.warning(e.getMessage());
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
            VirtualFileImpl.LOGGER.info("Uploading " + this.lFile);
            this.guid = this.guidBackend.upload(this.lFile);
            VirtualFileImpl.LOGGER.info("Guid: " + this.guid);
            if (this.guid == null) {
                GuidBackends.getInstance().uploadFailed();
            }
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
