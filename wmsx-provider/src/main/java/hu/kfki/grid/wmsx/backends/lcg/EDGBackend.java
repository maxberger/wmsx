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

public class EDGBackend extends AbstractLCGBackend {

    private static EDGBackend instance;

    private EDGBackend() {
    }

    public static synchronized EDGBackend getInstance() {
        if (EDGBackend.instance == null) {
            EDGBackend.instance = new EDGBackend();
        }
        return EDGBackend.instance;
    }

    @Override
    public List<String> jobOutputCommand(final String absolutePath,
            final String idString) {
        final List<String> commandLine = new Vector<String>();
        commandLine.add("/opt/edg/bin/edg-job-get-output");
        commandLine.add("--dir");
        commandLine.add(absolutePath);
        commandLine.add("--noint");
        commandLine.add(idString);
        return commandLine;
    }

    @Override
    public List<String> submitJdlCommand(final String jdlFile, final String vo) {
        final List<String> commandLine = new Vector<String>();
        commandLine.add("/opt/edg/bin/edg-job-submit");
        commandLine.add("--nolisten");
        if (vo != null) {
            commandLine.add("--vo");
            commandLine.add(vo);
        }
        commandLine.add(jdlFile);
        return commandLine;
    }

    @Override
    public String toString() {
        return "EDG";
    }
}
