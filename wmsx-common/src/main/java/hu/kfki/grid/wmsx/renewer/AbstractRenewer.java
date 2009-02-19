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
 * Generic Renewer which renews passwords by calling an executable.
 * 
 * @version $Date$
 * 
 */
public abstract class AbstractRenewer implements Renewer {

    private static final int MILLIS_BETWEEN_RENEWS = 3 * 60 * 60 * 1000;

    private static final int ALL_BITS_CHAR = 0xffff;

    private final String opassword;

    private boolean shutdown;

    /**
     * Standard constructor which takes a password to remember.
     * 
     * @param password
     *            the password. It is not stored in clear in the memory, but
     *            very easy to deduct.
     */
    public AbstractRenewer(final String password) {
        this.shutdown = false;
        this.opassword = this.obfuscate(password);
    }

    private String obfuscate(final String what) {
        final StringBuffer b = new StringBuffer(what.length());
        for (int i = 0; i < what.length(); i++) {
            b.append((char) (what.charAt(i) ^ AbstractRenewer.ALL_BITS_CHAR));
        }
        return b.toString();
    }

    private String deobfuscate(final String what) {
        return this.obfuscate(what);
    }

    /**
     * Request to shutdown this Renewer.
     */
    public void shutdown() {
        this.shutdown = true;
    }

    /**
     * Request to renew NOW.
     * 
     * @return true if the password could be renewed successfully.
     */
    public boolean renew() {
        final boolean retVal;
        this.preexec();
        retVal = this.exec(this.deobfuscate(this.opassword));
        this.postexec();
        return retVal;
    }

    /** {@inheritDoc} */
    public void run() {
        while (!this.shutdown) {
            try {
                Thread.sleep(AbstractRenewer.MILLIS_BETWEEN_RENEWS);
                this.renew();
            } catch (final InterruptedException e) {
                // ignore
            }
        }
    }

    /**
     * Override this method if you need anything done before execution.
     */
    protected void preexec() {
    }

    /**
     * Override this method to perform the actual renewal.
     * 
     * @param password
     *            the password to use
     * @return true if the renewal was successfully.
     */
    protected abstract boolean exec(String password);

    /**
     * Override this method if you need anything done after execution.
     */
    protected void postexec() {
    }
}
