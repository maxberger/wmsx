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

import hu.kfki.grid.wmsx.backends.JobUid;
import hu.kfki.grid.wmsx.job.JobListener;

import java.util.logging.Logger;

/**
 * Listens to status changes of submitted workers.
 * 
 * @version $Revision$
 */
public final class WorkerListener implements JobListener {

    private int pending;

    private int running;

    private static final class SingletonHolder {
        private static final WorkerListener INSTANCE = new WorkerListener();

        private SingletonHolder() {
            // empty on purpose.
        }
    }

    private static final Logger LOGGER = Logger.getLogger(WorkerListener.class
            .toString());

    private WorkerListener() {
        // Add code if needed
    }

    /**
     * @return the Singleton Instance.
     */
    public static WorkerListener getInstance() {
        return WorkerListener.SingletonHolder.INSTANCE;
    }

    private void display() {
        WorkerListener.LOGGER.info("Workerstatus: " + this.pending
                + " Pending, " + this.running + " Running");
    }

    /** {@inheritDoc} */
    public void done(final JobUid id, final boolean success) {
        synchronized (this) {
            this.running--;
            this.display();
        }
    }

    /** {@inheritDoc} */
    public void running(final JobUid id) {
        synchronized (this) {
            this.pending--;
            this.running++;
            this.display();
        }
    }

    /** {@inheritDoc} */
    public void startup(final JobUid id) {
        synchronized (this) {
            this.pending++;
            this.display();
        }
    }

}
