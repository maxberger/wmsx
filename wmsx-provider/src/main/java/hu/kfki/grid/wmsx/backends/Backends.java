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

package hu.kfki.grid.wmsx.backends;

import hu.kfki.grid.wmsx.backends.lcg.EDGBackend;
import hu.kfki.grid.wmsx.backends.lcg.GLiteBackend;
import hu.kfki.grid.wmsx.backends.local.FakeBackend;
import hu.kfki.grid.wmsx.backends.local.LocalBackend;
import hu.kfki.grid.wmsx.worker.WorkerBackend;

/**
 * Constant list of backends implemented.
 * 
 * @version $Revision$
 */
public final class Backends {

    public static final Backend EDG = EDGBackend.getInstance();

    public static final Backend GLITE = GLiteBackend.getInstance();

    public static final Backend FAKE = FakeBackend.getInstance();

    public static final Backend LOCAL = LocalBackend.getInstance();

    public static final Backend WORKER = WorkerBackend.getInstance();
}
