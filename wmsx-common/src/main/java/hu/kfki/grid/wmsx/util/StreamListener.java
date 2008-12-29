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
 */

/* $Id$ */

package hu.kfki.grid.wmsx.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

/**
 * Listens to to InputStreams and writes to result to an output Stream.
 * 
 * @version $Revision$
 */
public final class StreamListener implements Runnable {

    private static final int BUF_SIZE = 4096;

    private static final Logger LOGGER = Logger.getLogger(StreamListener.class
            .toString());

    private final OutputStream out;

    private final InputStream in;

    private final ScriptLauncher launcher;

    private StreamListener(final InputStream i, final OutputStream o,
            final ScriptLauncher l) {
        this.out = o;
        this.in = i;
        this.launcher = l;
    }

    /** {@inheritDoc} */
    public void run() {
        try {
            if (this.out != null) {
                final byte[] buf = new byte[StreamListener.BUF_SIZE];
                int r = this.in.read(buf);
                while (r >= 0) {
                    synchronized (this.out) {
                        this.out.write(buf, 0, r);
                        if (this.in.available() == 0) {
                            this.out.flush();
                        }
                    }
                    r = this.in.read(buf);
                }
            }
        } catch (final IOException e) {
            StreamListener.LOGGER.warning("IOException wrapping exec: "
                    + e.getMessage());
        } finally {
            try {
                this.in.close();
            } catch (final IOException e) {
                // ignore
            }
            try {
                this.out.close();
            } catch (final IOException e) {
                // ignore
            }
            this.launcher.forgetStream(this.out);
        }
    }

    public static void listen(final InputStream i, final OutputStream o,
            final ScriptLauncher l) {
        if (i == null) {
            return;
        }
        if (o == null) {
            return;
        }
        new Thread(new StreamListener(i, o, l)).start();
    }
}
