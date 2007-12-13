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
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

public class ShadowListener implements Runnable, JobListener {

    private static final Logger LOGGER = Logger.getLogger(ShadowListener.class
            .toString());

    File iFile;

    File oFile;

    File eFile;

    ReadableByteChannel oChannel;

    ReadableByteChannel eChannel;

    int listenerPid;

    boolean termination;

    Thread runThread;

    final WritableByteChannel appOutput;

    int port;

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

    public static ShadowListener listen(final SubmissionResults result,
            final WritableByteChannel outputStream) {
        final ShadowListener l = new ShadowListener(result, outputStream);
        return l;
    }

    protected void finalize() {
        this.cleanup();
    }

    private void killer(final boolean serious) {

        boolean safety = true;
        final List commandLine = new Vector();
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
                final String oBase = oName.substring(0, oName.lastIndexOf("."));
                final List mustHave = new Vector();
                mustHave.add(oBase);
                mustHave.add(Integer.toString(this.port));
                mustHave.add("edg-wl-grid-console-shadow");
                final Process p = Runtime.getRuntime().exec(
                        new String[] { "ps", "ax" });
                final List v = this.grepLines(p.getInputStream(), mustHave);
                final Iterator it = v.iterator();
                while (it.hasNext()) {
                    final String line = (String) it.next();
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
            this.runtimeExec((String[]) commandLine
                    .toArray(new String[commandLine.size()]));
        }

        if (serious) {
            this.port = 0;
            this.listenerPid = 0;
        }
    }

    private List grepLines(final InputStream inputStream, final List mustHave) {
        final List lines = new Vector();
        try {

            final BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream));
            String line = reader.readLine();
            while (line != null) {
                boolean passes = true;
                final Iterator it = mustHave.iterator();
                while (passes && it.hasNext()) {
                    final String lookFor = (String) it.next();
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

    public void done(final JobUid id, final boolean success) {
        // System.out.println("Terminator called!");
        this.termination = true;
        if (this.runThread != null) {
            this.runThread.interrupt();
        }
    }

    public void running(final JobUid id) {
        // Nothing yet
    }

    public void startup(final JobUid id) {
        // Nothing yet
    }
}
