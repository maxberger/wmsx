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
            final String jobShPath = jobShFile.getAbsolutePath();
            final String jobShName = jobShFile.getName();
            final String outDirs = "out/bplots out/data out/hist out/plots";
            final String profile = "/afs/kfki.hu/home/"
                    + System.getProperty("user.name")
                    + "/public/init/bash/bashrc";
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
            out.write("Executable = \"" + jobShName + "\";");
            out.newLine();
            out.write("Arguments = \"");
            out.write(this.cmd + " ");
            out.write(this.args.replaceAll("\"", "\\\\\"") + " ");
            out.write("\\\"" + outDirs + "\\\" ");
            out.write(profile);
            if (this.requireAfs) {
                out.write(" afs");
            }
            out.write("\";");
            out.newLine();
            out.write("InputSandBox = {\"" + jobShPath + "\", \""
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

}
