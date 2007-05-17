package hu.kfki.grid.wmsx;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		PrintStream parserOutput = null;
		WritableByteChannel appOutput = null;
		boolean init = false;
		if (args.length == 1) {
			try {
				parserOutput = System.out;
				appOutput = new FileOutputStream(args[0]).getChannel();
				init = true;
			} catch (IOException io) {
				init = false;
			}
		}
		if (!init) {
			parserOutput = new PrintStream(new ByteArrayOutputStream());
			appOutput = Channels.newChannel(System.out);
		}
		ParseResult pr = InputParser.parse(System.in, parserOutput);
		ShadowListener listener = ShadowListener.listen(pr, appOutput);
		JobWatcher.watch(pr.jobId, listener);
		listener = null;
	}

}
