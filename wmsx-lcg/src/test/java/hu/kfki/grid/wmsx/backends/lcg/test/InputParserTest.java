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

package hu.kfki.grid.wmsx.backends.lcg.test;

import hu.kfki.grid.wmsx.backends.lcg.InputParser;

import java.io.InputStream;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test for input parser.
 * 
 * @version $Date$
 */
public class InputParserTest {

    /**
     * Default constructor.
     */
    public InputParserTest() {
        // nothing to do.
    }

    /**
     * Test for {@link InputParser#parseGuid}.
     * 
     * @throws Exception
     *             if the test fails.
     */
    @Test
    public void testParseGuid() throws Exception {
        final InputStream inputStream = InputParserTest.class
                .getResourceAsStream("/lcg-cr.txt");
        final String guid = InputParser.parseGuid(inputStream);
        Assert.assertEquals(guid, "a57dbdb8-660b-41d0-bb6b-5703086fd551");
    }
}
