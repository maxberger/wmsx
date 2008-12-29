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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.jini.id.Uuid;

/**
 * Support for files on worker clients.
 * 
 * @version $Revision$
 */
public final class FileManager {

    private final Map<String, Set<String>> fileAvailAt = new HashMap<String, Set<String>>();

    /**
     * Default constructor.
     */
    public FileManager() {
        // empty on purpose
    }

    /**
     * Copy files from sourceSandbox to TargetSandbox, removing those already
     * existent.
     * 
     * @param workerId
     *            Id of the assigned worker
     * @param wfid
     *            Id of the workflow
     * @param sourceSandbox
     *            Sandbox to modify.
     * @param targetSandbox
     *            Sandbox to modify.
     */
    public void modifyInputSandbox(final Uuid workerId, final String wfid,
            final Map<String, byte[]> sourceSandbox,
            final Map<String, byte[]> targetSandbox) {
        targetSandbox.clear();
        if (wfid == null) {
            targetSandbox.putAll(sourceSandbox);
            return;
        }
        final String completeId = wfid + workerId;
        for (final Map.Entry<String, byte[]> entry : sourceSandbox.entrySet()) {
            final String file = entry.getKey();
            Set<String> avail = this.fileAvailAt.get(file);
            if (avail == null) {
                avail = new HashSet<String>();
                this.fileAvailAt.put(file, avail);
            }
            if (!avail.contains(completeId)) {
                targetSandbox.put(file, entry.getValue());
                avail.add(completeId);
            }
        }
    }

    /**
     * Parse output sandbox to see which files are now available on the worker.
     * 
     * @param workerId
     *            id of the worker
     * @param wfid
     *            Id of the workflow
     * @param sandbox
     *            output sandbox.
     */
    public void parseOutputSandbox(final Uuid workerId, final String wfid,
            final Map<String, byte[]> sandbox) {
        if (wfid == null) {
            return;
        }
        final String completeId = wfid + workerId;
        for (final String file : sandbox.keySet()) {
            final Set<String> newSet = new HashSet<String>();
            newSet.add(completeId);
            this.fileAvailAt.put(file, newSet);
        }
    }

    /**
     * Clear the knowledge about this worker.
     * 
     * @param uuid
     *            id of the worker
     * @param wfid
     *            Id of the workflow
     */
    public void clearWorker(final Uuid uuid, final String wfid) {
        final String completeId = wfid + uuid;
        for (final Set<String> workers : this.fileAvailAt.values()) {
            workers.remove(completeId);
        }

    }
}
