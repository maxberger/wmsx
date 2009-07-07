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

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Public interface for serving VirtualFiles.
 * 
 * @version $Date$
 */
public interface FileServer extends Remote {

    /**
     * Retrieve a File from this server.
     * 
     * @param name
     *            Name of the file
     * @return file content
     * @throws RemoteException
     *             if the connection fails.
     */
    byte[] retrieveFile(String name) throws RemoteException;

    /**
     * Dummy ping for connection test.
     * 
     * @throws RemoteException
     *             if the connection fails.
     */
    void ping() throws RemoteException;
}
