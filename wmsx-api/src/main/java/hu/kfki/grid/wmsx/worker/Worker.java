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

package hu.kfki.grid.wmsx.worker;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface for workers supporting push notification.
 * 
 * @version $Date$
 */
public interface Worker extends Remote {

    /**
     * Notification that new work is available.
     * 
     * @throws RemoteException
     *             if the connection is broken.
     */
    void newWork() throws RemoteException;

    /**
     * Cancel the given job if it is running.
     * 
     * @param id
     *            Internal Id of the job.
     * @throws RemoteException
     *             if the connection is broken.
     */
    void cancel(Object id) throws RemoteException;

}
