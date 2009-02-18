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

import java.rmi.RemoteException;
import java.util.logging.Logger;

import net.jini.id.Uuid;

/**
 * periodically tells server that worker is still alive.
 * 
 * @version $Date$
 * 
 */
public class Alive implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(Alive.class
            .toString());

    private static final int RETRY_COUNT = 5;

    private static final int PING_INTERVAL = 30 * 1000;

    private final Controller controller;

    private final Uuid uuid;

    private boolean started;

    private boolean shutdown;

    /**
     * Default constructor.
     * 
     * @param cont
     *            reference of controller.
     * @param id
     *            my uid.
     */
    public Alive(final Controller cont, final Uuid id) {
        this.controller = cont;
        this.uuid = id;
        this.started = false;
        this.shutdown = false;
    }

    /** {@inheritDoc} */
    public void run() {
        boolean goon = true;
        int count = Alive.RETRY_COUNT;

        while (goon) {
            try {
                Thread.sleep(Alive.PING_INTERVAL);
            } catch (final InterruptedException e) {
                // ignore
            }
            try {
                this.controller.ping(this.uuid);
            } catch (final RemoteException e) {
                Alive.LOGGER.warning(e.getMessage());
                count--;
                if (count <= 0) {
                    synchronized (this) {
                        this.shutdown = true;
                    }
                }
            }
            synchronized (this) {
                if (this.shutdown) {
                    this.started = false;
                    this.shutdown = false;
                    goon = false;
                }
            }
        }
    }

    /**
     * Start alive notice.
     */
    public synchronized void start() {
        if (!this.started) {
            new Thread(this).start();
            this.started = true;
        }

    }

    /**
     * Shutdown alive notice.
     */
    public synchronized void stop() {
        this.shutdown = true;
    }
}
