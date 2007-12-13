package hu.kfki.grid.wmsx.provider.arglist;

import hu.kfki.grid.wmsx.provider.JdlJob;
import hu.kfki.grid.wmsx.provider.JobFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;
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

    private final boolean interactive;

    private final String prefix;

    private final String name;

    public LaszloJobFactory(final String _cmd, final String _args,
            final File _outDir, final File _tmpDir, final int _num,
            final boolean _interactive, final String _prefix, final String _name) {
        this.outDir = _outDir;
        this.tmpDir = _tmpDir;
        this.cmdWithPath = _cmd;
        this.args = _args;
        this.num = _num;
        this.interactive = _interactive;
        this.name = _name;
        if (_prefix == null) {
            this.prefix = this.num + "_";
        } else {
            this.prefix = _prefix;
        }
    }

    public JdlJob createJdlJob() {

        final String cmd = this.getCmd();
        final String base = this.prefix + cmd;
        String extBase = base;
        final String jdlExt = ".jdl";
        final File jdlFile;
        final BufferedWriter out;
        try {
            synchronized (this.tmpDir) {
                int n = 1;
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

            final ArglistJdlReader jdlArgs = new ArglistJdlReader(
                    this.cmdWithPath + ".jdl", this.getCmd());

            this.writeJdl(out, starterFile, jdlArgs);
            this.prepareStarterFile(starterFile, jdlArgs);

            final String jdlFilename = jdlFile.getAbsolutePath();
            final String resultDir = new File(this.outDir, extBase)
                    .getAbsolutePath();
            // final String output = new File(this.tmpDir, extBase + ".out")
            // .getAbsolutePath();
            final String output = new File(resultDir, LaszloJobFactory.STD_OUT)
                    .getAbsolutePath();
            final JdlJob job = new JdlJob(jdlFilename, output, resultDir);
            job.setPreexec(this.cmdWithPath + "_preexec");
            job.setPostexec(this.cmdWithPath + "_postexec");
            job.setChain(this.cmdWithPath + "_chain");
            job.setCommand(this.cmdWithPath);
            job.setArgs(this.args.split(" "));
            job.setPrefix(this.prefix);
            job.setName(this.name);
            return job;
        } catch (final IOException io) {
            return null;
        }
    }

    private String getCmd() {
        final String cmd = new File(this.cmdWithPath).getName();
        return cmd;
    }

    private void writeJdl(final BufferedWriter out, final File starterFile,
            final ArglistJdlReader jdlArgs) throws IOException {
        boolean realInteractive = this.interactive || jdlArgs.getInteractive();
        final String inputFile = new File(new File(this.cmdWithPath)
                .getParentFile(), jdlArgs.getArchive()).getCanonicalPath();
        out.write("[");
        out.newLine();
        if (realInteractive) {
            out.write("JobType = \"Interactive\";");
        } else {
            out.write("JobType = \"Normal\";");
            out.newLine();
            out.write("StdOutput = \"" + LaszloJobFactory.STD_OUT + "\";");
            out.newLine();
            // out.write("StdError = \"StdErr\";");
            out.write("StdError = \"" + LaszloJobFactory.STD_OUT + "\";");
        }
        out.newLine();
        out.write("Executable = \"" + starterFile.getName() + "\";");
        out.newLine();
        out.write("InputSandBox = {\"" + starterFile.getAbsolutePath()
                + "\", \"" + inputFile + "\"};");
        out.newLine();
        out.write("OutputSandBox = {\"out.tar.gz\"");
        if (!realInteractive) {
            out.write(",\"" + LaszloJobFactory.STD_OUT + "\"");
            // out.write(",\"StdErr\"");
        }
        out.write("};");
        out.newLine();
        String reqValue = null;

        if (jdlArgs.getAfs()) {
            reqValue = "(Member(\"AFS\",other.GlueHostApplicationSoftwareRunTimeEnvironment))";
        }
        final String extraReq = jdlArgs.getRequirements();
        if (extraReq != null) {
            if (reqValue == null) {
                reqValue = "";
            } else {
                reqValue = reqValue + " && ";
            }
            reqValue = reqValue + extraReq;
        }
        if (reqValue != null) {
            out.write("Requirements = " + reqValue + ";");
            out.newLine();
        }

        out.write("]");
        out.newLine();
        out.close();
    }

    private void prepareStarterFile(final File starterFile,
            final ArglistJdlReader jdlArgs) throws IOException {
        final BufferedWriter jobStarter = new BufferedWriter(new FileWriter(
                starterFile));

        jobStarter.write("#!/bin/sh");
        jobStarter.newLine();

        jobStarter.write("ARCHIVE=" + jdlArgs.getArchive());
        jobStarter.newLine();
        jobStarter.write("PROGRAM=" + jdlArgs.getExecutable());
        jobStarter.newLine();
        jobStarter.write("PROGDIR=" + jdlArgs.getProgramDir());
        jobStarter.newLine();
        jobStarter.write("PARAMS=\"" + this.args + "\"");
        jobStarter.newLine();
        jobStarter.write("OUTDIR=" + jdlArgs.getOutputDirectory());
        jobStarter.newLine();
        if (jdlArgs.getAfs()) {
            jobStarter.write("AFS=true");
            jobStarter.newLine();
        }
        final List software = jdlArgs.getSoftware();
        if (!software.isEmpty()) {
            jobStarter.write("SOFTWARE=\"");
            final Iterator it = software.iterator();
            while (it.hasNext()) {
                final String s = (String) it.next();
                jobStarter.write(s);
                jobStarter.write(" ");
            }
            jobStarter.write("\"");
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
