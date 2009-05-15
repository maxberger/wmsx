/*
 * WMSX - Workload Management Extensions for gLite
 * 
 * Copyright (C) 2007-2009 Max Berger
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
 */

/* $Id$ */
package hu.kfki.grid.wmsx.job;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.junit.Assert;

/**
 * Test for {@link JobState}.
 * 
 * @version $Date: 1/1/2000$
 */
public class JobStateTest extends TestCase {

    /**
     * Does nothing.
     */
    public JobStateTest() {
        super("JobStateTest");
    }

    /**
     * @return This testsuite.
     */
    public static Test suite() {
        return new TestSuite(JobStateTest.class);
    }

    /**
     * Tests basic equalities.
     * 
     * @throws Exception
     *             if the test fails.
     */
    public void testBasic() throws Exception {
        final JobState state = JobState.RUNNING;
        Assert.assertNotSame(state, JobState.NONE);
        Assert.assertEquals(state, JobState.RUNNING);
        Assert.assertSame(state, JobState.RUNNING);
    }
}
