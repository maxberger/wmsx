package hu.kfki.grid.wmsx.job.submit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.logging.Logger;

public class InputParser {

    private static final Logger LOGGER = Logger.getLogger(InputParser.class
            .toString());

    public static ParseResult parse(final InputStream inStream,
            final PrintStream outStream) {

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
        return new ParseResult(jobId, iStream, oStream, eStream, shadowpid,
                port);
    }
}
