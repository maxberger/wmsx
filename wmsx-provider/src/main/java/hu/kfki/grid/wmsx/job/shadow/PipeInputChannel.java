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

package hu.kfki.grid.wmsx.job.shadow;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

public class PipeInputChannel implements ReadableByteChannel {

    private File file;

    private ReadableByteChannel channel;

    public PipeInputChannel(final String fileName) {
        this(new File(fileName));
    }

    public PipeInputChannel(final File file) {
        this.file = file;
        this.channel = null;
    }

    public int read(final ByteBuffer arg0) throws IOException {
        if (this.channel == null) {
            try {
                this.channel = new FileInputStream(this.file).getChannel();
            } catch (final NullPointerException npe) {
                throw new IOException("File was null");
            }
        }
        return this.channel.read(arg0);
    }

    public void close() throws IOException {
        if (this.channel != null) {
            this.channel.close();
        }
        this.file = null;
    }

    public boolean isOpen() {
        if (this.channel == null) {
            return true;
        } else {
            return this.channel.isOpen();
        }
    }

}
