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
import hu.kfki.grid.wmsx.job.description.JDLJobDescription;
import hu.kfki.grid.wmsx.job.description.JobDescription;
import hu.kfki.grid.wmsx.provider.JdlJob;
import hu.kfki.grid.wmsx.provider.WmsxProviderImpl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;

/**
 * Describes a running workflow.
 * 
 * @version $Date$
 */
public class Workflow {

    private static final Logger LOGGER = Logger.getLogger(Workflow.class
            .toString());

    private final File directory;

    private final Map<String, List<String>> nextNodes;

    private final Map<String, List<String>> prevNodes;

    private final Set<String> potentialTodo;

    private final Set<String> done;

    private final Backend backend;

    private final int appId;

    /**
     * Create a new workflow.
     * 
     * @param dir
     *            work directory.
     * @param back
     *            Backend for individual activities
     * @param applicationId
     *            Application Id for this wf. If shared among different WFs,
     *            they must represent the same application.
     */
    Workflow(final File dir, final Backend back, final int applicationId) {
        this.directory = dir;
        this.nextNodes = new HashMap<String, List<String>>();
        this.prevNodes = new HashMap<String, List<String>>();
        this.potentialTodo = new HashSet<String>();
        this.done = new HashSet<String>();
        this.backend = back;
        this.appId = applicationId;
    }

    /**
     * To be called whenever an activity has finished.
     * 
     * @param jdlJob
     *            Description of the activity.
     */
    public synchronized void activityHasFinished(final JdlJob jdlJob) {
        final String name = jdlJob.getName();
        Workflow.LOGGER.info("Done with: " + name);
        this.done.add(name);
        synchronized (this.nextNodes) {
            final List<String> next = this.nextNodes.get(name);
            if (next != null) {
                this.potentialTodo.addAll(next);
            }
        }
        final Iterator<String> it = this.potentialTodo.iterator();
        final List<String> toExecute = new Vector<String>();
        while (it.hasNext()) {
            final String node = it.next();
            final List<String> prevs = this.prevNodes.get(node);
            boolean execute = true;
            if (prevs != null) {
                final Iterator<String> i2 = prevs.iterator();
                while (i2.hasNext() && execute) {
                    final String prevNode = i2.next();
                    if (!this.done.contains(prevNode)) {
                        execute = false;
                    }
                }
            }
            if (execute) {
                toExecute.add(node);
            }
        }

        final Iterator<String> i3 = toExecute.iterator();
        while (i3.hasNext()) {
            final String node = i3.next();
            this.executeNode(node);
        }
    }

    private void executeNode(final String node) {
        Workflow.LOGGER.info("Submitting: " + node);
        this.potentialTodo.remove(node);
        WmsxProviderImpl.getInstance().addJobFactory(
                new WorkflowNodeJobFactory(this, node));
    }

    /**
     * Set the "next nodes" for a given activity.
     * 
     * @param name
     *            Name of the activity
     * @param listEntry
     *            List of nodes to follow
     */
    public void setNextNodes(final String name, final List<String> listEntry) {
        synchronized (this.nextNodes) {
            this.nextNodes.put(name, listEntry);
            this.parsePrev(listEntry);
        }
    }

    private void parsePrev(final List<String> listEntry) {
        final Iterator<String> it = listEntry.iterator();
        while (it.hasNext()) {
            final String nodeName = it.next();
            if (!this.prevNodes.containsKey(nodeName)) {
                try {
                    final JobDescription nodeJob = new JDLJobDescription(
                            new File(this.directory, nodeName));
                    final List<String> prevs = nodeJob.getListEntry("Prev");
                    this.prevNodes.put(nodeName, prevs);
                } catch (final IOException e) {
                    Workflow.LOGGER.warning(e.getMessage());
                }

            }
        }
    }

    /**
     * @return working directory for this workflow.
     */
    public File getDirectory() {
        return this.directory;
    }

    /**
     * @return the backend used to execute this workflow.
     */
    public Backend getBackend() {
        return this.backend;
    }

    /**
     * @return the application Id of this workflow.
     */
    public int getApplicationId() {
        return this.appId;
    }
}
