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

    String jdlFilename;

    String output;

    public String resultDir;

    public LaszloJob(final String _cmd, final String _args,
            final String _inputFile, final File _outDir, final File _tmpDir,
            final int _num) {
        this.outDir = _outDir;
        this.tmpDir = _tmpDir;
        this.cmd = _cmd;
        this.args = _args;
        this.inputFile = _inputFile;
        this.num = _num;
        this.jdlFilename = null;
        this.output = null;
    }

    private void prepareJdl() {

        final String base = cmd + "_" + num;
        String extBase = base;
        final String jdlExt = ".jdl";
        final File jdlFile;
        final BufferedWriter out;
        try {
            synchronized (tmpDir) {
                int n = 0;
                File potentialJdlFile = new File(tmpDir, extBase + jdlExt);
                while (potentialJdlFile.exists()) {
                    n++;
                    extBase = base + "." + n;
                    potentialJdlFile = new File(tmpDir, extBase + jdlExt);
                }
                jdlFile = potentialJdlFile;
                out = new BufferedWriter(new FileWriter(jdlFile));
            }
            final File jobShFile = new File(tmpDir, "job.sh");
            final String jobShPath = jobShFile.getAbsolutePath();
            final String jobShName = jobShFile.getName();
            final String outDirs = "out/bplots out/data out/hist out/plots";
            final String profile = "/afs/kfki.hu/home/"
                    + System.getProperty("user.name")
                    + "/public/init/bash/bashrc";
            out.write("[");
            out.newLine();
            out.write("JobType = \"Interactive\";");
            out.newLine();
            out.write("Executable = \"" + jobShName + "\";");
            out.newLine();
            out.write("Arguments = \"");
            out.write(cmd + " ");
            out.write(args.replaceAll("\"", "\\\\\"") + " ");
            out.write("\\\"" + outDirs + "\\\" ");
            out.write(profile);
            out.write("\";");
            out.newLine();
            out.write("InputSandBox = {\"" + jobShPath + "\", \"" + inputFile
                    + "\"};");
            out.newLine();
            out.write("OutputSandBox = {\"out.tar.gz\"};");
            out.newLine();

            // echo "Requirements = ($REQ);" >> log/submit.jdl

            out.write("]");
            out.newLine();
            out.close();
            this.jdlFilename = jdlFile.getAbsolutePath();
            this.output = new File(tmpDir, extBase + ".out").getAbsolutePath();
            this.resultDir = new File(outDir, extBase).getAbsolutePath();
        } catch (IOException io) {
            this.jdlFilename = null;
            this.output = null;
            this.resultDir = null;
        }
    }

    public String getJdlFile() {
        if (this.jdlFilename == null)
            this.prepareJdl();
        return this.jdlFilename;
    }

    public String getOutput() {
        if (this.jdlFilename == null)
            this.prepareJdl();
        return this.output;
    }

    public String getResultDir() {
        return this.resultDir;
    }
}
