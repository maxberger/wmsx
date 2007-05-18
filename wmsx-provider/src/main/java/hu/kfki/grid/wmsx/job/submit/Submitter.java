package hu.kfki.grid.wmsx.job.submit;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Vector;

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
		final List commandLine = new Vector();
		commandLine.add("/opt/edg/bin/edg-job-submit");
		commandLine.add("--nolisten");
		commandLine.add(jdlFile);
		final Process p = Runtime.getRuntime().exec(
				(String[]) commandLine.toArray());

		// final PrintStream parserOutput = new PrintStream(
		// new ByteArrayOutputStream());
		final PrintStream parserOutput = System.out;
		return InputParser.parse(p.getInputStream(), parserOutput);
	}
}
