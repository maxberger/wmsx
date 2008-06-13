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

package hu.kfki.grid.wmsx.backends.lcg;

import java.util.List;
import java.util.Vector;

/**
 * Backend for gLite 2.7.
 * 
 * @version $Revision$
 */
public final class EDGBackend extends AbstractLCGBackend {

    private static EDGBackend instance;

    private EDGBackend() {
    }

    /**
     * @return the Singleton instance.
     */
    public static synchronized EDGBackend getInstance() {
        if (EDGBackend.instance == null) {
            EDGBackend.instance = new EDGBackend();
        }
        return EDGBackend.instance;
    }

    /** {@inheritDoc} */
    @Override
    public List<String> jobOutputCommand(final String absolutePath,
            final String idString) {
        final List<String> commandLine = new Vector<String>();
        commandLine.add(AbstractLCGBackend.ENV);
        commandLine.add("edg-job-get-output");
        commandLine.add("--dir");
        commandLine.add(absolutePath);
        commandLine.add(AbstractLCGBackend.NOINT);
        commandLine.add(idString);
        return commandLine;
    }

    /** {@inheritDoc} */
    @Override
    public List<String> retreiveLogCommand(final String jobId,
            final String filename) {
        final List<String> commandLine = new Vector<String>();
        commandLine.add(AbstractLCGBackend.ENV);
        commandLine.add("edg-job-get-logging-info");
        commandLine.add("-o");
        commandLine.add(filename);
        commandLine.add(AbstractLCGBackend.NOINT);
        commandLine.add(jobId);
        return commandLine;
    }

    /** {@inheritDoc} */
    @Override
    public List<String> submitJdlCommand(final String jdlFile, final String vo) {
        final List<String> commandLine = new Vector<String>();
        commandLine.add(AbstractLCGBackend.ENV);
        commandLine.add("edg-job-submit");
        commandLine.add("--nolisten");
        if (vo != null) {
            commandLine.add("--vo");
            commandLine.add(vo);
        }
        commandLine.add(jdlFile);
        return commandLine;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "EDG";
    }
}
