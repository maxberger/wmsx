package hu.kfki.grid.wmsx.test;

import hu.kfki.grid.wmsx.InputParser;
import hu.kfki.grid.wmsx.ParseResult;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import junit.framework.TestCase;

public class InputParserTest extends TestCase {

	/**
	 * Create the test case
	 * 
	 * @param testName
	 *            name of the test case
	 */
	public InputParserTest(String testName) {
		super(testName);
	}

	/**
	 * Rigourous Test :-)
	 */
	public void testApp() {
		InputStream is = InputParserTest.class
				.getResourceAsStream("/submit_output");
		PrintStream ps = new PrintStream(new ByteArrayOutputStream());
		ParseResult result = InputParser.parse(is, ps);
		assertEquals(result.getJobId(),
				"https://grid151.kfki.hu:9000/AGDbJ6UIDaRsDJk44AfReA");
		assertEquals(result.getIStream(),
				"/tmp/listener-AGDbJ6UIDaRsDJk44AfReA.in");
		assertEquals(result.getOStream(),
				"/tmp/listener-AGDbJ6UIDaRsDJk44AfReA.out");
		assertEquals(result.getEStream(),
				"/tmp/listener-AGDbJ6UIDaRsDJk44AfReA.err");
		assertEquals(result.getShadowpid(), 12893);
	}

}
