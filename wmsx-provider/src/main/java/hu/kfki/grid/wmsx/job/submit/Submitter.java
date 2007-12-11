package hu.kfki.grid.wmsx.job.submit;

import hu.kfki.grid.wmsx.backends.Backend;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.logging.Logger;

import edg.workload.userinterface.jclient.JobId;

public class Submitter {

    private static Submitter submitter;

    private static final Logger LOGGER = Logger.getLogger(Submitter.class
            .toString());

    private Submitter() {
    }

    static synchronized public Submitter getSubmitter() {
        if (Submitter.submitter == null) {
            Submitter.submitter = new Submitter();
        }
        return Submitter.submitter;
    }

    public ParseResult submitJdl(final String jdlFile, final String vo,
            final Backend backend) throws IOException {
        final List commandLine = backend.submitJdl(jdlFile, vo);
        final Process p = Runtime.getRuntime().exec(
                (String[]) commandLine.toArray(new String[commandLine.size()]),
                null, new File(jdlFile).getParentFile());
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final PrintStream parserOutput = new PrintStream(baos);
        // final PrintStream parserOutput = System.out;
        final ParseResult result = InputParser.parse(p.getInputStream(),
                parserOutput);

        String jobIdStr = result.getJobId();
        if (jobIdStr != null) {
            try {
                new JobId(jobIdStr);
            } catch (final IllegalArgumentException iae) {
                jobIdStr = null;
            }
        }

        if (jobIdStr == null) {
            Submitter.LOGGER.warning("Failed to submit Job.");
            Submitter.LOGGER.info(baos.toString());
            return null;
        } else {
            Submitter.LOGGER.fine(baos.toString());
        }
        return result;
    }
}
