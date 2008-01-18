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

package hu.kfki.grid.wmsx;

import java.io.IOException;

/**
 * My Jini Service Interface!
 * 
 * @version $Revision$
 */
public interface Wmsx {

    boolean ping(boolean remote);

    String submitJdl(String jdlFile, String outputFile, String resultDir)
            throws IOException;

    void submitLaszlo(String argFile, boolean interactive, String name)
            throws IOException;

    void setMaxJobs(int maxJobs);

    void startWorkers(int number);

    boolean rememberAfs(String password);

    boolean rememberGrid(String password);

    void forgetAfs();

    void setVo(String newVo);

    void setBackend(String backend);
}
