package hu.kfki.grid.wmsx.job.result;

import hu.kfki.grid.wmsx.provider.IRemoteWmsxProvider;
import hu.kfki.grid.wmsx.provider.JdlJob;
import hu.kfki.grid.wmsx.provider.WmsxProviderImpl;
import hu.kfki.grid.wmsx.provider.scripts.ScriptLauncher;
import hu.kfki.grid.wmsx.workflow.Workflow;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

public class ResultMoverAndPostexec implements Runnable {

    private final Process process;

    private final File dir;

    private static final Logger LOGGER = Logger
            .getLogger(ResultMoverAndPostexec.class.toString());

    private final JdlJob job;

    public ResultMoverAndPostexec(final Process p, final File d, final JdlJob j) {
        this.process = p;
        this.dir = d;
        this.job = j;
    }

    public void run() {
        try {
            if (this.process != null) {
                this.process.waitFor();
            }
            this.moveResult();
            this.postExec();

        } catch (final InterruptedException e) {
            ResultMoverAndPostexec.LOGGER.warning(e.getMessage());
        }

    }

    private void moveResult() {
        final File[] dirContent = this.dir.listFiles();
        for (int i = 0; i < dirContent.length; i++) {
            final File subdir = dirContent[i];
            if (subdir.isDirectory()) {
                final File[] toMoves = subdir.listFiles();
                for (int j = 0; j < toMoves.length; j++) {
                    final File toMove = toMoves[j];
                    toMove.renameTo(new File(this.dir, toMove.getName()));
                }
                subdir.delete();
            }
        }
    }

    private void postExec() {
        final String postexec = this.job.getPostexec();
        if (postexec != null) {
            final String output = this.job.getOutput();
            ResultMoverAndPostexec.LOGGER.info("Running " + postexec);

            final List<String> cmdVec = new Vector<String>();
            cmdVec.add(postexec);
            cmdVec.add(this.job.getCommand());
            cmdVec.add(this.dir.getAbsolutePath());
            cmdVec.addAll(Arrays.asList(this.job.getArgs()));

            final int postRetVal = ScriptLauncher.getInstance().launchScript(
                    cmdVec.toArray(new String[0]), output + "_postexec");
            if (postRetVal == 1) {
                final String chain = this.job.getChain();
                ResultMoverAndPostexec.LOGGER.info("Running " + chain);
                cmdVec.set(0, chain);
                this.runchain(cmdVec);
            }
        }
        final Workflow wf = this.job.getWorkflow();
        if (wf != null) {
            wf.isDone(this.job);
        }
    }

    private void runchain(final List<String> cmdVec) {
        final OutputStream o = new ByteArrayOutputStream();
        ScriptLauncher.getInstance().launchScript(
                cmdVec.toArray(new String[0]), o);
        final BufferedReader r = new BufferedReader(new StringReader(o
                .toString()));
        final List<IRemoteWmsxProvider.LaszloCommand> l = new Vector<IRemoteWmsxProvider.LaszloCommand>();
        final List<String> j = new Vector<String>();

        final String output = this.job.getOutput();

        try {
            final BufferedWriter debugWriter = new BufferedWriter(
                    new FileWriter(output + "_chain"));
            String line = r.readLine();
            while (line != null) {
                debugWriter.write(line);
                debugWriter.newLine();
                final String[] splitLine = line.split(" ");
                if (splitLine.length == 1) {
                    j.add(line);
                } else {
                    final String command = splitLine[0];
                    final String args = line.substring(command.length() + 1);

                    final IRemoteWmsxProvider.LaszloCommand lcmd = new IRemoteWmsxProvider.LaszloCommand(
                            command, args);
                    l.add(lcmd);
                }
                line = r.readLine();
            }
            debugWriter.close();
        } catch (final IOException e) {
            ResultMoverAndPostexec.LOGGER
                    .fine("IOException: " + e.getMessage());
        }

        boolean did = false;
        if (!j.isEmpty()) {
            ResultMoverAndPostexec.LOGGER.info("Chain returned " + j.size()
                    + " new JDL job(s).");
            for (final String jdl : j) {
                final String nextJdl;
                if (new File(jdl).isAbsolute()) {
                    nextJdl = jdl;
                } else {
                    nextJdl = new File(this.dir, jdl).getAbsolutePath();
                }
                WmsxProviderImpl.getInstance().submitJdl(nextJdl, null, null);
            }
            did = true;
        }
        if (!l.isEmpty()) {
            ResultMoverAndPostexec.LOGGER.info("Chain returned " + l.size()
                    + " new Laszlo job(s).");
            WmsxProviderImpl.getInstance().submitLaszlo(l, false,
                    this.job.getPrefix(), this.job.getName());
            did = true;
        }
        if (!did) {
            ResultMoverAndPostexec.LOGGER.info("Chain returned no new jobs");
        }

    }
}
