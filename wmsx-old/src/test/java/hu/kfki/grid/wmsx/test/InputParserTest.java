package hu.kfki.grid.wmsx.test;

import hu.kfki.grid.wmsx.InputParser;
import hu.kfki.grid.wmsx.ParseResult;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import junit.framework.Assert;
import junit.framework.TestCase;

public class InputParserTest extends TestCase {

	/**
	 * Create the test case
	 * 
	 * @param testName
	 *            name of the test case
	 */
	public InputParserTest(final String testName) {
		super(testName);
	}

	/**
	 * Rigourous Test :-)
	 */
	public void testApp() {
		final InputStream is = InputParserTest.class
				.getResourceAsStream("/submit_output");
		final PrintStream ps = new PrintStream(new ByteArrayOutputStream());
		final ParseResult result = InputParser.parse(is, ps);
		Assert.assertEquals(result.getJobId(),
				"https://grid151.kfki.hu:9000/AGDbJ6UIDaRsDJk44AfReA");
		Assert.assertEquals(result.getIStream(),
				"/tmp/listener-AGDbJ6UIDaRsDJk44AfReA.in");
		Assert.assertEquals(result.getOStream(),
				"/tmp/listener-AGDbJ6UIDaRsDJk44AfReA.out");
		Assert.assertEquals(result.getEStream(),
				"/tmp/listener-AGDbJ6UIDaRsDJk44AfReA.err");
		Assert.assertEquals(result.getShadowpid(), 12893);
	}

}
