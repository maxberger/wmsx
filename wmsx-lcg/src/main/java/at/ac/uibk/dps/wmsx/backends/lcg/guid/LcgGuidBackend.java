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

package at.ac.uibk.dps.wmsx.backends.lcg.guid;

import hu.kfki.grid.wmsx.backends.lcg.InputParser;
import hu.kfki.grid.wmsx.util.ProcessHelper;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import at.ac.uibk.dps.wmsx.backends.guid.GuidBackend;
import at.ac.uibk.dps.wmsx.backends.lcg.LcgCommon;

/**
 * Implementation for GuidBackend which works by wrapping the glite command line
 * tools.
 * 
 * @version $Date$
 */
public class LcgGuidBackend implements GuidBackend {

    private static final String GUID_PREFIX = "guid:";

    private static final String LCG_CR = "lcg-cr";

    private static final String LCG_CP = "lcg-cp";

    private static final String LCG_DEL = "lcg-del";

    private static final Logger LOGGER = Logger.getLogger(LcgGuidBackend.class
            .toString());

    /**
     * Default constructor.
     */
    public LcgGuidBackend() {
        // nothing to be done.
    }

    /** {@inheritDoc} */
    public boolean isAvailable() {
        return LcgCommon.isAvailable(LcgGuidBackend.LCG_CR);
    }

    /** {@inheritDoc} */
    public String upload(final File localFile) {
        String retVal = null;
        final String[] commandLine = new String[] { LcgCommon.ENV,
                LcgGuidBackend.LCG_CR, localFile.getAbsolutePath(), };
        try {
            final Process p = Runtime.getRuntime().exec(commandLine);
            retVal = InputParser.parseGuid(p.getInputStream());
            ProcessHelper.cleanupProcess(p);
        } catch (final IOException io) {
            LcgGuidBackend.LOGGER.warning(io.getMessage());
        }
        return retVal;
    }

    /** {@inheritDoc} */
    public void download(final String guid, final File file) {
        final String[] commandLine = new String[] { LcgCommon.ENV,
                LcgGuidBackend.LCG_CP, LcgGuidBackend.GUID_PREFIX + guid,
                "file:" + file.getAbsolutePath(), };
        try {
            final Process p = Runtime.getRuntime().exec(commandLine);
            ProcessHelper.cleanupProcess(p);
        } catch (final IOException io) {
            LcgGuidBackend.LOGGER.warning(io.getMessage());
        }
    }

    /** {@inheritDoc} */
    public void delete(final String guid) {
        final String[] commandLine = new String[] { LcgCommon.ENV,
                LcgGuidBackend.LCG_DEL, "-a",
                LcgGuidBackend.GUID_PREFIX + guid, };
        try {
            final Process p = Runtime.getRuntime().exec(commandLine);
            ProcessHelper.cleanupProcess(p);
        } catch (final IOException io) {
            LcgGuidBackend.LOGGER.warning(io.getMessage());
        }
    }

}
