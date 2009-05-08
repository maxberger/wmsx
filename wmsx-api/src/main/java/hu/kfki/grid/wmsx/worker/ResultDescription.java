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

package hu.kfki.grid.wmsx.worker;

import java.io.Serializable;
import java.util.List;

import at.ac.uibk.dps.wmsx.util.VirtualFile;

/**
 * Describes the result sent back to the controller.
 * 
 * @version $Date$
 */
public class ResultDescription implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final List<VirtualFile> outputSandbox;

    /**
     * Default constructor.
     * 
     * @param output
     *            Output sandbox.
     */
    public ResultDescription(final List<VirtualFile> output) {
        this.outputSandbox = output;
    }

    /**
     * @return the output sandbox.
     */
    public List<VirtualFile> getOutputSandbox() {
        return this.outputSandbox;
    }

}
