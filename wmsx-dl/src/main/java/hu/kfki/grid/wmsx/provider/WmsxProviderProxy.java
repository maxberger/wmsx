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

import hu.kfki.grid.wmsx.JobInfo;
import hu.kfki.grid.wmsx.SubmissionResult;
import hu.kfki.grid.wmsx.TransportJobUID;
import hu.kfki.grid.wmsx.Wmsx;
import hu.kfki.grid.wmsx.provider.IRemoteWmsxProvider.LaszloCommand;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import net.jini.admin.Administrable;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.lease.Lease;
import net.jini.id.Uuid;

/**
 * A smart proxy which wraps the remote Jini service calls.
 * 
 * @version $Date$
 */
// CHECKSTYLE:OFF
// Data Abstraction coupling is too high, but it is needed.
public class WmsxProviderProxy implements Serializable, Wmsx, Administrable {
    // CHECKSTYLE:ON

    private static final String FILE_NOT_FOUND = "File not Found: ";

    private static final long serialVersionUID = 3L;

    private static final Logger LOGGER = Logger
            .getLogger(WmsxProviderProxy.class.toString());

    private final IRemoteWmsxProvider remoteService;

    /**
     * Default Constructor.
     * 
     * @param remote
     *            Actual Proxy of the Remote Service.
     */
    public WmsxProviderProxy(final Remote remote) {
        this.remoteService = (IRemoteWmsxProvider) remote;
    }

    /** {@inheritDoc} */
    public SubmissionResult submitJdl(final String jdlFile,
            final String output, final String resultDir) throws IOException {
        try {
            final File f = new File(jdlFile);
            if (!f.exists()) {
                throw new FileNotFoundException(
                        WmsxProviderProxy.FILE_NOT_FOUND + jdlFile);
            }
            final String outputPath;
            if (output != null) {
                final File f2 = new File(output);
                if (f2.exists()) {
                    throw new IOException("File exists: " + output);
                }
                outputPath = f2.getCanonicalPath();
            } else {
                outputPath = null;
            }
            final String resultPath;
            if (resultDir != null) {
                resultPath = new File(resultDir).getCanonicalPath();
            } else {
                resultPath = null;
            }

            return this.remoteService.submitJdl(f.getCanonicalPath(),
                    outputPath, resultPath);
        } catch (final RemoteException re) {
            WmsxProviderProxy.LOGGER.warning(re.getMessage());
            return new SubmissionResult(re.toString());
        }
    }

    /** {@inheritDoc} */
    public Object getAdmin() throws RemoteException {
        return this.remoteService;
    }

    /** {@inheritDoc} */
    public void setMaxJobs(final int maxJobs) {
        try {
            this.remoteService.setMaxJobs(maxJobs);
        } catch (final RemoteException re) {
            WmsxProviderProxy.LOGGER.warning(re.getMessage());
        }
    }

    /** {@inheritDoc} */
    public void startWorkers(final int num) {
        if (num < 1) {
            return;
        }
        try {
            this.remoteService.startWorkers(num);
        } catch (final RemoteException re) {
            WmsxProviderProxy.LOGGER.warning(re.getMessage());
        }
    }

    /** {@inheritDoc} */
    public boolean ping(final boolean remote) {
        if (remote) {
            try {
                this.remoteService.ping();
            } catch (final RemoteException re) {
                WmsxProviderProxy.LOGGER.fine(re.getMessage());
                return false;
            }
        }
        return true;
    }

    /** {@inheritDoc} */
    public void submitLaszlo(final String argFile, final boolean interactive,
            final String name) throws IOException {
        final File f = new File(argFile).getCanonicalFile();
        if (!f.exists()) {
            throw new FileNotFoundException(WmsxProviderProxy.FILE_NOT_FOUND
                    + argFile);
        }
        final List<LaszloCommand> commands = new ArrayList<LaszloCommand>();
        final FileReader freader = new FileReader(f);
        final BufferedReader reader = new BufferedReader(freader);
        String line = reader.readLine();
        while (line != null) {
            line = line.trim();
            if (line.length() > 0 && line.charAt(0) != '#') {
                final int spacePos = line.indexOf(' ');
                final String command;
                final String args;
                if (spacePos < 0) {
                    command = line;
                    args = "";
                } else {
                    command = line.substring(0, spacePos);
                    args = line.substring(spacePos + 1).trim();
                }
                final File cmdWithPath = new File(f.getParentFile(), command);
                commands.add(new IRemoteWmsxProvider.LaszloCommand(cmdWithPath
                        .getAbsolutePath(), args));

            }
            line = reader.readLine();
        }
        if (commands.isEmpty()) {
            WmsxProviderProxy.LOGGER.warning("List of commands is empty!");
        } else {
            this.remoteService.submitLaszlo(commands, interactive, null, name);
        }
    }

    /** {@inheritDoc} */
    public void forgetAfs() {
        try {
            this.remoteService.forgetAfs();
        } catch (final RemoteException re) {
            WmsxProviderProxy.LOGGER.warning(re.getMessage());
        }
    }

    /** {@inheritDoc} */
    public boolean rememberAfs(final String password) {
        try {
            return this.remoteService.rememberAfs(password);
        } catch (final RemoteException re) {
            WmsxProviderProxy.LOGGER.warning(re.getMessage());
            return false;
        }
    }

    /** {@inheritDoc} */
    public boolean rememberGrid(final String password) {
        try {
            return this.remoteService.rememberGrid(password);
        } catch (final RemoteException re) {
            WmsxProviderProxy.LOGGER.warning(re.getMessage());
            return false;
        }
    }

    /** {@inheritDoc} */
    public void setVo(final String newVo) {
        try {
            this.remoteService.setVo(newVo);
        } catch (final RemoteException re) {
            WmsxProviderProxy.LOGGER.warning(re.getMessage());
        }
    }

    /** {@inheritDoc} */
    public void setBackend(final String backend) {
        try {
            this.remoteService.setBackend(backend);
        } catch (final RemoteException re) {
            WmsxProviderProxy.LOGGER.warning(re.getMessage());
        }
    }

    /** {@inheritDoc} */
    public Iterable<String> listBackends() {
        Iterable<String> list = Collections.emptyList();
        try {
            list = this.remoteService.listBackends();
        } catch (final RemoteException re) {
            WmsxProviderProxy.LOGGER.warning(re.getMessage());
        }
        return list;
    }

    /** {@inheritDoc} */
    public void shutdownWorkers() {
        try {
            this.remoteService.shutdownWorkers();
        } catch (final RemoteException re) {
            WmsxProviderProxy.LOGGER.warning(re.getMessage());
        }
    }

    /** {@inheritDoc} */
    public void cancelJob(final TransportJobUID jobId) {
        try {
            this.remoteService.cancelJob(jobId);
        } catch (final RemoteException re) {
            WmsxProviderProxy.LOGGER.warning(re.getMessage());
        }
    }

    /** {@inheritDoc} */
    public JobInfo getJobInfo(final TransportJobUID jobId) {
        try {
            return this.remoteService.getJobInfo(jobId);
        } catch (final RemoteException re) {
            WmsxProviderProxy.LOGGER.warning(re.getMessage());
        }
        return null;
    }

    /** {@inheritDoc} */
    public Iterable<TransportJobUID> listJobs() {
        Iterable<TransportJobUID> list = Collections.emptyList();
        try {
            list = this.remoteService.listJobs();
        } catch (final RemoteException re) {
            WmsxProviderProxy.LOGGER.warning(re.getMessage());
        }
        return list;
    }

    /** {@inheritDoc} */
    public Lease registerEventListener(final RemoteEventListener r) {
        try {
            return this.remoteService.registerEventListener(r);
        } catch (final RemoteException re) {
            WmsxProviderProxy.LOGGER.warning(re.getMessage());
        }
        return null;
    }

    /** {@inheritDoc} */
    public void shutdownWorker(final Uuid workerId) {
        try {
            this.remoteService.shutdownWorker(workerId);
        } catch (final RemoteException re) {
            WmsxProviderProxy.LOGGER.warning(re.getMessage());
        }
    }

}
