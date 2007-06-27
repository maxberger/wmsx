package hu.kfki.grid.wmsx.provider;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LaszloJob implements JobDesc {

    public final File outDir;

    public final File tmpDir;

    public final String cmd;

    public final String args;

    public final String inputFile;

    public final int num;

    public final boolean requireAfs;

    String jdlFilename;

    String output;

    public String resultDir;

    public LaszloJob(final String _cmd, final String _args,
            final String _inputFile, final File _outDir, final File _tmpDir,
            final int _num, final boolean _requireAfs) {
        this.outDir = _outDir;
        this.tmpDir = _tmpDir;
        this.cmd = _cmd;
        this.args = _args;
        this.inputFile = _inputFile;
        this.num = _num;
        this.jdlFilename = null;
        this.output = null;
        this.requireAfs = _requireAfs;
    }

    private void prepareJdl() {

        final boolean interactive = false;

        if (this.jdlFilename != null) {
            return;
        }

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
            if (interactive) {
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
            if (!interactive) {
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
            this.jdlFilename = jdlFile.getAbsolutePath();
            this.output = new File(this.tmpDir, extBase + ".out")
                    .getAbsolutePath();
            this.resultDir = new File(this.outDir, extBase).getAbsolutePath();
        } catch (final IOException io) {
            this.jdlFilename = null;
            this.output = null;
            this.resultDir = null;
        }
    }

    public String getJdlFile() {
        this.prepareJdl();
        return this.jdlFilename;
    }

    public String getOutput() {
        this.prepareJdl();
        return this.output;
    }

    public String getResultDir() {
        this.prepareJdl();
        return this.resultDir;
    }
}
