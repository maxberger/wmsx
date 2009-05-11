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

import java.io.Serializable;

/**
 * Data class for job submission results.
 * 
 * @version $Date: 1/1/2000$
 */
public final class SubmissionResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private final TransportJobUID jobUid;

    private final String errorMessage;

    /**
     * Constructor for successful job Submission.
     * 
     * @param job
     *            The {@link TransportJobUID} of the Job.
     */
    public SubmissionResult(final TransportJobUID job) {
        this.jobUid = job;
        this.errorMessage = null;
    }

    /**
     * Constructor for failed job submission.
     * 
     * @param error
     *            Error Message.
     */
    public SubmissionResult(final String error) {
        this.jobUid = null;
        this.errorMessage = error;
    }

    /**
     * @return the jobUid
     */
    public TransportJobUID getJobUid() {
        return this.jobUid;
    }

    /**
     * @return the errorMessage
     */
    public String getErrorMessage() {
        return this.errorMessage;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        if (this.jobUid == null) {
            return this.errorMessage;
        } else {
            return this.jobUid.toString();
        }
    }
}
