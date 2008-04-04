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

/* $Id: vasblasd$ */

package hu.kfki.grid.wmsx.backends.lcg;

import hu.kfki.grid.wmsx.backends.Backend;
import hu.kfki.grid.wmsx.backends.JobUid;
import hu.kfki.grid.wmsx.backends.SubmissionResults;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.logging.Logger;

import edg.workload.userinterface.jclient.JobId;

public class InputParser {

    private static final Logger LOGGER = Logger.getLogger(InputParser.class
            .toString());

    public static SubmissionResults parse(final InputStream inStream,
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

        if (jobId != null) {
            try {
                new JobId(jobId);
            } catch (final IllegalArgumentException iae) {
                jobId = null;
            }
        }

        if (jobId == null) {
            return null;
        } else {
            return new SubmissionResults(new JobUid(backend, new JobId(jobId)),
                    iStream, oStream, eStream, shadowpid, port);
        }
    }
}
