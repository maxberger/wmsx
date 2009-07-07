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
package at.ac.uibk.dps.wmsx.backends.guid;

import java.io.File;

/**
 * Interface for backends which support file transfer from / to GUID.
 * 
 * @version $Date$
 */
public interface GuidBackend {

    /**
     * Checks if this backend can be loaded.
     * 
     * @return true if this backend is available.
     */
    boolean isAvailable();

    /**
     * Upload a file to an LFC.
     * 
     * @param localFile
     *            handle for the file.
     * @return the GUID of the file (without any decoration, e.g. no guid:).
     */
    String upload(File localFile);

    /**
     * Download a file from an LFC.
     * 
     * @param guid
     *            GUID of the file, without decoration.
     * @param file
     *            file to store to.
     */
    void download(String guid, File file);

    /**
     * Delete a file from LFC.
     * 
     * @param guid
     *            GUID of the file, without decoration.
     */
    void delete(String guid);
}
