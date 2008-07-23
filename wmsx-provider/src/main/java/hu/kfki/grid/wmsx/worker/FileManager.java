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
     * Modify the input sandbox to remove items not needed.
     * 
     * @param workerId
     *            Id of the assigned worker
     * @param wfid
     *            Id of the workflow
     * @param sandbox
     *            Sandbox to modify.
     */
    public void modifyInputSandbox(final Uuid workerId, final String wfid,
            final Map<String, byte[]> sandbox) {
        if (wfid == null) {
            return;
        }
        final String completeId = wfid + workerId;
        final Set<String> filenames = new HashSet<String>(sandbox.keySet());
        for (final String file : filenames) {
            Set<String> avail = this.fileAvailAt.get(file);
            if (avail == null) {
                avail = new HashSet<String>();
                this.fileAvailAt.put(file, avail);
            }
            if (avail.contains(completeId)) {
                sandbox.remove(file);
            } else {
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
}
