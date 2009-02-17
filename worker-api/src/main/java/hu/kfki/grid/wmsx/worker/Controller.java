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

import net.jini.id.Uuid;

/**
 * Interface to the Controller.
 * 
 * @version $Revision$
 */
public interface Controller extends Remote {
    /**
     * Retrieve a work chunk.
     * 
     * @param uuid
     *            id of the worker.
     * @return description of work to do.
     * @throws RemoteException
     *             if the connection is broken.
     */
    WorkDescription retrieveWork(Uuid uuid) throws RemoteException;

    /**
     * Worker is done with a job.
     * 
     * @param id
     *            id of the work chunk.
     * @param result
     *            the result (output sandbox).
     * @param uuid
     *            id of the worker.
     * @throws RemoteException
     *             if the connection is broken.
     */
    void doneWith(Object id, ResultDescription result, Uuid uuid)
            throws RemoteException;

    /**
     * Worker failed to execute a job.
     * 
     * @param id
     *            id of the work chunk.
     * @param uuid
     *            id of the worker.
     * @throws RemoteException
     *             if the connection is broken.
     */
    void failed(Object id, Uuid uuid) throws RemoteException;

    /**
     * Shows that this worker is still alive.
     * 
     * @param uuid
     *            id of the worker.
     * @throws RemoteException
     *             if the connection is broken.
     */
    void ping(Uuid uuid) throws RemoteException;

    /**
     * Register a worker with the controller.
     * 
     * @param uuid
     *            id of the worker.
     * @param worker
     *            the remote proxy to this worker.
     * @throws RemoteException
     *             if the connection is broken.
     */
    void registerWorker(Uuid uuid, Worker worker) throws RemoteException;

}
