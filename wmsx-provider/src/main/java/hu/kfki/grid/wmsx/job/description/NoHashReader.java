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

package hu.kfki.grid.wmsx.job.description;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.logging.Logger;

/**
 * Reads a file and ignores lines starting with hash (#).
 * 
 * @version $Date$
 */
public class NoHashReader extends Reader {

    private static final Logger LOGGER = Logger.getLogger(NoHashReader.class
            .toString());

    private final BufferedReader in;

    private String buffer;

    private int bufpos;

    /**
     * Create a Reader based on an original Reader.
     * 
     * @param orig
     *            Reader to filter.
     */
    public NoHashReader(final Reader orig) {
        this.in = new BufferedReader(orig);
    }

    /** {@inheritDoc} */
    @Override
    public void close() throws IOException {
        this.in.close();
    }

    /** {@inheritDoc} */
    @Override
    public int read(final char[] cbuf, final int off, final int len)
            throws IOException {

        if (this.buffer == null) {
            this.buffer = this.in.readLine();
            this.bufpos = 0;
        }
        if (this.buffer == null) {
            return -1;
        }

        int blen = this.buffer.length();
        if (blen > 0 && this.buffer.charAt(0) == '#') {
            blen = 0;
        }
        final int toRead = Math.min(len, blen - this.bufpos);

        this.buffer.getChars(this.bufpos, this.bufpos + toRead, cbuf, off);
        this.bufpos += toRead;
        if (this.bufpos == blen) {
            this.buffer = null;
        }

        return toRead;
    }

    /** {@inheritDoc} */
    @Override
    // CHECKSTYLE:OFF
    protected void finalize() throws Throwable {
        // CHECKSTYLE:ON
        try {
            this.close();
        } catch (final IOException e) {
            NoHashReader.LOGGER.fine(e.getMessage());
        }
        super.finalize();
    }

}
