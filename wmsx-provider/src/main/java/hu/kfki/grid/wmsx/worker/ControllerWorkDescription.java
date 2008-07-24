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

import hu.kfki.grid.wmsx.job.description.JobDescription;
import hu.kfki.grid.wmsx.util.FileUtil;

import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Describes work with additional attributes needed by the controller.
 * 
 * @version $Revision$
 */
public class ControllerWorkDescription {

    private static final int DEFAULT_RETRIES = 5;

    private final WorkDescription workDescription;

    private final long creationTime;

    private final Boolean preferLocal;

    private final Map<String, byte[]> inputSandbox;

    private int retriesLeft;

    /**
     * Default constructor.
     * 
     * @param uid
     *            (worker) backend specific uid.
     * @param jobDesc
     *            work description.
     */
    public ControllerWorkDescription(final Object uid,
            final JobDescription jobDesc) {
        this.inputSandbox = FileUtil.createSandbox(jobDesc
                .getListEntry(JobDescription.INPUTSANDBOX), jobDesc
                .getBaseDir());

        final List<String> args = new Vector<String>();
        final String aString = jobDesc.getStringEntry(JobDescription.ARGUMENTS,
                "");
        for (final String sa : aString.split(" ")) {
            final String sa2 = sa.trim();
            if (sa2.length() > 0) {
                args.add(sa2);
            }
        }

        try {
            this.retriesLeft = Integer.parseInt(jobDesc
                    .getStringEntry(JobDescription.RETRYCOUNT),
                    ControllerWorkDescription.DEFAULT_RETRIES);
        } catch (final NumberFormatException nfe) {
            this.retriesLeft = ControllerWorkDescription.DEFAULT_RETRIES;
        }

        this.workDescription = new WorkDescription(uid, jobDesc
                .getListEntry(JobDescription.OUTPUTSANDBOX), jobDesc
                .getStringEntry(JobDescription.EXECUTABLE), jobDesc
                .getStringEntry(JobDescription.DEPLOY), args, jobDesc
                .getStringEntry(JobDescription.STDOUTPUT), jobDesc
                .getStringEntry(JobDescription.STDERROR), jobDesc
                .getStringEntry(JobDescription.WORKFLOWID));
        this.creationTime = System.currentTimeMillis();
        final String pLocal = jobDesc.getStringEntry(JobDescription.LOCAL);
        if (pLocal != null) {
            this.preferLocal = Boolean.valueOf(pLocal);
        } else {
            this.preferLocal = null;
        }
    }

    /**
     * @return The work description to send to the client.
     */
    public WorkDescription getWorkDescription() {
        return this.workDescription;
    }

    /**
     * @return The time this work description was created.
     */
    public long getCreationTime() {
        return this.creationTime;
    }

    /**
     * @return if local execution is preferred;
     */
    public Boolean getPreferLocal() {
        return this.preferLocal;
    }

    /**
     * @return The input sandbox.
     */
    public Map<String, byte[]> getInputSandbox() {
        return this.inputSandbox;
    }

    /**
     * Decreases the RetryCounter and returns true if the job should be retried.
     * 
     * @return true if retries are left.
     */
    public boolean decreaseRetry() {
        this.retriesLeft--;
        return this.retriesLeft >= 0;
    }
}
