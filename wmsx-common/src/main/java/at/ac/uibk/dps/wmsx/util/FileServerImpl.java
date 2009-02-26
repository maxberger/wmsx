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

import hu.kfki.grid.wmsx.util.Exporter;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * {@link FileServer} implementation which servers {@link VirtualFileImpl}.
 * 
 * @version $Date$
 */
public final class FileServerImpl implements FileServer {

    private static final Logger LOGGER = Logger.getLogger(FileServerImpl.class
            .toString());

    private FileServer myProxy;

    private final Map<String, VirtualFile> serverMap = new HashMap<String, VirtualFile>();

    private static final class SingletonHolder {
        protected static final FileServerImpl INSTANCE = new FileServerImpl();

        private SingletonHolder() {
        }
    }

    private FileServerImpl() {
        // nothing to do.
    }

    /**
     * @return the Singleton instance.
     */
    public static FileServerImpl getInstance() {
        return FileServerImpl.SingletonHolder.INSTANCE;
    }

    /**
     * Actually start the file server.
     */
    public void start() {
        synchronized (this) {
            if (this.myProxy == null) {
                this.myProxy = (FileServer) Exporter.getInstance().export(this);
            }
        }
    }

    /**
     * @return true if the FileServer is available.
     */
    public boolean isAvailable() {
        synchronized (this) {
            return this.myProxy != null;
        }
    }

    /**
     * Start Serving a given file.
     * 
     * @param vFile
     *            the file to serve.
     * @return A proxy which can be used to retrieve the file again.
     */
    public FileServer serveFile(final VirtualFile vFile) {
        final String name = vFile.getName();
        FileServerImpl.LOGGER.info("Serving file: " + name);
        this.serverMap.put(name, vFile);
        return this.myProxy;
    }

    /** {@inheritDoc} */
    public byte[] retrieveFile(final String name) {
        final VirtualFile vFile = this.serverMap.get(name);
        if (vFile == null) {
            return null;
        } else {
            FileServerImpl.LOGGER.info("Transferring file: " + name);
            return vFile.getFileContent();
        }
    }

}
