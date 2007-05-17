package hu.kfki.grid.wmsx.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for hu.kfki.grid.wmsx.test");
		// $JUnit-BEGIN$
		suite.addTestSuite(ShadowListenerTest.class);
		suite.addTestSuite(InputParserTest.class);
		// $JUnit-END$
		return suite;
	}

}
