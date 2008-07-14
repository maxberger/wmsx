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

package hu.kfki.grid.wmsx.backends;

/**
 * Carries the results from a job submission back to the backend.
 * 
 * @version $Revision$
 */
public class SubmissionResults {
    private final JobUid jobId;

    private final String iStream;

    private final String oStream;

    private final String eStream;

    private final int shadowpid;

    private final int port;

    public SubmissionResults(final JobUid jobUid, final String stream,
            final String stream2, final String stream3, final int pid,
            final int portNum) {
        super();
        this.jobId = jobUid;
        this.iStream = stream;
        this.oStream = stream2;
        this.eStream = stream3;
        this.shadowpid = pid;
        this.port = portNum;
    }

    public SubmissionResults(final JobUid jobUid) {
        this(jobUid, null, null, null, 0, 0);
    }

    public String getEStream() {
        return this.eStream;
    }

    public String getIStream() {
        return this.iStream;
    }

    public JobUid getJobId() {
        return this.jobId;
    }

    public String getOStream() {
        return this.oStream;
    }

    public int getShadowpid() {
        return this.shadowpid;
    }

    public int getPort() {
        return this.port;
    }

}
