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

package hu.kfki.grid.wmsx.backends.lcg;

import hu.kfki.grid.wmsx.backends.Backend;
import hu.kfki.grid.wmsx.backends.JobUid;
import hu.kfki.grid.wmsx.backends.SubmissionResults;
import hu.kfki.grid.wmsx.job.JobState;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * Helper class to support output parsing of gLites command line tools.
 * 
 * @version $Revision$
 */
public final class InputParser {

    private static final Logger LOGGER = Logger.getLogger(InputParser.class
            .toString());

    private InputParser() {
        // Empty on purpose.
    }

    /**
     * Parse the result of a jdl submission via gLite command line tools.
     * 
     * @param inStream
     *            Input Stream to parse
     * @param outStream
     *            where to print the output to.
     * @param backend
     *            Backend which was in use.
     * @return A {@link SubmissionResults} object containing the parsed
     *         submission, or null.
     */
    public static SubmissionResults parseSubmission(final InputStream inStream,
            final PrintStream outStream, final Backend backend) {
        String jobId = null;
        String iStream = null;
        String oStream = null;
        String eStream = null;
        int shadowpid = 0;
        int port = 0;
        try {

            final BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inStream));
            String line = reader.readLine();

            int have = 0;
            while (line != null && have != 0x3f) {
                outStream.println(line);
                line = line.trim();
                try {
                    if (line.charAt(0) == '-' && jobId == null) {
                        jobId = line.substring(2).trim();
                        have |= 0x01;
                    } else if (line.startsWith("Shadow process")) {
                        shadowpid = Integer.parseInt(line.substring(25).trim());
                        have |= 0x02;
                    } else if (line.startsWith("Port")) {
                        port = Integer.parseInt(line.substring(25).trim());
                        have |= 0x20;
                    } else if (line.startsWith("Input Stream")) {
                        iStream = line.substring(25).trim();
                        have |= 0x04;
                    } else if (line.startsWith("Output Stream")) {
                        oStream = line.substring(25).trim();
                        have |= 0x08;
                    } else if (line.startsWith("Error Stream")) {
                        eStream = line.substring(25).trim();
                        have |= 0x10;
                    } else if (line.startsWith("https://")) {
                        jobId = line.trim();
                        have |= 0x01;
                    }
                } catch (final NumberFormatException nfe) {
                    InputParser.LOGGER.fine(nfe.getMessage());
                } catch (final StringIndexOutOfBoundsException obe) {
                    InputParser.LOGGER.fine(obe.getMessage());
                }
                line = reader.readLine();
            }
        } catch (final IOException e) {
            InputParser.LOGGER.fine(e.getMessage());
        }

        if (jobId == null) {
            return null;
        } else {
            return new SubmissionResults(new JobUid(backend, jobId), iStream,
                    oStream, eStream, shadowpid, port);
        }
    }

    /**
     * Parse the status output of a given input Stream.
     * 
     * @param i
     *            InputStream to parse
     * @return a JobState with the current status.
     */
    public static JobState parseStatus(final InputStream i) {
        JobState retVal = JobState.NONE;
        try {
            final BufferedReader reader = new BufferedReader(
                    new InputStreamReader(i));
            String line = reader.readLine();
            boolean found = false;
            while (line != null && !found) {
                line = line.toLowerCase(Locale.ENGLISH);
                if (line.startsWith("current status:")) {
                    final String statusStr = line.split(":")[1].trim();
                    if ("submitted".equals(statusStr)
                            || "scheduled".equals(statusStr)
                            || "ready".equals(statusStr)
                            || "waiting".equals(statusStr)) {
                        retVal = JobState.STARTUP;
                    } else if ("running".equals(statusStr)) {
                        retVal = JobState.RUNNING;
                    } else if ("done (success)".equals(statusStr)
                            || "cleared".equals(statusStr)) {
                        retVal = JobState.SUCCESS;
                    } else if ("aborted".equals(statusStr)
                            || "cancelled".equals(statusStr)
                            || "done (failed)".equals(statusStr)) {
                        retVal = JobState.FAILED;
                    } else {
                        InputParser.LOGGER.warning("Unknown State: "
                                + statusStr);
                    }
                    found = true;
                }
                line = reader.readLine();
            }
        } catch (final IOException io) {
            InputParser.LOGGER.warning(io.getMessage());
        }
        return retVal;
    }
}
