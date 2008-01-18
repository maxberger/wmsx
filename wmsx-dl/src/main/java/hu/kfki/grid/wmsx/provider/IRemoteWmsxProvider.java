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
 * 
 */

/* $Id: vasblasd$ */

package hu.kfki.grid.wmsx.provider;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Remote version of Jini service.
 * 
 * @version $Revision$
 */
public interface IRemoteWmsxProvider extends Serializable, Remote {

    /**
     * Describes an arglist command.
     * 
     * @version $Revision$
     */
    static class LaszloCommand implements Serializable {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        private final String command;

        private final String args;

        public LaszloCommand(final String _commandWithPath, final String _args) {
            this.command = _commandWithPath;
            this.args = _args;
        }

        public String getArgs() {
            return this.args;
        }

        public String getCommand() {
            return this.command;
        }

    }

    void ping() throws RemoteException;

    void startWorkers(int number) throws RemoteException;

    String submitJdl(String jdlFile, String output, String resultDir)
            throws RemoteException;

    void submitLaszlo(List commands, boolean interactive, String prefix,
            String name) throws RemoteException;

    void setMaxJobs(int maxJobs) throws RemoteException;

    boolean rememberAfs(String password) throws RemoteException;

    boolean rememberGrid(String password) throws RemoteException;

    void forgetAfs() throws RemoteException;

    void setVo(String newVo) throws RemoteException;

    void setBackend(String backend) throws RemoteException;

}
