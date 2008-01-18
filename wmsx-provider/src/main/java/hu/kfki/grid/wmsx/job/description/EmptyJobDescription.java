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

package hu.kfki.grid.wmsx.job.description;

import java.io.File;
import java.util.List;
import java.util.Vector;

public class EmptyJobDescription extends AbstractJobDescription {

    public List<String> getListEntry(final String key) {
        return new Vector<String>();
    }

    public String getStringEntry(final String key) {
        return null;
    }

    public String toJDL() {
        return "[]";
    }

    public void removeEntry(final String entry) {
    }

    public void replaceEntry(final String entry, final String value) {
    }

    public File getBaseDir() {
        return File.listRoots()[0];
    }

}
