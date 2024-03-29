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

package hu.kfki.grid.wmsx.renewer;

/**
 * Renewer for AFS passwords.
 * 
 * @version $Date$
 */
public class AFS extends AbstractRenewer {

    /**
     * Start a new AFS renewer with the given password.
     * 
     * @param password
     *            The AFS password.
     */
    public AFS(final String password) {
        super(password);
    }

    /** {@inheritDoc} */
    @Override
    protected boolean exec(final String password) {
        return PasswordAppLauncher.getInstance().launch(
                new String[] { "kinit" }, password);
    }

    /** {@inheritDoc} */
    @Override
    protected void postexec() {
        PasswordAppLauncher.getInstance()
                .launch(new String[] { "aklog" }, null);
    }

}
