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

public abstract class Renewer implements Runnable {

    private final String opassword;

    private boolean shutdown;

    public Renewer(final String password) {
        this.shutdown = false;
        this.opassword = this.obfuscate(password);
    }

    private String obfuscate(final String what) {
        final StringBuffer b = new StringBuffer(what.length());
        for (int i = 0; i < what.length(); i++) {
            b.append((char) (what.charAt(i) ^ 0xffff));
        }
        return b.toString();
    }

    private String deobfuscate(final String what) {
        return this.obfuscate(what);
    }

    public void shutdown() {
        this.shutdown = true;
    }

    public boolean renew() {
        final boolean retVal;
        this.preexec();
        retVal = this.exec(this.deobfuscate(this.opassword));
        this.postexec();
        return retVal;
    }

    public void run() {
        while (!this.shutdown) {
            try {
                Thread.sleep(3 * 60 * 60 * 1000);
                this.renew();
            } catch (final InterruptedException e) {
                // ignore
            }
        }
    }

    protected void preexec() {
    }

    abstract protected boolean exec(String password);

    protected void postexec() {
    }
}
