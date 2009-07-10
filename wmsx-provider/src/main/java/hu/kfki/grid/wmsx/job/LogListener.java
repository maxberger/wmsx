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

package hu.kfki.grid.wmsx.job;

import hu.kfki.grid.wmsx.backends.JobUid;

import java.util.logging.Logger;

/**
 * A listener which just records log messages.
 * 
 * @version $Date$
 */
public final class LogListener implements JobListener {
    private static final Logger LOGGER = Logger.getLogger(LogListener.class
            .toString());

    private static final class SingletonHolder {
        private static final LogListener INSTANCE = new LogListener();

        private SingletonHolder() {
        }
    }

    private LogListener() {
    }

    /**
     * @return the singleton instance.
     */
    public static LogListener getInstance() {
        return LogListener.SingletonHolder.INSTANCE;
    }

    /** {@inheritDoc} */
    public void done(final JobUid jobId, final boolean success) {
        if (success) {
            LogListener.LOGGER.info("DONE/SUCCESS: " + jobId);
        } else {
            LogListener.LOGGER.info("DONE/FAILED: " + jobId);
        }
    }

    /** {@inheritDoc} */
    public void running(final JobUid jobId) {
        LogListener.LOGGER.info("RUNNING: " + jobId);
    }

    /** {@inheritDoc} */
    public void startup(final JobUid jobId) {
        LogListener.LOGGER.info("STARTUP: " + jobId);
    }

}
