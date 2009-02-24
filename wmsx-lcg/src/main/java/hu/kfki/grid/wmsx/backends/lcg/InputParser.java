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
 * @version $Date$
 */
public final class InputParser {

    private static final int EDG_CONTENT_START_INDEX = 25;

    private static final int HAVE_JOBID = 0x01;

    private static final int HAVE_SHADOW = 0x02;

    private static final int HAVE_INPUT = 0x04;

    private static final int HAVE_OUTPUT = 0x08;

    private static final int HAVE_ERROR = 0x10;

    private static final int HAVE_PORT = 0x20;

    private static final int HAVE_ALL = InputParser.HAVE_JOBID
            | InputParser.HAVE_SHADOW | InputParser.HAVE_INPUT
            | InputParser.HAVE_OUTPUT | InputParser.HAVE_ERROR
            | InputParser.HAVE_PORT;

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
     * @param isInteractive
     *            if true, look for lines important to interactive jobs
     * @param needError
     *            if true, look for error stream on interactive jobs.
     * @return A {@link SubmissionResults} object containing the parsed
     *         submission, or null.
     */
    public static SubmissionResults parseSubmission(final InputStream inStream,
            final PrintStream outStream, final Backend backend,
            final boolean isInteractive, final boolean needError) {
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
            if (!isInteractive) {
                have |= InputParser.HAVE_ERROR | InputParser.HAVE_INPUT
                        | InputParser.HAVE_OUTPUT | InputParser.HAVE_PORT
                        | InputParser.HAVE_SHADOW;
            }
            if (!needError) {
                have |= InputParser.HAVE_ERROR;
            }
            while (line != null && have != InputParser.HAVE_ALL) {
                outStream.println(line);
                line = line.trim();
                InputParser.LOGGER.finest(line);
                try {
                    if (line.length() > 0 && line.charAt(0) == '-'
                            && jobId == null) {
                        jobId = line.substring(2).trim();
                        have |= InputParser.HAVE_JOBID;
                    } else if (line.startsWith("Shadow process")) {
                        shadowpid = Integer.parseInt(line.substring(
                                InputParser.EDG_CONTENT_START_INDEX).trim());
                        have |= InputParser.HAVE_SHADOW;
                    } else if (line.startsWith("Port")) {
                        port = Integer.parseInt(line.substring(
                                InputParser.EDG_CONTENT_START_INDEX).trim());
                        have |= InputParser.HAVE_PORT;
                    } else if (line.startsWith("Input Stream")) {
                        iStream = line.substring(
                                InputParser.EDG_CONTENT_START_INDEX).trim();
                        have |= InputParser.HAVE_INPUT;
                    } else if (line.startsWith("Output Stream")) {
                        oStream = line.substring(
                                InputParser.EDG_CONTENT_START_INDEX).trim();
                        have |= InputParser.HAVE_OUTPUT;
                    } else if (line.startsWith("Error Stream")) {
                        eStream = line.substring(
                                InputParser.EDG_CONTENT_START_INDEX).trim();
                        have |= InputParser.HAVE_ERROR;
                    } else if (line.startsWith("https://")) {
                        jobId = line.trim();
                        have |= InputParser.HAVE_JOBID;
                    } else if (line.startsWith("- Port")) {
                        port = Integer.parseInt(InputParser.splitcolon(line));
                        have |= InputParser.HAVE_PORT;
                    } else if (line.startsWith("- Shadow")) {
                        shadowpid = Integer.parseInt(InputParser
                                .splitcolon(line));
                        have |= InputParser.HAVE_SHADOW;
                    } else if (line.startsWith("- Input")) {
                        iStream = InputParser.splitcolon(line);
                        have |= InputParser.HAVE_INPUT;
                    } else if (line.startsWith("- Output")) {
                        oStream = InputParser.splitcolon(line);
                        have |= InputParser.HAVE_OUTPUT;
                    }
                } catch (final NumberFormatException nfe) {
                    InputParser.LOGGER.fine(nfe.getMessage());
                } catch (final StringIndexOutOfBoundsException obe) {
                    InputParser.LOGGER.fine(obe.getMessage());
                }
                line = reader.readLine();
            }
            reader.close();
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

    private static String splitcolon(final String line) {
        return line.split(":")[1].trim();
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
                            || "done (exit code !=0)".equals(statusStr)
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
            reader.close();
        } catch (final IOException io) {
            InputParser.LOGGER.warning(io.getMessage());
        }
        return retVal;
    }

    /**
     * Try to parse the guid: string from an lcg-cr command.
     * 
     * @param inputStream
     *            InputStream to parse
     * @return the guid or null.
     */
    public static String parseGuid(final InputStream inputStream) {
        String retVal = null;
        try {
            final BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream));
            String line = reader.readLine();
            boolean found = false;
            while (line != null && !found) {
                if (line.startsWith("guid:")) {
                    retVal = line.split(":")[1].trim();
                    found = true;
                }
                line = reader.readLine();
            }
            reader.close();
        } catch (final IOException io) {
            InputParser.LOGGER.warning(io.getMessage());
        }
        return retVal;
    }
}
