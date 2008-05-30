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
 * 
 */

/* $Id$ */

package hu.kfki.grid.wmsx.provider;

import hu.kfki.grid.wmsx.backends.Backend;

public class JdlJobFactory implements JobFactory {
    private final String jdlFile;

    private final String output;

    private final String result;

    private final int appId;

    private Backend backend;

    /**
     * @param jdl
     *            path to JDL file
     * @param out
     *            path to stdout file for interactive jobs
     * @param resultDir
     *            path to directory for retrieving results.
     * @param back
     *            backend to use.
     * @param applicationId
     *            Application if the JDL file represents a workflow.
     */
    public JdlJobFactory(final String jdl, final String out,
            final String resultDir, final Backend back, final int applicationId) {
        this.jdlFile = jdl;
        this.output = out;
        this.result = resultDir;
        this.backend = back;
        this.appId = applicationId;
    }

    public JdlJob createJdlJob() {
        return new JdlJob(this.jdlFile, this.output, this.result, null,
                this.backend, this.appId);
    }

    public Backend getBackend() {
        return this.backend;
    }

    public void setBackend(final Backend newBackend) {
        this.backend = newBackend;
    }

}
