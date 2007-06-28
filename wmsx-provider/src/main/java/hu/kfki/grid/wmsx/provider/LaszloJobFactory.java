package hu.kfki.grid.wmsx.provider;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LaszloJobFactory implements JobFactory {

    private final File outDir;

    private final File tmpDir;

    private final String cmd;

    private final String args;

    private final String inputFile;

    private final int num;

    private final boolean requireAfs;

    private final boolean interactive;

    public LaszloJobFactory(final String _cmd, final String _args,
            final String _inputFile, final File _outDir, final File _tmpDir,
            final int _num, final boolean _requireAfs,
            final boolean _interactive) {
        this.outDir = _outDir;
        this.tmpDir = _tmpDir;
        this.cmd = _cmd;
        this.args = _args;
        this.inputFile = _inputFile;
        this.num = _num;
        this.requireAfs = _requireAfs;
        this.interactive = _interactive;
    }

    public JdlJob createJdlJob() {

        final String base = this.cmd + "_" + this.num;
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
            final File jobShFile = new File(this.tmpDir, "job.sh");
            final File starterFile = new File(this.tmpDir, extBase + ".sh");
            this.writeJdl(out, jobShFile, starterFile);

            final String outDirs = "out/bplots out/data out/hist out/plots";
            final String profile = "/afs/kfki.hu/home/"
                    + System.getProperty("user.name")
                    + "/public/init/bash/bashrc";

            this.prepareStarterFile(starterFile, outDirs, profile);

            final String jdlFilename = jdlFile.getAbsolutePath();
            final String output = new File(this.tmpDir, extBase + ".out")
                    .getAbsolutePath();
            final String resultDir = new File(this.outDir, extBase)
                    .getAbsolutePath();
            return new JdlJob(jdlFilename, output, resultDir);
        } catch (final IOException io) {
            return null;
        }
    }

    private void writeJdl(final BufferedWriter out, final File jobShFile,
            final File starterFile) throws IOException {
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
                + "\", \"" + jobShFile.getAbsolutePath() + "\", \""
                + this.inputFile + "\"};");
        out.newLine();
        out.write("OutputSandBox = {\"out.tar.gz\"");
        if (!this.interactive) {
            out.write(",\"StdOut\",\"StdErr\"");
        }
        out.write("};");
        out.newLine();
        if (this.requireAfs) {
            out
                    .write("Requirements = (Member(\"AFS\",other.GlueHostApplicationSoftwareRunTimeEnvironment));");
            out.newLine();
        }
        // echo "Requirements = ($REQ);" >> log/submit.jdl

        out.write("]");
        out.newLine();
        out.close();
    }

    private void prepareStarterFile(final File starterFile,
            final String outDirs, final String profile) throws IOException {
        final BufferedWriter jobStarter = new BufferedWriter(new FileWriter(
                starterFile));
        jobStarter.write("#!/bin/sh");
        jobStarter.newLine();
        jobStarter.write("chmod +x ./job.sh");
        jobStarter.newLine();
        jobStarter.write("./job.sh ");
        jobStarter.write(this.cmd + " ");
        jobStarter.write(this.args + " ");
        jobStarter.write("\"" + outDirs + "\" ");
        if (this.requireAfs) {
            jobStarter.write(profile);
            jobStarter.write(" afs");
        }
        jobStarter.newLine();
        jobStarter.close();
    }

}
