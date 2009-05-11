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

package hu.kfki.grid.wmsx.provider;

import hu.kfki.grid.wmsx.SubmissionResult;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Remote version of Jini service.
 * 
 * @version $Date$
 * @see hu.kfki.grid.wmsx.Wmsx
 */
public interface IRemoteWmsxProvider extends Remote {

    /**
     * Describes an arglist command.
     * 
     * @version $Date$
     */
    static class LaszloCommand implements Serializable {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        private final String command;

        private final String args;

        public LaszloCommand(final String commandWithPath,
                final String arguments) {
            this.command = commandWithPath;
            this.args = arguments;
        }

        public String getArgs() {
            return this.args;
        }

        public String getCommand() {
            return this.command;
        }

    }

    /**
     * Simple ping method.
     * 
     * @throws RemoteException
     *             if the connection is broken.
     * @see hu.kfki.grid.wmsx.Wmsx#ping(boolean)
     */
    void ping() throws RemoteException;

    /**
     * Start a # of workers on the current backend.
     * 
     * @param number
     *            number of workers to start
     * @throws RemoteException
     *             if the connection is broken.
     * @see hu.kfki.grid.wmsx.Wmsx#startWorkers(int)
     */
    void startWorkers(int number) throws RemoteException;

    /**
     * Submit an existing jdl file.
     * 
     * @param jdlFile
     *            name of the file
     * @param output
     *            file where to store stdout if interactive
     * @param resultDir
     *            directory where to retrieve the result to
     * @return a {@link SubmissionResult}.
     * @throws RemoteException
     *             if the connection is broken.
     * @see hu.kfki.grid.wmsx.Wmsx#submitJdl(String, String, String)
     */
    SubmissionResult submitJdl(String jdlFile, String output, String resultDir)
            throws RemoteException;

    /**
     * Submit a list of Laszlo Commands.
     * 
     * @param commands
     *            The list of commands to submit
     * @param interactive
     *            If true, the jobs are submitted interactive
     * @param prefix
     *            common Prefix for the whole job
     * @param name
     *            Name of the LaszloCommand
     * @throws RemoteException
     *             if the connection is broken.
     * @see hu.kfki.grid.wmsx.Wmsx#submitLaszlo(String, boolean, String)
     */
    void submitLaszlo(List<IRemoteWmsxProvider.LaszloCommand> commands,
            boolean interactive, String prefix, String name)
            throws RemoteException;

    /**
     * Set number of concurrent running jobs.
     * 
     * @param maxJobs
     *            new number of running jobs
     * @throws RemoteException
     *             if the connection is broken.
     * @see hu.kfki.grid.wmsx.Wmsx#setMaxJobs(int)
     */
    void setMaxJobs(int maxJobs) throws RemoteException;

    /**
     * Keep AFS token alive.
     * 
     * @param password
     *            AFS password
     * @return true if pw can be remembered
     * @throws RemoteException
     *             if the connection is broken.
     * @see hu.kfki.grid.wmsx.Wmsx#rememberAfs(String)
     */
    boolean rememberAfs(String password) throws RemoteException;

    /**
     * Keep Grid token alive.
     * 
     * @param password
     *            Grid password
     * @return true if pw can be remembered
     * @throws RemoteException
     *             if the connection is broken.
     * @see hu.kfki.grid.wmsx.Wmsx#rememberGrid(String)
     */
    boolean rememberGrid(String password) throws RemoteException;

    /**
     * Forget AFS password.
     * 
     * @throws RemoteException
     *             if the connection is broken.
     * @see hu.kfki.grid.wmsx.Wmsx#forgetAfs()
     */
    void forgetAfs() throws RemoteException;

    /**
     * Vo to use for all following operations.
     * 
     * @param newVo
     *            new VO.
     * @throws RemoteException
     *             if the connection is broken.
     * @see hu.kfki.grid.wmsx.Wmsx#setVo(String)
     */
    void setVo(String newVo) throws RemoteException;

    /**
     * Backend to use for all following operations.
     * 
     * @param backend
     *            new backend.
     * @throws RemoteException
     *             if the connection is broken.
     * @see hu.kfki.grid.wmsx.Wmsx#setBackend(String)
     */
    void setBackend(String backend) throws RemoteException;

    /**
     * List the available backends.
     * 
     * @return a List of backend names.
     * @throws RemoteException
     *             if the connection is broken.
     * @see hu.kfki.grid.wmsx.Wmsx#listBackends()
     */
    Iterable<String> listBackends() throws RemoteException;

    /**
     * Kill all running workers.
     * 
     * @throws RemoteException
     *             if the connection is broken.
     * @see hu.kfki.grid.wmsx.Wmsx#shutdownWorkers()
     */
    void shutdownWorkers() throws RemoteException;
}
