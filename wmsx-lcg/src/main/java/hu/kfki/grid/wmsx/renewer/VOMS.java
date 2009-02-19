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

package hu.kfki.grid.wmsx.renewer;

import java.util.List;
import java.util.Vector;

public class VOMS extends Renewer {

    private final String voms;

    public VOMS(final String password, final String vo) {
        super(password);
        this.voms = vo;
    }

    @Override
    protected boolean exec(final String password) {
        final List<String> commandLine = new Vector<String>();
        commandLine.add("voms-proxy-init");
        if (this.voms != null) {
            commandLine.add("-voms");
            commandLine.add(this.voms);
        }
        commandLine.add("-pwstdin");
        return PasswordAppLauncher.getInstance().launch(
                commandLine.toArray(new String[commandLine.size()]), password);

    }

}
