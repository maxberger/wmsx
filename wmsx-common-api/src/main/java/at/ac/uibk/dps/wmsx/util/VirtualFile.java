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

import java.io.File;
import java.io.Serializable;

/**
 * Management for virtual files.
 * 
 * @version $Date$
 */
public interface VirtualFile extends Serializable {

    /**
     * Retrieve the filename.
     * 
     * @return Filename without any path components.
     */
    String getName();

    /**
     * Stores the file back onto the file system under its name.
     * 
     * @param dir
     *            Directory to store to.
     */
    void storeFile(final File dir);

    /**
     * Delete all created temporary artifacts.
     */
    void deleteTemp();

}
