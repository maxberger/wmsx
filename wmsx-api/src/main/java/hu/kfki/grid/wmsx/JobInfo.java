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

    private static final long serialVersionUID = 2L;

    private String siteId = "";

    private boolean isWorker;

    private Uuid workerId;

    private Date creationTime;

    private Date startRunningTime;

    private Date doneRunningTime;

    private String executable = "";

    private String description = "";

    private String output = "";

    private final TransportJobUID jobId;

    private JobState status = JobState.NONE;

    /**
     * Default constructor.
     * 
     * @param id
     *            The JobID.
     */
    public JobInfo(final TransportJobUID id) {
        this.jobId = id;
    }

    /**
     * @return the siteId
     */
    public String getSiteId() {
        return this.siteId;
    }

    /**
     * @return the isWorker
     */
    public boolean isWorker() {
        return this.isWorker;
    }

    /**
     * @param nisWorker
     *            the isWorker to set
     */
    public void setWorker(final boolean nisWorker) {
        this.isWorker = nisWorker;
    }

    /**
     * @return the workerId
     */
    public Uuid getWorkerId() {
        return this.workerId;
    }

    /**
     * @param nworkerId
     *            the workerId to set
     */
    public void setWorkerId(final Uuid nworkerId) {
        this.workerId = nworkerId;
    }

    /**
     * @return the creationTime
     */
    public Date getCreationTime() {
        if (this.creationTime == null) {
            return null;
        } else {
            return (Date) this.creationTime.clone();
        }
    }

    /**
     * @param ncreationTime
     *            the creationTime to set. Must not be null.
     */
    public void setCreationTime(final Date ncreationTime) {
        this.creationTime = (Date) ncreationTime.clone();
    }

    /**
     * @return the startRunningTime
     */
    public Date getStartRunningTime() {
        if (this.startRunningTime == null) {
            return null;
        } else {
            return (Date) this.startRunningTime.clone();
        }
    }

    /**
     * @param nstartRunningTime
     *            the startRunningTime to set. Must not be null.
     */
    public void setStartRunningTime(final Date nstartRunningTime) {
        this.startRunningTime = (Date) nstartRunningTime.clone();
    }

    /**
     * @return the doneRunningTime
     */
    public Date getDoneRunningTime() {
        if (this.doneRunningTime == null) {
            return null;
        } else {
            return (Date) this.doneRunningTime.clone();
        }
    }

    /**
     * @param ndoneRunningTime
     *            the doneRunningTime to set. Must not be null.
     */
    public void setDoneRunningTime(final Date ndoneRunningTime) {
        this.doneRunningTime = (Date) ndoneRunningTime.clone();
    }

    /**
     * @return the executable
     */
    public String getExecutable() {
        return this.executable;
    }

    /**
     * @param nexecutable
     *            the executable to set
     */
    public void setExecutable(final String nexecutable) {
        this.executable = nexecutable;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * @param ndescription
     *            the description to set
     */
    public void setDescription(final String ndescription) {
        this.description = ndescription;
    }

    /**
     * @return the output
     */
    public String getOutput() {
        return this.output;
    }

    /**
     * @param noutput
     *            the output to set
     */
    public void setOutput(final String noutput) {
        this.output = noutput;
    }

    /**
     * @return the status
     */
    public JobState getStatus() {
        return this.status;
    }

    /**
     * @param nstatus
     *            the status to set
     */
    public void setStatus(final JobState nstatus) {
        this.status = nstatus;
    }

    /**
     * @return the jobId
     */
    public TransportJobUID getJobId() {
        return this.jobId;
    }

    /**
     * @param nsiteId
     *            the siteId to set
     */
    public void setSiteId(final String nsiteId) {
        this.siteId = nsiteId;
    }

}
