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
