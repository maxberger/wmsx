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

package hu.kfki.grid.wmsx.workflow;

import hu.kfki.grid.wmsx.backends.Backend;

import java.io.File;

/**
 * Creates instances of {@link Workflow}.
 * 
 * @version $Date$
 */
public final class WorkflowFactory {

    private int currentId;

    private WorkflowFactory() {
    }

    private static final class SingletonHolder {
        private static final WorkflowFactory INSTANCE = new WorkflowFactory();

        private SingletonHolder() {
        }
    }

    /**
     * @return singleton instance if this class.
     */
    public static WorkflowFactory getInstance() {
        return WorkflowFactory.SingletonHolder.INSTANCE;
    }

    /**
     * Creates a new instance of Workflow.
     * 
     * @param dir
     *            Directory to work with
     * @param back
     *            Backend to use for this workflow
     * @param appId
     *            if != 0, use this as an application id.
     * @return an instance of {@link Workflow}
     */
    public Workflow createWorkflow(final File dir, final Backend back,
            final int appId) {
        final int id;
        if (appId != 0) {
            id = appId;
        } else {
            synchronized (this) {
                id = ++this.currentId;
            }
        }
        return new Workflow(dir, back, id);
    }
}
