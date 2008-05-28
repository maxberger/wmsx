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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.rmi.RemoteException;
import java.util.logging.Logger;

import net.jini.id.Uuid;
import net.jini.id.UuidFactory;

/**
 * Performs work on a remote host.
 * 
 * @version $Revision$
 */
public final class Worker {

    private static final int START_DELAY = 5;

    private static final int MSTOSECONDS = 1000;

    private static final int MAX_WAIT = 45 * 60;

    private static final Logger LOGGER = Logger.getLogger(Worker.class
            .toString());

    private final Uuid uuid;

    private final Controller controller;

    private final Alive alive;

    private Worker(final Controller cont) {
        this.controller = cont;
        this.uuid = UuidFactory.generate();
        this.alive = new Alive(cont, this.uuid);
    }

    private void logWithTime(final String base) {
        Worker.LOGGER.info(base + " ( " + System.currentTimeMillis() + " )");
    }

    private void start() {
        final WorkPerformer performer = WorkPerformer.getInstance();
        boolean terminate = false;
        long lastChecked = 0;
        long delay = Worker.START_DELAY;
        Worker.LOGGER.info("Worker started, Uuid is " + this.uuid);
        try {
            while (!terminate) {
                this.logWithTime("Checking for work");

                final WorkDescription todo = this.controller
                        .retrieveWork(this.uuid);

                if (todo != null) {

                    if ("shutdown".equals(todo.getId())) {
                        terminate = true;
                    } else {
                        this.alive.start();
                        performer.performWork(todo, this.controller, this.uuid);
                        delay = Worker.START_DELAY;
                        lastChecked = 0;
                    }
                } else {
                    this.logWithTime("Sleeping");
                    this.alive.stop();
                    lastChecked += delay;
                    if (lastChecked < Worker.MAX_WAIT) {
                        try {
                            Thread.sleep(delay * Worker.MSTOSECONDS);
                        } catch (final InterruptedException e) {
                            // ignore
                        }
                    }
                    delay += Math.random() / 2.0 * delay;
                    if (lastChecked >= Worker.MAX_WAIT) {
                        terminate = true;
                    }
                }
            }
        } catch (final RemoteException re) {
            Worker.LOGGER.warning(re.getMessage());
        }
        this.alive.stop();
        Worker.LOGGER.info("Shutting down.");
    }

    /**
     * Main Method. To be called from on the client.
     * 
     * @param args
     *            location of the proxyfile
     */
    public static void main(final String[] args) {
        Worker.LOGGER.info("Initializing worker...");
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
            final FileInputStream fis = new FileInputStream(proxyFile);
            final ObjectInputStream in = new ObjectInputStream(fis);
            final Controller comp = (Controller) in.readObject();
            in.close();
            new Worker(comp).start();
        } catch (final IOException e) {
            Worker.LOGGER.warning(e.getMessage());
        } catch (final ClassNotFoundException e) {
            Worker.LOGGER.warning(e.getMessage());
        }
        Worker.LOGGER.info("Shutting down");
    }
}
