package hu.kfki.grid.wmsx.test;

import hu.kfki.grid.wmsx.ParseResult;

import java.io.File;

import junit.framework.TestCase;

public class ShadowListenerTest extends TestCase {

	int rnd;

	File tempDir;

	ParseResult fakeResult;

	// private String tmpName(String ext) {
	// return new File(tempDir, "test-shadow-" + rnd + "." + ext)
	// .getAbsolutePath();
	// }

	protected void setUp() throws Exception {
		// super.setUp();
		// rnd = new Random().nextInt(Integer.MAX_VALUE);
		// String tempDirS = System.getProperty("java.io.tempdir");
		// if (tempDirS == null)
		// tempDirS = "/tmp";
		// tempDir = new File(tempDirS);
		// fakeResult = new ParseResult("", tmpName("in"), tmpName("out"),
		// tmpName("err"), 0);
	}

	public void testShadow() throws Exception {
		// Runtime.getRuntime().exec(
		// new String[] { "mkfifo", fakeResult.getOStream() }).waitFor();
		// ShadowListener.listen(fakeResult);
		// Writer ow = new OutputStreamWriter(new FileOutputStream(fakeResult
		// .getOStream()));
		// ow.write("Bla!");
		// ow.close();
	}

}
