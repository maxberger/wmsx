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

package hu.kfki.grid.wmsx.provider;

public class JdlJobFactory implements JobFactory {
    private final String jdlFile;

    private final String output;

    private final String result;

    public JdlJobFactory(final String jdlFile, final String output,
            final String resultDir) {
        this.jdlFile = jdlFile;
        this.output = output;
        this.result = resultDir;
    }

    public JdlJob createJdlJob() {
        return new JdlJob(this.jdlFile, this.output, this.result, null);
    }

}
