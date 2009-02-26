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

package hu.kfki.grid.wmsx.worker;

import hu.kfki.grid.wmsx.util.Exporter;
import hu.kfki.grid.wmsx.util.LogUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.rmi.RemoteException;
import java.util.logging.Logger;

import net.jini.id.Uuid;
import net.jini.id.UuidFactory;
import at.ac.uibk.dps.wmsx.util.FileServerImpl;

/**
 * Performs work on a remote host.
 * 
 * @version $Date$
 */
public final class WorkerImpl implements Worker {

    private static final int START_DELAY = 5;

    private static final int MSTOSECONDS = 1000;

    private static final int MAX_WAIT = 45 * 60;

    private static final Logger LOGGER = Logger.getLogger(WorkerImpl.class
            .toString());

    private static final int RETRY_COUNT = 5;

    private final Uuid uuid;

    private final Controller controller;

    private final Alive alive;

    private WorkerImpl(final Controller cont, final Uuid id) {
        this.controller = cont;
        this.uuid = id;
        this.alive = new Alive(cont, this.uuid);
    }

    private void logWithTime(final String base) {
        WorkerImpl.LOGGER
                .info(base + " ( " + System.currentTimeMillis() + " )");
    }

    private void start() {
        final WorkPerformer performer = WorkPerformer.getInstance();
        boolean terminate = false;
        long lastChecked = 0;
        long delay = WorkerImpl.START_DELAY;
        int count = WorkerImpl.RETRY_COUNT;
        WorkerImpl.LOGGER.info("Worker started, Uuid is " + this.uuid);
        this.registerWithController();
        try {
            while (!terminate) {
                this.logWithTime("Checking for work");
                try {
                    final WorkDescription todo = this.controller
                            .retrieveWork(this.uuid);

                    if (todo == null) {
                        this.logWithTime("Sleeping");
                        this.alive.stop();
                        lastChecked += delay;
                        if (lastChecked < WorkerImpl.MAX_WAIT) {
                            synchronized (this) {
                                try {
                                    this.wait(delay * WorkerImpl.MSTOSECONDS);
                                } catch (final InterruptedException e) {
                                    // ignore
                                }
                            }
                        }
                        delay += Math.random() / 2.0 * delay;
                        if (lastChecked >= WorkerImpl.MAX_WAIT) {
                            terminate = true;
                        }
                    } else {
                        if ("shutdown".equals(todo.getId())) {
                            terminate = true;
                        } else {
                            this.alive.start();
                            performer.performWork(todo, this.controller,
                                    this.uuid);
                            delay = WorkerImpl.START_DELAY;
                            lastChecked = 0;
                        }
                    }
                } catch (final RemoteException e) {
                    WorkerImpl.LOGGER.warning(e.getMessage());
                    count--;
                    if (count < 0) {
                        throw e;
                    }
                }
            }
        } catch (final RemoteException re) {
            WorkerImpl.LOGGER.warning(re.getMessage());
        }
        this.alive.stop();
        WorkerImpl.LOGGER.info("Unexporting");
        Exporter.getInstance().unexportAll();
    }

    private void registerWithController() {
        try {
            this.controller.registerWorker(this.uuid, (Worker) Exporter
                    .getInstance().export(this));
        } catch (final RemoteException r) {
            WorkerImpl.LOGGER.info(LogUtil.logException(r));
        }
    }

    /**
     * Main Method. To be called from on the client.
     * 
     * @param args
     *            location of the proxyfile
     */
    public static void main(final String[] args) {
        WorkerImpl.LOGGER.info("Initializing worker...");
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new AllSecurityManager());
        }
        try {
            final String proxyFile;
            if (args.length > 0) {
                proxyFile = args[0];
            } else {
                proxyFile = "proxyFile";
            }
            final Uuid id;
            if (args.length > 1) {
                WorkerImpl.LOGGER.info("Using predefined Uuid: " + args[1]);
                id = UuidFactory.create(args[1]);
            } else {
                id = UuidFactory.generate();
            }
            final FileInputStream fis = new FileInputStream(proxyFile);
            final ObjectInputStream in = new ObjectInputStream(fis);
            final Controller comp = (Controller) in.readObject();
            in.close();
            new WorkerImpl(comp, id).start();
        } catch (final IOException e) {
            WorkerImpl.LOGGER.warning(e.getMessage());
        } catch (final ClassNotFoundException e) {
            WorkerImpl.LOGGER.warning(e.getMessage());
        }
        WorkerImpl.LOGGER.info("Shutting down");
    }

    /** {@inheritDoc} */
    public void newWork() throws RemoteException {
        FileServerImpl.getInstance().start();
        synchronized (this) {
            this.notify();
        }
    }
}
