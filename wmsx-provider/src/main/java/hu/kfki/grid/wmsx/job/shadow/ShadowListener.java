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

package hu.kfki.grid.wmsx.job.shadow;

import hu.kfki.grid.wmsx.backends.JobUid;
import hu.kfki.grid.wmsx.backends.SubmissionResults;
import hu.kfki.grid.wmsx.job.JobListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * Listens to jobs which have a Shadow port and retrieves stdout and stderr.
 * 
 * @version $Revision$
 */
public final class ShadowListener implements Runnable, JobListener {

    private static final Logger LOGGER = Logger.getLogger(ShadowListener.class
            .toString());

    private File iFile;

    private File oFile;

    private File eFile;

    private ReadableByteChannel oChannel;

    private ReadableByteChannel eChannel;

    private int listenerPid;

    private boolean termination;

    private Thread runThread;

    private final WritableByteChannel appOutput;

    private int port;

    private ShadowListener(final SubmissionResults result,
            final WritableByteChannel outputStream) {
        this.appOutput = outputStream;
        // Runtime.runFinalizersOnExit(true);

        this.oFile = this.fileFromResult(result.getOStream());
        this.eFile = this.fileFromResult(result.getEStream());
        this.iFile = this.fileFromResult(result.getIStream());
        this.oChannel = new PipeInputChannel(this.oFile);
        this.eChannel = new PipeInputChannel(this.eFile);
        this.listenerPid = result.getShadowpid();
        this.port = result.getPort();

        if (outputStream != null && this.oFile != null) {
            this.termination = false;
            this.runThread = new Thread(this);
            this.runThread.start();
        } else {
            this.termination = true;
            this.cleanup();
        }

    }

    private File fileFromResult(final String stream) {
        if (stream == null) {
            return null;
        }
        return new File(stream);
    }

    /**
     * Start Listening (if needed).
     * 
     * @param result
     *            The Submission output
     * @param outputStream
     *            outputStream to listen to.
     * @return a shadowlistener instance.
     */
    public static ShadowListener listen(final SubmissionResults result,
            final WritableByteChannel outputStream) {
        return new ShadowListener(result, outputStream);
    }

    /** {@inheritDoc} */
    @Override
    // CHECKSTYLE:OFF
    protected void finalize() throws Throwable {
        // CHECKSTYLE:ON
        this.cleanup();
        super.finalize();
    }

    private void killer(final boolean serious) {

        boolean safety = true;
        final List<String> commandLine = new ArrayList<String>();
        commandLine.add("kill");
        if (serious) {
            commandLine.add("-9");
        }
        if (this.listenerPid != 0) {
            commandLine.add(Integer.toString(this.listenerPid));
            safety = false;
        }

        if (this.port != 0 && this.oFile != null) {
            final String oName = this.oFile.getAbsolutePath();
            try {
                final String oBase = oName.substring(0, oName.lastIndexOf('.'));
                final List<String> mustHave = new ArrayList<String>();
                mustHave.add(oBase);
                mustHave.add(Integer.toString(this.port));
                mustHave.add("edg-wl-grid-console-shadow");
                final Process p = Runtime.getRuntime().exec(
                        new String[] { "ps", "ax" });
                final List<String> v = this.grepLines(p.getInputStream(),
                        mustHave);
                final Iterator<String> it = v.iterator();
                while (it.hasNext()) {
                    final String line = it.next();
                    final String pidStr = line.substring(0, 6).trim();
                    commandLine.add(pidStr);
                }
            } catch (final IndexOutOfBoundsException iobe) {
                ShadowListener.LOGGER.fine(iobe.getMessage());
            } catch (final IOException e) {
                ShadowListener.LOGGER.fine(e.getMessage());
            }
        }

        if (!safety) {
            this.runtimeExec(commandLine
                    .toArray(new String[commandLine.size()]));
        }

        if (serious) {
            this.port = 0;
            this.listenerPid = 0;
        }
    }

    private List<String> grepLines(final InputStream inputStream,
            final List<String> mustHave) {
        final List<String> lines = new ArrayList<String>();
        try {

            final BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream));
            String line = reader.readLine();
            while (line != null) {
                boolean passes = true;
                final Iterator<String> it = mustHave.iterator();
                while (passes && it.hasNext()) {
                    final String lookFor = it.next();
                    passes = line.indexOf(lookFor) >= 0;
                }
                if (passes) {
                    lines.add(line);
                }

                line = reader.readLine();
            }
        } catch (final IOException e) {
            ShadowListener.LOGGER.fine(e.getMessage());
        }
        return lines;
    }

    private void cleanup() {
        this.killer(false);
        this.closeChannel(this.oChannel);
        this.oChannel = null;
        this.closeChannel(this.eChannel);
        this.eChannel = null;
        this.killer(true);
        this.listenerPid = 0;
        this.deleteFile(this.oFile);
        this.oFile = null;
        this.deleteFile(this.iFile);
        this.iFile = null;
        this.deleteFile(this.eFile);
        this.eFile = null;
    }

    private void deleteFile(final File file) {
        try {
            file.delete();
        } catch (final NullPointerException e) {
            // ignore
        } catch (final SecurityException e) {
            // ignore
        }

    }

    private void runtimeExec(final String[] args) {
        try {
            final Process p = Runtime.getRuntime().exec(args);
            p.waitFor();
        } catch (final IOException e) {
            ShadowListener.LOGGER.fine(e.getMessage());
        } catch (final InterruptedException e) {
            ShadowListener.LOGGER.fine(e.getMessage());
        }
    }

    private void closeChannel(final Channel c) {
        try {
            c.close();
        } catch (final NullPointerException e) {
            // Ignore
        } catch (final IOException e) {
            // Ignore
        }
    }

    /** {@inheritDoc} */
    public void run() {
        ShadowListener.LOGGER.info("Shadow listener started");
        try {
            final ByteBuffer buf = ByteBuffer.allocateDirect(4096);
            while (!this.termination) {
                buf.rewind();
                // appOutput.flush();
                this.oChannel.read(buf);
                buf.flip();
                while (buf.remaining() > 0) {
                    this.appOutput.write(buf);
                }
            }
        } catch (final IOException e) {
            // System.out.println("Exceptional!");
            // Ignore, the end is near!
        }
        ShadowListener.LOGGER.info("Shadow listener terminated");
        this.cleanup();
    }

    /** {@inheritDoc} */
    public void done(final JobUid id, final boolean success) {
        // System.out.println("Terminator called!");
        this.termination = true;
        if (this.runThread != null) {
            this.runThread.interrupt();
        }
    }

    /** {@inheritDoc} */
    public void running(final JobUid id) {
        // Nothing yet
    }

    /** {@inheritDoc} */
    public void startup(final JobUid id) {
        // Nothing yet
    }
}
