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

import java.io.Serializable;
import java.util.Date;

import net.jini.id.Uuid;

/**
 * Describes Information about a job.
 * 
 * @version $Date: 1/1/2000$
 */
public class JobInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String siteId;

    private boolean isWorker;

    private Uuid workerId;

    private Date creationTime;

    private Date startRunningTime;

    private Date doneRunningTime;

    private String executable;

    private String description;

    private String output;

    private TransportJobUID jobId;

    private JobState status;

    /**
     * Default constructor.
     * 
     * @param site
     *            Site this job was scheduled to.
     */
    public JobInfo(final String site) {
        this.siteId = site;
    }

    /**
     * @return the siteId
     */
    public String getSiteId() {
        return this.siteId;
    }

}
