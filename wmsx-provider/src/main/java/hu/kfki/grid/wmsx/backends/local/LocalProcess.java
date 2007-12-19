package hu.kfki.grid.wmsx.backends.local;

import hu.kfki.grid.wmsx.job.JobState;
import hu.kfki.grid.wmsx.job.description.JobDescription;
import hu.kfki.grid.wmsx.provider.scripts.ScriptLauncher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class LocalProcess implements Runnable {

    File workdir;

    final Map<Object, JobState> stateMap;

    final Object uid;

    final File baseDir;

    final JobDescription job;

    private static final Logger LOGGER = Logger.getLogger(LocalProcess.class
            .toString());

    public LocalProcess(final Map<Object, JobState> state, final Object id,
            final File jdlDir, final JobDescription desc) {
        state.put(id, JobState.NONE);
        this.stateMap = state;
        this.uid = id;
        this.baseDir = jdlDir;
        this.job = desc;
        this.workdir = null;
    }

    public synchronized void run() {
        this.stateMap.put(this.uid, JobState.STARTUP);
        try {
            this.startup();
            this.stateMap.put(this.uid, JobState.RUNNING);
            this.running();
            this.stateMap.put(this.uid, JobState.SUCCESS);
        } catch (final IOException e) {
            LocalProcess.LOGGER.warning(e.getMessage());
            this.stateMap.put(this.uid, JobState.FAILED);
        }
    }

    private void startup() throws IOException {
        this.workdir = File.createTempFile("wmsx", null);
        this.workdir.delete();
        this.workdir.mkdirs();
        LocalProcess.LOGGER.info(this.workdir.toString());

        final List<String> inputList = this.job
                .getListEntry(JobDescription.INPUTSANDBOX);
        this.copyList(inputList, this.baseDir, this.workdir);
    }

    private void copyList(final List<String> inputList, final File from,
            final File to) throws IOException {
        IOException ex = null;
        final Iterator<String> it = inputList.iterator();
        while (it.hasNext()) {
            final String fileName = it.next();
            final File fileNameFile = new File(fileName);
            final File inputFile;
            if (fileNameFile.isAbsolute()) {
                inputFile = fileNameFile;
            } else {
                inputFile = new File(from, fileName);
            }
            final File toFile = new File(to, inputFile.getName());
            try {
                LocalProcess.copy(inputFile, toFile);
            } catch (final IOException e) {
                LocalProcess.LOGGER.warning(e.getMessage());
                ex = e;
            }
        }
        if (ex != null) {
            throw new IOException("Error copying some files");
        }
    }

    private static void copy(final File in, final File out) throws IOException {
        final FileInputStream fin = new FileInputStream(in);
        final FileOutputStream fout = new FileOutputStream(out);
        final FileChannel inChannel = fin.getChannel();
        final FileChannel outChannel = fout.getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } catch (final IOException ia) {
            // This is due to a bug in Java 1.4
            try {
                final byte[] b = new byte[4096];
                int count;
                do {
                    count = fin.read(b);
                    if (count > 0) {
                        fout.write(b, 0, count);
                    }
                } while (count > 0);
            } catch (final IOException o) {
                throw ia;
            } finally {
                if (fin != null) {
                    fin.close();
                }
                if (fout != null) {
                    fout.close();
                }
            }
        } finally {
            if (inChannel != null) {
                inChannel.close();
            }
            if (outChannel != null) {
                outChannel.close();
            }
        }

        try {
            Runtime.getRuntime().exec(
                    new String[] { "/bin/chmod",
                            "--reference=" + in.getCanonicalPath(),
                            out.getCanonicalPath() }).waitFor();
        } catch (final InterruptedException e) {
            // Ignore
        }
    }

    private void running() throws IOException {
        final String commande = this.job
                .getStringEntry(JobDescription.EXECUTABLE);
        if (commande == null) {
            return;
        }
        final String command;
        if (new File(commande).isAbsolute()) {
            command = commande;
        } else {
            command = new File(this.workdir, commande).getCanonicalPath();
        }
        try {
            Runtime.getRuntime().exec(
                    new String[] { "/bin/chmod", "+x", command }).waitFor();
        } catch (final InterruptedException e) {
            // Ignore
        }

        final String arguments = this.job
                .getStringEntry(JobDescription.ARGUMENTS);
        final String commandline;
        if (arguments == null) {
            commandline = command;
        } else {
            commandline = command + " " + arguments;
        }
        String stdout = this.job.getStringEntry(JobDescription.STDOUTPUT);
        if (stdout != null) {
            stdout = new File(this.workdir, stdout).getCanonicalPath();
        }
        ScriptLauncher.getInstance().launchScript(commandline, this.workdir,
                stdout);
    }

    public synchronized void retrieveOutput(final File dir) {
        if (this.workdir == null) {
            return;
        }
        final File realTarget = new File(dir, "sub" + this.uid);
        realTarget.mkdirs();
        final List<String> list = this.job
                .getListEntry(JobDescription.OUTPUTSANDBOX);
        try {
            this.copyList(list, this.workdir, realTarget);
        } catch (final IOException e) {
            LocalProcess.LOGGER.warning(e.getMessage());
        }

        this.cleanup();
        this.workdir = null;
    }

    private void cleanup() {
        this.cleanDir(this.workdir);
    }

    private void cleanDir(final File dir) {
        final File[] entries = dir.listFiles();
        if (entries != null) {
            for (int i = 0; i < entries.length; i++) {
                final File f = entries[i];
                if (f.isDirectory()) {
                    this.cleanDir(f);
                } else if (f.isFile()) {
                    f.delete();
                }
            }
        }
        dir.delete();
    }
}
