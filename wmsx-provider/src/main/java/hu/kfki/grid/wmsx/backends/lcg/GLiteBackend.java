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

package hu.kfki.grid.wmsx.backends.lcg;

import java.util.List;
import java.util.Vector;

public final class GLiteBackend extends AbstractLCGBackend {

    private static GLiteBackend instance;

    private GLiteBackend() {
    }

    public static synchronized GLiteBackend getInstance() {
        if (GLiteBackend.instance == null) {
            GLiteBackend.instance = new GLiteBackend();
        }
        return GLiteBackend.instance;
    }

    @Override
    public List<String> jobOutputCommand(final String absolutePath,
            final String idString) {
        final List<String> commandLine = new Vector<String>();
        commandLine.add("/opt/glite/bin/glite-job-output");
        commandLine.add("--dir");
        commandLine.add(absolutePath);
        commandLine.add("--noint");
        commandLine.add(idString);
        return commandLine;
    }

    @Override
    public List<String> submitJdlCommand(final String jdlFile, final String vo) {
        final List<String> commandLine = new Vector<String>();
        commandLine.add("/opt/glite/bin/glite-job-submit");
        commandLine.add("--nolisten");
        commandLine.add("--noint");
        if (vo != null) {
            commandLine.add("--vo");
            commandLine.add(vo);
        }
        commandLine.add(jdlFile);
        return commandLine;
    }

    @Override
    public String toString() {
        return "GLite";
    }
}
