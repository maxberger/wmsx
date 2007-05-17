package hu.kfki.grid.wmsx;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

public class PipeInputChannel implements ReadableByteChannel {

	private File file;

	private ReadableByteChannel channel;

	public PipeInputChannel(String fileName) {
		this(new File(fileName));
	}

	public PipeInputChannel(File file) {
		this.file = file;
		channel = null;
	}

	public int read(ByteBuffer arg0) throws IOException {
		if (channel == null) {
			channel = new FileInputStream(file).getChannel();
		}
		return channel.read(arg0);
	}

	public void close() throws IOException {
		if (channel != null) {
			channel.close();
		}
		file = null;
	}

	public boolean isOpen() {
		if (channel == null) {
			return true;
		} else
			return channel.isOpen();
	}

}
