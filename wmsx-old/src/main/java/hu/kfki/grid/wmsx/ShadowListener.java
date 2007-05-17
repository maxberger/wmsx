package hu.kfki.grid.wmsx;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public class ShadowListener implements Runnable {

	final File iFile;

	final File oFile;

	final File eFile;

	final ReadableByteChannel oChannel;

	final ReadableByteChannel eChannel;

	final int listenerPid;

	boolean termination;

	Thread runThread;

	final WritableByteChannel appOutput;

	private ShadowListener(ParseResult result, WritableByteChannel outputStream) {
		appOutput = outputStream;
		Runtime.runFinalizersOnExit(true);

		oFile = new File(result.getOStream());
		eFile = new File(result.getEStream());
		iFile = new File(result.getIStream());
		oChannel = new PipeInputChannel(oFile);
		eChannel = new PipeInputChannel(eFile);
		listenerPid = result.getShadowpid();
		termination = false;

		runThread = new Thread(this);
		runThread.start();

	}

	public static ShadowListener listen(ParseResult result,
			WritableByteChannel outputStream) {
		ShadowListener l = new ShadowListener(result, outputStream);
		return l;
	}

	protected void finalize() {
		if (listenerPid != 0)
			runtimeExec(new String[] { "kill", Integer.toString(listenerPid) });
		closeChannel(oChannel);
		closeChannel(eChannel);
		if (listenerPid != 0)
			runtimeExec(new String[] { "kill", "-9",
					Integer.toString(listenerPid) });
		deleteFile(oFile);
		deleteFile(iFile);
		deleteFile(eFile);
	}

	private void deleteFile(File file) {
		try {
			file.delete();
		} catch (SecurityException e) {
			// ignore
		}

	}

	private void runtimeExec(String[] args) {
		try {
			Process p = Runtime.getRuntime().exec(args);
			p.waitFor();
		} catch (IOException e) {
			// Ignore
		} catch (InterruptedException e) {
			// Ignore
		}
	}

	private void closeChannel(Channel c) {
		try {
			c.close();
		} catch (IOException e) {
			// Ignore
		}
	}

	public void run() {
		// System.out.println("I am listening...");
		try {
			ByteBuffer buf = ByteBuffer.allocateDirect(4096);
			while (!termination) {
				buf.rewind();
				// appOutput.flush();
				oChannel.read(buf);
				buf.flip();
				while (buf.remaining() > 0) {
					appOutput.write(buf);
				}
			}
		} catch (IOException e) {
			// System.out.println("Exceptional!");
			// Ignore, the end is near!
		}
		// System.out.println("Listener is terminated");
	}

	public void terminate() {
		// System.out.println("Terminator called!");
		termination = true;
		runThread.interrupt();
	}
}
