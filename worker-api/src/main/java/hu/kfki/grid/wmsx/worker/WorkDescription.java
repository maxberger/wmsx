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

public class WorkDescription implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final Object id;

    /* <String,byte[]> */
    private final Map inputSandbox;

    private final String executable;

    /* <String> */
    private final List arguments;

    private final String stdout;

    private final String stderr;

    /* <String> */
    private final List outputSandbox;

    public WorkDescription(final Object _id, final Map input,
            final List output, final String exec, final List arg,
            final String out, final String err) {
        this.id = _id;
        this.inputSandbox = input;
        this.outputSandbox = output;
        this.executable = exec;
        this.arguments = arg;
        this.stdout = out;
        this.stderr = err;
    }

    public Object getId() {
        return this.id;
    }

    public Map getInputSandbox() {
        return this.inputSandbox;
    }

    public String getExecutable() {
        return this.executable;
    }

    public String getStdout() {
        return this.stdout;
    }

    public String getStderr() {
        return this.stderr;
    }

    public List getOutputSandbox() {
        return this.outputSandbox;
    }

    public List getArguments() {
        return this.arguments;
    }

}
