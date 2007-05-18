package hu.kfki.grid.wmsx.job.submit;

import java.io.IOException;
import java.io.PrintStream;

public class Submitter {

	private static Submitter submitter;

	private Submitter() {
	}

	static synchronized public Submitter getSubmitter() {
		if (Submitter.submitter == null) {
			Submitter.submitter = new Submitter();
		}
		return Submitter.submitter;
	}

	public ParseResult submitJdl(final String jdlFile) throws IOException {
		final Process p = Runtime.getRuntime().exec(
				new String[] { "/opt/edg/bin/edg-job-submit", jdlFile });
		// final PrintStream parserOutput = new PrintStream(
		// new ByteArrayOutputStream());
		final PrintStream parserOutput = System.out;
		return InputParser.parse(p.getInputStream(), parserOutput);
	}
}
