package hu.kfki.grid.wmsx.job.submit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class Submitter {

	private static Submitter submitter;

	private Submitter() {
	}

	static synchronized public Submitter getSubmitter() {
		if (submitter == null) {
			submitter = new Submitter();
		}
		return submitter;
	}

	public ParseResult submitJdl(String jdlFile) throws IOException {
		Process p = Runtime.getRuntime().exec(
				new String[] { "/opt/edg/bin/edg-job-submit", jdlFile });
		PrintStream parserOutput = new PrintStream(new ByteArrayOutputStream());
		return InputParser.parse(p.getInputStream(), parserOutput);
	}
}
