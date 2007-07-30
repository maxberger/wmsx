package hu.kfki.grid.wmsx.provider;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.logging.Logger;

public class LaszloJobFactory implements JobFactory {

    private static final Logger LOGGER = Logger
            .getLogger(LaszloJobFactory.class.toString());

    private static final String STD_OUT = "StdOut";

    private final File outDir;

    private final File tmpDir;

    private final String cmdWithPath;

    private final String args;

    private final int num;

    private final boolean requireAfs;

    private final boolean interactive;

    public LaszloJobFactory(final String _cmd, final String _args,
            final File _outDir, final File _tmpDir, final int _num,
            final boolean _requireAfs, final boolean _interactive) {
        this.outDir = _outDir;
        this.tmpDir = _tmpDir;
        this.cmdWithPath = _cmd;
        this.args = _args;
        this.num = _num;
        this.requireAfs = _requireAfs;
        this.interactive = _interactive;
    }

    public JdlJob createJdlJob() {

        final String cmd = this.getCmd();

        final String base = cmd + "_" + this.num;
        String extBase = base;
        final String jdlExt = ".jdl";
        final File jdlFile;
        final BufferedWriter out;
        try {
            synchronized (this.tmpDir) {
                int n = 0;
                File potentialJdlFile = new File(this.tmpDir, extBase + jdlExt);
                while (potentialJdlFile.exists()) {
                    n++;
                    extBase = base + "." + n;
                    potentialJdlFile = new File(this.tmpDir, extBase + jdlExt);
                }
                jdlFile = potentialJdlFile;
                out = new BufferedWriter(new FileWriter(jdlFile));
            }
            final File starterFile = new File(this.tmpDir, extBase + ".sh");
            this.writeJdl(out, starterFile);

            this.prepareStarterFile(starterFile);

            final String jdlFilename = jdlFile.getAbsolutePath();
            final String resultDir = new File(this.outDir, extBase)
                    .getAbsolutePath();
            // final String output = new File(this.tmpDir, extBase + ".out")
            // .getAbsolutePath();
            final String output = new File(resultDir, LaszloJobFactory.STD_OUT)
                    .getAbsolutePath();
            return new JdlJob(jdlFilename, output, resultDir);
        } catch (final IOException io) {
            return null;
        }
    }

    private String getCmd() {
        final String cmd = new File(this.cmdWithPath).getName();
        return cmd;
    }

    private void writeJdl(final BufferedWriter out, final File starterFile)
            throws IOException {
        final String inputFile = this.cmdWithPath + ".tar.gz";
        out.write("[");
        out.newLine();
        if (this.interactive) {
            out.write("JobType = \"Interactive\";");
        } else {
            out.write("JobType = \"Normal\";");
            out.newLine();
            out.write("StdOutput = \"StdOut\";");
            out.newLine();
            out.write("StdError = \"StdErr\";");
        }
        out.newLine();
        out.write("Executable = \"" + starterFile.getName() + "\";");
        out.newLine();
        out.write("InputSandBox = {\"" + starterFile.getAbsolutePath()
                + "\", \"" + inputFile + "\"};");
        out.newLine();
        out.write("OutputSandBox = {\"out.tar.gz\"");
        if (!this.interactive) {
            out.write(",\"" + LaszloJobFactory.STD_OUT + "\",\"StdErr\"");
        }
        out.write("};");
        out.newLine();
        if (this.requireAfs) {
            out
                    .write("Requirements = (Member(\"AFS\",other.GlueHostApplicationSoftwareRunTimeEnvironment));");
            out.newLine();
        }
        out.write("]");
        out.newLine();
        out.close();
    }

    private void prepareStarterFile(final File starterFile) throws IOException {
        final BufferedWriter jobStarter = new BufferedWriter(new FileWriter(
                starterFile));
        jobStarter.write("#!/bin/sh");
        jobStarter.newLine();

        jobStarter.write("PROGAM=" + this.getCmd());
        jobStarter.newLine();
        jobStarter.write("PARAMS=" + this.args);
        jobStarter.newLine();
        jobStarter.write("OUTDIR=" + "out");
        jobStarter.newLine();
        if (this.requireAfs) {
            jobStarter.write("AFS=true");
            jobStarter.newLine();
        }

        this.copyFromStarterBase(jobStarter);
        jobStarter.close();
    }

    private void copyFromStarterBase(final BufferedWriter jobStarter) {
        try {
            final InputStream in = ClassLoader
                    .getSystemResourceAsStream("starter_base.sh");
            final Reader freader = new InputStreamReader(in);
            final BufferedReader reader = new BufferedReader(freader);
            String line = reader.readLine();
            while (line != null) {
                jobStarter.write(line);
                jobStarter.newLine();
                line = reader.readLine();
            }
            in.close();
        } catch (final IOException e) {
            LaszloJobFactory.LOGGER
                    .warning("Error copying from starter_base.sh: "
                            + e.getMessage());
        }
    }

}
