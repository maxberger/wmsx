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

import hu.kfki.grid.wmsx.backends.Backend;
import hu.kfki.grid.wmsx.backends.Backends;
import hu.kfki.grid.wmsx.backends.JobUid;
import hu.kfki.grid.wmsx.backends.local.LocalBackend;
import hu.kfki.grid.wmsx.job.JobWatcher;
import hu.kfki.grid.wmsx.job.description.JDLJobDescription;
import hu.kfki.grid.wmsx.job.description.JobDescription;
import hu.kfki.grid.wmsx.provider.JdlJobFactory;
import hu.kfki.grid.wmsx.provider.JobFactory;
import hu.kfki.grid.wmsx.provider.WmsxProviderImpl;
import hu.kfki.grid.wmsx.util.Exporter;
import hu.kfki.grid.wmsx.util.FileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.logging.Logger;

import net.jini.id.Uuid;
import net.jini.id.UuidFactory;

/**
 * Starter class for an actual worker controller.
 * 
 * @version $Date$
 */
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

    private JobDescription jobDesc;

    private File tmpDir;

    private int workerCount;

    private ControllerServer() {
        this.controller = new ControllerImpl();
        this.controllerStub = (Controller) Exporter.getInstance().export(
                this.controller);
    }

    /**
     * @return the actual Controller
     */
    public ControllerImpl getControllerImpl() {
        return this.controller;
    }

    /**
     * Write out the proxy which connects with the controller.
     * 
     * @param where
     *            file to write to.
     * @throws IOException
     *             if the file cannot be written.
     */
    public void writeProxy(final File where) throws IOException {
        final FileOutputStream fos = new FileOutputStream(where);
        final ObjectOutputStream out = new ObjectOutputStream(fos);
        out.writeObject(this.controllerStub);
        out.close();
    }

    /**
     * Prepare the necessary files for a worker in the given directory.
     * 
     * @param tmpDirForWorker
     *            the directory to use.
     */
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
            FileUtil.makeExecutable(shFile);
            ControllerServer.getInstance().writeProxy(
                    new File(this.tmpDir, "proxyFile"));
            this.jobDesc = new JDLJobDescription(jdlFile);
        } catch (final IOException e) {
            ControllerServer.LOGGER.warning(e.toString());
        }
    }

    private class WorkerStarter implements Runnable {

        private final Backend submitTo;

        public WorkerStarter(final Backend backend) {
            if (WorkerBackend.WORKER.equals(backend.toString())) {
                this.submitTo = Backends.getInstance().get(LocalBackend.LOCAL);
            } else {
                this.submitTo = backend;
            }
        }

        public void run() {
            try {
                final JobDescription jd = ControllerServer.this.jobDesc.clone();
                final JobFactory fac;
                final Uuid uuid = UuidFactory.generate();
                if (LocalBackend.LOCAL.equals(this.submitTo.toString())) {
                    ControllerServer.this.controller.setIsLocal(uuid);
                }
                jd.replaceEntry(JobDescription.ARGUMENTS, "proxyFile " + uuid);
                synchronized (ControllerServer.this) {
                    ControllerServer.this.workerCount++;
                    fac = new JdlJobFactory(
                            jd,
                            null,
                            new File(
                                    ControllerServer.this.tmpDir,
                                    Integer
                                            .toString(ControllerServer.this.workerCount))
                                    .getAbsolutePath(), this.submitTo, 0);
                }
                final JobUid juid = WmsxProviderImpl.getInstance()
                        .reallySubmitFactory(fac);
                JobWatcher.getInstance().addWatch(juid,
                        WorkerListener.getInstance());
            } catch (final CloneNotSupportedException e) {
                ControllerServer.LOGGER.warning("Internal error: "
                        + e.getMessage());
            }

        }
    }

    /**
     * Submit workers to the given backend.
     * 
     * @param backend
     *            Backend to use.
     * @param count
     *            number of workers to submit
     */
    public void submitWorkers(final Backend backend, final int count) {
        if (this.jobDesc != null) {
            this.controller.setShutdownState(false);

            for (int i = 0; i < count; i++) {
                new Thread(new WorkerStarter(backend)).start();
            }
        } else {
            ControllerServer.LOGGER.warning("Worker not initialized!");
        }
    }

    /**
     * Terminate all workers.
     */
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
