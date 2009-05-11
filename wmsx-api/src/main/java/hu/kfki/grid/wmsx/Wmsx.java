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

package hu.kfki.grid.wmsx;

import java.io.IOException;

/**
 * WMSX Service Interface.
 * 
 * @version $Date$
 */
public interface Wmsx {

    /**
     * Simple ping method.
     * 
     * @return true if the provider is alive.
     * @param remote
     *            if true, the ping is sent to the remote provider.
     * @see hu.kfki.grid.wmsx.provider.IRemoteWmsxProvider#ping()
     */
    boolean ping(boolean remote);

    /**
     * Submit an existing jdl file.
     * 
     * @param jdlFile
     *            name of the file
     * @param outputFile
     *            file where to store stdout if interactive
     * @param resultDir
     *            directory where to retrieve the result to
     * @return a {@link SubmissionResult}.
     * @see hu.kfki.grid.wmsx.provider.IRemoteWmsxProvider#submitJdl(String,
     *      String, String)
     * @throws IOException
     *             If some of the input files cannot be opened.
     */
    SubmissionResult submitJdl(String jdlFile, String outputFile,
            String resultDir) throws IOException;

    /**
     * Submit a list of Laszlo Commands.
     * 
     * @param argFile
     *            File containing the laszlo arguments
     * @param interactive
     *            If true, the jobs are submitted interactive
     * @param name
     *            Name of the LaszloCommand
     * @see hu.kfki.grid.wmsx.provider.IRemoteWmsxProvider#submitLaszlo(List,
     *      boolean, String, String)
     * @throws IOException
     *             If some of the input files cannot be opened.
     */
    void submitLaszlo(String argFile, boolean interactive, String name)
            throws IOException;

    /**
     * Set number of concurrent running jobs.
     * 
     * @param maxJobs
     *            new number of running jobs
     * @see hu.kfki.grid.wmsx.provider.IRemoteWmsxProvider#setMaxJobs(int)
     */
    void setMaxJobs(int maxJobs);

    /**
     * Start a # of workers on the current backend.
     * 
     * @param number
     *            number of workers to start
     * @see hu.kfki.grid.wmsx.provider.IRemoteWmsxProvider#startWorkers(int)
     */
    void startWorkers(int number);

    /**
     * Keep AFS token alive.
     * 
     * @param password
     *            AFS password
     * @return true if pw can be remembered
     * @see hu.kfki.grid.wmsx.provider.IRemoteWmsxProvider#rememberAfs(String)
     */
    boolean rememberAfs(String password);

    /**
     * Keep Grid token alive.
     * 
     * @param password
     *            Grid password
     * @return true if pw can be remembered
     * @see hu.kfki.grid.wmsx.provider.IRemoteWmsxProvider#rememberGrid(String)
     */
    boolean rememberGrid(String password);

    /**
     * Forget AFS password.
     * 
     * @see hu.kfki.grid.wmsx.provider.IRemoteWmsxProvider#forgetAfs()
     */
    void forgetAfs();

    /**
     * Vo to use for all following operations.
     * 
     * @param newVo
     *            new VO.
     * @see hu.kfki.grid.wmsx.provider.IRemoteWmsxProvider#setVo(String)
     */
    void setVo(String newVo);

    /**
     * Backend to use for all following operations.
     * 
     * @param backend
     *            new backend.
     * @see hu.kfki.grid.wmsx.provider.IRemoteWmsxProvider#setBackend(String)
     */
    void setBackend(String backend);

    /**
     * List the available backends.
     * 
     * @return a List&lt;String&gt; with a list of backends.
     * @see hu.kfki.grid.wmsx.provider.IRemoteWmsxProvider#listBackends()
     */
    Iterable<String> listBackends();

    /**
     * Kill all running workers.
     * 
     * @see hu.kfki.grid.wmsx.provider.IRemoteWmsxProvider#shutdownWorkers()
     */
    void shutdownWorkers();
}
