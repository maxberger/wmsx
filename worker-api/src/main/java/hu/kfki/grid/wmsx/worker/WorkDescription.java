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

/* $Id: vasblasd$ */

package hu.kfki.grid.wmsx.worker;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Describes the work sent to worker.
 * 
 * @version $Revision$
 */
public class WorkDescription implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final Object id;

    private final Map<String, byte[]> inputSandbox;

    private final String executable;

    private final String deploy;

    private final List<String> arguments;

    private final String stdout;

    private final String stderr;

    private final String workflowId;

    /* <String> */
    private final List<String> outputSandbox;

    /**
     * Create a new work description.
     * 
     * @param jobId
     *            Id of the job.
     * @param input
     *            Input Sandbox.
     * @param output
     *            List of files for output sandbox.
     * @param exec
     *            Name of executable.
     * @param deployexec
     *            Name of executable for deployment or null.
     * @param arg
     *            Arguments for executable.
     * @param out
     *            Name of file to store Stdout in.
     * @param err
     *            Name of file to store Stderr in.
     * @param wfId
     *            Id of the workflow or null.
     */
    public WorkDescription(final Object jobId, final Map<String, byte[]> input,
            final List<String> output, final String exec,
            final String deployexec, final List<String> arg, final String out,
            final String err, final String wfId) {
        this.id = jobId;
        this.inputSandbox = input;
        this.outputSandbox = output;
        this.executable = exec;
        this.deploy = deployexec;
        this.arguments = arg;
        this.stdout = out;
        this.stderr = err;
        this.workflowId = wfId;
    }

    /**
     * @return JobId for this job.
     */
    public Object getId() {
        return this.id;
    }

    public Map<String, byte[]> getInputSandbox() {
        return this.inputSandbox;
    }

    /**
     * @return executable to call
     */
    public String getExecutable() {
        return this.executable;
    }

    /**
     * @return deployment executable to call
     */
    public String getDeploy() {
        return this.deploy;
    }

    /**
     * @return file used to store stdout
     */
    public String getStdout() {
        return this.stdout;
    }

    /**
     * @return file used to store stderr
     */
    public String getStderr() {
        return this.stderr;
    }

    /**
     * @return files to send back to the caller.
     */
    public List<String> getOutputSandbox() {
        return this.outputSandbox;
    }

    /**
     * @return command line arguments.
     */
    public List<String> getArguments() {
        return this.arguments;
    }

    /**
     * @return id of the workflow, or null
     */
    public String getWorkflowId() {
        return this.workflowId;
    }

}
