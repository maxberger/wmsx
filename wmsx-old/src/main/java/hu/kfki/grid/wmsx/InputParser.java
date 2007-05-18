package hu.kfki.grid.wmsx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class InputParser {

	public static ParseResult parse(final InputStream inStream,
			final PrintStream outStream) {

		String jobId = null;
		String iStream = null;
		String oStream = null;
		String eStream = null;
		int shadowpid = 0;
		try {

			final BufferedReader reader = new BufferedReader(
					new InputStreamReader(inStream));
			String line = reader.readLine();

			int have = 0;
			while ((line != null) && (have != 0x1f)) {
				outStream.println(line);
				line = line.trim();
				try {
					if ((line.charAt(0) == '-') && (jobId == null)) {
						jobId = line.substring(2).trim();
						have |= 0x01;
					} else if (line.startsWith("Shadow process")) {
						shadowpid = Integer.parseInt(line.substring(25).trim());
						have |= 0x02;
					} else if (line.startsWith("Input Stream")) {
						iStream = line.substring(25).trim();
						have |= 0x04;
					} else if (line.startsWith("Output Stream")) {
						oStream = line.substring(25).trim();
						have |= 0x08;
					} else if (line.startsWith("Error Stream")) {
						eStream = line.substring(25).trim();
						have |= 0x10;
					}
				} catch (final NumberFormatException nfe) {
					// Ignore
				} catch (final StringIndexOutOfBoundsException obe) {
					// Ignore
				}
				line = reader.readLine();
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return new ParseResult(jobId, iStream, oStream, eStream, shadowpid);
	}
}
