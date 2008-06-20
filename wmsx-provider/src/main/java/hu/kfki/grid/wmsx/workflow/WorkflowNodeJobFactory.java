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

package hu.kfki.grid.wmsx.workflow;

import hu.kfki.grid.wmsx.provider.JdlJob;
import hu.kfki.grid.wmsx.provider.JobFactory;

import java.io.File;

/**
 * Create a new (next) node in an existing workflow.
 * 
 * @version $Revision$
 */
public class WorkflowNodeJobFactory implements JobFactory {

    private final Workflow workflow;

    private final String name;

    /**
     * Default constructor.
     * 
     * @param workflo
     *            Workflow to attach this node to
     * @param node
     *            name of the node.
     */
    public WorkflowNodeJobFactory(final Workflow workflo, final String node) {
        this.workflow = workflo;
        this.name = node;
    }

    /** {@inheritDoc} */
    public JdlJob createJdlJob() {
        final File workdir = this.workflow.getDirectory();
        return new JdlJob(new File(workdir, this.name).getAbsolutePath(), null,
                workdir.getAbsolutePath(), this.workflow, this.workflow
                        .getBackend(), this.workflow.getApplicationId());
    }

}
