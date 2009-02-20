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

/**
 * Contains information about 1 worker client.
 * 
 * @version $Date$
 */
public class WorkerInfo {

    private static final int RETRIES_START = 3;

    private long lastSeen;

    private boolean local;

    private Worker proxy;

    private int retriesLeft;

    /**
     * Default constructor.
     */
    public WorkerInfo() {
        this.retriesLeft = WorkerInfo.RETRIES_START;
        this.updateLastSeen();
    }

    /**
     * Update the last seen information to now.
     */
    public void updateLastSeen() {
        this.lastSeen = System.currentTimeMillis();
    }

    /**
     * @return the lastSeen
     */
    public long getLastSeen() {
        return this.lastSeen;
    }

    /**
     * Set Local to true.
     */
    public void setIsLocal() {
        this.local = true;
    }

    /**
     * @return true if this worker is local.
     */
    public boolean getLocal() {
        return this.local;
    }

    /**
     * @param worker
     *            the proxy for this worker.
     */
    public void setProxy(final Worker worker) {
        this.proxy = worker;
    }

    /**
     * @return the proxy for this worker.
     */
    public Worker getProxy() {
        return this.proxy;
    }

    /**
     * Decrease the number of retries left.
     */
    public void decreaseRetries() {
        this.retriesLeft--;
    }

    /**
     * Reset the number of retries.
     */
    public void resetRetries() {
        this.retriesLeft = WorkerInfo.RETRIES_START;
    }

    /**
     * @return true if this worker has retries.
     */
    public boolean hasRetries() {
        return this.retriesLeft >= 0;
    }
}
