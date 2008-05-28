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

/* $Id$ */

package hu.kfki.grid.wmsx.worker;

import hu.kfki.grid.wmsx.backends.Backend;
import hu.kfki.grid.wmsx.backends.Backends;
import hu.kfki.grid.wmsx.provider.JdlJobFactory;
import hu.kfki.grid.wmsx.provider.JobFactory;
import hu.kfki.grid.wmsx.provider.WmsxProviderImpl;
import hu.kfki.grid.wmsx.util.FileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.rmi.server.ExportException;
import java.util.logging.Logger;

import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.InvocationLayerFactory;
import net.jini.jeri.ServerEndpoint;
import net.jini.jeri.tcp.TcpServerEndpoint;

public final class ControllerServer {

    private static final class SingletonHolder {
        private static final ControllerServer INSTANCE = new ControllerServer();

        private SingletonHolder() {
        }
    }

    private static final Logger LOGGER = Logger
            .getLogger(ControllerServer.class.toString());

    private final ControllerImpl controller;

    private final Controller controllerStub;

    private String jdlPath;

    private File tmpDir;

    private int workerCount;

    private ControllerServer() {

        this.controller = new ControllerImpl();

        final InvocationLayerFactory invocationLayerFactory = new BasicILFactory();

        Controller stub = null;
        int port = GlobusTcp.getInstance().getMinTcp();
        final int max = GlobusTcp.getInstance().getMaxTcp();
        while (stub == null && port <= max) {
            try {
                final ServerEndpoint endpoint = TcpServerEndpoint
                        .getInstance(port);
                stub = (Controller) new BasicJeriExporter(endpoint,
                        invocationLayerFactory, false, true)
                        .export(this.controller);
            } catch (final ExportException e) {
                port++;
                stub = null;
            }

        }
        this.controllerStub = stub;
    }

    public ControllerImpl getControllerImpl() {
        return this.controller;
    }

    public void writeProxy(final File where) throws IOException {
        final FileOutputStream fos = new FileOutputStream(where);
        final ObjectOutputStream out = new ObjectOutputStream(fos);
        out.writeObject(this.controllerStub);
        out.close();
    }

    public void prepareWorker(final File tmpDirForWorker) {
        this.tmpDir = tmpDirForWorker;

        try {
            final File jdlFile = new File(this.tmpDir, "worker.jdl");
            FileUtil.copy(ClassLoader
                    .getSystemResourceAsStream("worker/worker.jdl"), jdlFile);
            FileUtil.copy(ClassLoader
                    .getSystemResourceAsStream("worker/worker.tar.gz"),
                    new File(this.tmpDir, "worker.tar.gz"));
            final File shFile = new File(this.tmpDir, "worker.sh");
            FileUtil.copy(ClassLoader
                    .getSystemResourceAsStream("worker/worker.sh"), shFile);
            try {
                Runtime.getRuntime().exec(
                        new String[] { "/bin/chmod", "+x",
                                shFile.getCanonicalPath(), }).waitFor();
            } catch (final InterruptedException e) {
                // Ignore
            }
            ControllerServer.getInstance().writeProxy(
                    new File(this.tmpDir, "proxyFile"));
            this.jdlPath = jdlFile.getCanonicalPath();
        } catch (final IOException e) {
            ControllerServer.LOGGER.warning(e.toString());
        }
    }

    public void submitWorker(final Backend backend) {
        final Backend submitTo;
        if (Backends.WORKER.equals(backend)) {
            submitTo = Backends.LOCAL;
        } else {
            submitTo = backend;
        }
        if (this.jdlPath != null) {
            this.controller.setShutdownState(false);
            this.workerCount++;
            final JobFactory fac = new JdlJobFactory(this.jdlPath, null,
                    new File(this.tmpDir, Integer.toString(this.workerCount))
                            .getAbsolutePath(), submitTo);
            WmsxProviderImpl.getInstance().addJobFactory(fac);
        } else {
            ControllerServer.LOGGER.warning("Worker not initialized!");
        }
    }

    public void shutdownWorkers() {
        this.controller.setShutdownState(true);
    }

    /**
     * @return Singleton instance.
     */
    public static ControllerServer getInstance() {
        return ControllerServer.SingletonHolder.INSTANCE;
    }

}
