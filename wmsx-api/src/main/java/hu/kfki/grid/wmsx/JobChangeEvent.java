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

import hu.kfki.grid.wmsx.job.JobState;
import net.jini.core.event.RemoteEvent;

/**
 * Triggered when a Job has changed state.
 * 
 * @version $Date: 1/1/2000$
 */
public final class JobChangeEvent extends RemoteEvent {

    private static final long serialVersionUID = 2L;

    private final JobState state;

    /**
     * Creates a new JobChangedEvent.
     * 
     * @param jobUID
     *            Id of the Job
     * @param newState
     *            the new state
     * @param snum
     *            serial number of the event
     */
    public JobChangeEvent(final TransportJobUID jobUID,
            final JobState newState, final int snum) {
        super(jobUID, 1, snum, null);
        this.state = newState;
    }

    /**
     * @return the JobID.
     */
    public TransportJobUID getJobUid() {
        return (TransportJobUID) this.getSource();
    }

    /**
     * @return the new Job State.
     */
    public JobState getState() {
        return this.state;
    }

}
