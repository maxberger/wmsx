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

package hu.kfki.grid.wmsx.job.description;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Represents an empty job, for cases where a job description is needed to avoid
 * null-pointer exceptions.
 * 
 * @version $Date$
 */
public class EmptyJobDescription extends AbstractJobDescription {

    /**
     * Default constructor.
     */
    public EmptyJobDescription() {
        // do nothing.
    }

    /** {@inheritDoc} */
    public List<String> getListEntry(final String key) {
        return Collections.emptyList();
    }

    /** {@inheritDoc} */
    public String getStringEntry(final String key) {
        return null;
    }

    /** {@inheritDoc} */
    public File toJdl() throws IOException {
        throw new IOException("Empty Jobs cannot be submitted!");
    }

    /** {@inheritDoc} */
    public void removeEntry(final String entry) {
        // Do nothing
    }

    /** {@inheritDoc} */
    public void replaceEntry(final String entry, final String value) {
        // Do nothing
    }

    /** {@inheritDoc} */
    public File getBaseDir() {
        return File.listRoots()[0];
    }

    /** {@inheritDoc} */
    public String getName() {
        return "";
    }

}
