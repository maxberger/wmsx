/*
 * WMSX - Workload Management Extensions for gLite
 * 
 * Copyright (C) 2007-2008 Max Berger
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see http://www.gnu.org/licenses/.
 * 
 */

/* $Id$ */

package hu.kfki.grid.wmsx.provider.test;

import hu.kfki.grid.wmsx.job.description.JDLJobDescription;
import hu.kfki.grid.wmsx.job.description.JobDescription;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 * 
 * @version $Revision$
 */
public class JdlTest extends TestCase {
    /**
     * Create the test case.
     * 
     * @param testName
     *            name of the test case
     */
    public JdlTest(final String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(JdlTest.class);
    }

    /**
     * Rigorous Test :-).
     * 
     * @throws Exception
     *             if the test fails.
     */
    public void testJdl() throws Exception {
        final File jdl = File.createTempFile("wmsxtest", null);
        final BufferedWriter w = new BufferedWriter(new FileWriter(jdl));
        w.write("[");
        w.newLine();

        w.write("Executable=\"bla\";");
        w.newLine();
        w.write("Strange=\"bla\";");
        w.newLine();
        w.write("#Comment=\"bla\";");
        w.newLine();

        w.write("]");
        w.close();
        jdl.deleteOnExit();

        final JobDescription desc = new JDLJobDescription(jdl);
        Assert.assertEquals("bla", desc
                .getStringEntry(JobDescription.EXECUTABLE));
        Assert.assertEquals(null, desc.getStringEntry("Comment"));

        final JobDescription desc2 = desc.clone();
        desc.replaceEntry(JobDescription.EXECUTABLE, "Hahaha!");
        Assert.assertEquals("bla", desc2
                .getStringEntry(JobDescription.EXECUTABLE));
        Assert.assertEquals(null, desc2.getStringEntry("Comment"));

    }
}
