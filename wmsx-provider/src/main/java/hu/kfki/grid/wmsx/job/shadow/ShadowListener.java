package hu.kfki.grid.wmsx.job.shadow;

import hu.kfki.grid.wmsx.job.JobListener;
import hu.kfki.grid.wmsx.job.submit.ParseResult;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public class ShadowListener implements Runnable, JobListener {

	final File iFile;

	final File oFile;

	final File eFile;

	final ReadableByteChannel oChannel;

	final ReadableByteChannel eChannel;

	final int listenerPid;

	boolean termination;

	Thread runThread;

	final WritableByteChannel appOutput;

	private ShadowListener(final ParseResult result,
			final WritableByteChannel outputStream) {
		this.appOutput = outputStream;
		// Runtime.runFinalizersOnExit(true);

		this.oFile = fileFromResult(result.getOStream());
		this.eFile = fileFromResult(result.getEStream());
		this.iFile = fileFromResult(result.getIStream());
		this.oChannel = new PipeInputChannel(this.oFile);
		this.eChannel = new PipeInputChannel(this.eFile);
		this.listenerPid = result.getShadowpid();

		if ((outputStream != null) && (oFile != null)) {
			this.termination = false;
			this.runThread = new Thread(this);
			this.runThread.start();
		} else {
			this.termination = true;
		}

	}

	private File fileFromResult(String stream) {
		if (stream == null)
			return null;
		return new File(stream);
	}

	public static ShadowListener listen(final ParseResult result,
			final WritableByteChannel outputStream) {
		final ShadowListener l = new ShadowListener(result, outputStream);
		return l;
	}

	protected void finalize() {
		if (this.listenerPid != 0) {
			this.runtimeExec(new String[] { "kill",
					Integer.toString(this.listenerPid) });
		}
		this.closeChannel(this.oChannel);
		this.closeChannel(this.eChannel);
		if (this.listenerPid != 0) {
			this.runtimeExec(new String[] { "kill", "-9",
					Integer.toString(this.listenerPid) });
		}
		this.deleteFile(this.oFile);
		this.deleteFile(this.iFile);
		this.deleteFile(this.eFile);
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
			// Ignore
		} catch (final InterruptedException e) {
			// Ignore
		}
	}

	private void closeChannel(final Channel c) {
		try {
			c.close();
		} catch (final IOException e) {
			// Ignore
		}
	}

	public void run() {
		// System.out.println("I am listening...");
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
		// System.out.println("Listener is terminated");
	}

	public void done() {
		// System.out.println("Terminator called!");
		this.termination = true;
		if (this.runThread != null) {
			this.runThread.interrupt();
		}
	}

	public void running() {
		// Nothing yet
	}

	public void startup() {
		// Nothing yet
	}
}
