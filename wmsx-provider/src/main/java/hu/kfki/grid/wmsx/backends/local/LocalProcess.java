/*
 * WMSX - Workload Management Extensions for gLite
 * 
 * Copyright (C) 2007-2008 Max Berger
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see http://www.gnu.org/licenses/.
 */

/* $Id$ */

package hu.kfki.grid.wmsx.backends.local;

import hu.kfki.grid.wmsx.backends.JobUid;
import hu.kfki.grid.wmsx.job.JobState;
import hu.kfki.grid.wmsx.job.JobWatcher;
import hu.kfki.grid.wmsx.job.description.JobDescription;
import hu.kfki.grid.wmsx.util.FileUtil;
import hu.kfki.grid.wmsx.util.ScriptLauncher;
import hu.kfki.grid.wmsx.util.ScriptProcessListener;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Wrapper for a process on the local machine.
 * 
 * @version $Date$
 */
public class LocalProcess implements Runnable, ScriptProcessListener {

    private static final Logger LOGGER = Logger.getLogger(LocalProcess.class
            .toString());

    private File workdir;

    private final Map<JobUid, JobState> stateMap;

    private final JobUid uid;

    private final JobDescription job;

    private Process process;

    private volatile boolean failed;

    /**
     * Create a localProcess wrapper.
     * 
     * @param state
     *            jobstate to update
     * @param id
     *            id of the job
     * @param desc
     *            jobdescription.
     */
    public LocalProcess(final Map<JobUid, JobState> state, final JobUid id,
            final JobDescription desc) {
        state.put(id, JobState.NONE);
        this.stateMap = state;
        this.uid = id;
        this.job = desc;
        this.workdir = null;
    }

    /** runs the job. */
    public synchronized void run() {
        this.stateMap.put(this.uid, JobState.STARTUP);
        JobWatcher.getInstance().checkWithState(this.uid, JobState.STARTUP);
        try {
            if (!this.failed) {
                this.startup();
            }
            if (!this.failed) {
                this.stateMap.put(this.uid, JobState.RUNNING);
                JobWatcher.getInstance().checkWithState(this.uid,
                        JobState.RUNNING);
                this.running();
            }
            if (!this.failed) {
                this.stateMap.put(this.uid, JobState.SUCCESS);
                JobWatcher.getInstance().checkWithState(this.uid,
                        JobState.SUCCESS);
            }
        } catch (final IOException e) {
            LocalProcess.LOGGER.warning(e.getMessage());
            this.failed = true;
        }
        if (this.failed) {
            this.stateMap.put(this.uid, JobState.FAILED);
            JobWatcher.getInstance().checkWithState(this.uid, JobState.FAILED);
            this.cleanup();
        }
    }

    private void startup() throws IOException {
        this.workdir = FileUtil.createTempDir();
        LocalProcess.LOGGER.info(this.workdir.toString());

        final List<String> inputList = this.job
                .getListEntry(JobDescription.INPUTSANDBOX);
        FileUtil.copyList(inputList, this.job.getBaseDir(), this.workdir);
    }

    private void running() throws IOException {
        final String commande = this.job
                .getStringEntry(JobDescription.EXECUTABLE);
        if (commande == null) {
            return;
        }
        final String command = FileUtil.resolveFile(this.workdir, commande)
                .getCanonicalPath();
        FileUtil.makeExecutable(new File(command));

        final String arguments = this.job
                .getStringEntry(JobDescription.ARGUMENTS);

        final StringBuilder commandline = new StringBuilder(command);
        if (arguments != null) {
            commandline.append(' ');
            commandline.append(arguments);
        }
        String stdout = this.job.getStringEntry(JobDescription.STDOUTPUT);
        if (stdout != null) {
            stdout = new File(this.workdir, stdout).getCanonicalPath();
        }
        String stderr = this.job.getStringEntry(JobDescription.STDERROR);
        if (stderr != null) {
            stderr = new File(this.workdir, stderr).getCanonicalPath();
        }
        ScriptLauncher.getInstance().launchScript(commandline.toString(),
                this.workdir, stdout, stderr, this);
    }

    /**
     * Retrieve the output and store it in dir.
     * 
     * @param dir
     *            directory to store into.
     */
    public synchronized void retrieveOutput(final File dir) {
        if (this.workdir == null) {
            return;
        }
        final File realTarget = new File(dir, "sub" + this.uid.getBackendId());
        if (!realTarget.mkdirs()) {
            LocalProcess.LOGGER.warning("Failed to create " + realTarget);
        }
        final List<String> list = this.job
                .getListEntry(JobDescription.OUTPUTSANDBOX);
        try {
            FileUtil.copyList(list, this.workdir, realTarget);
        } catch (final IOException e) {
            LocalProcess.LOGGER.warning(e.getMessage());
        }

        this.cleanup();
        this.workdir = null;
    }

    private void cleanup() {
        if (this.workdir != null) {
            FileUtil.cleanDir(this.workdir, false);
        }
    }

    /**
     * 
     */
    public void tryToDestroy() {
        synchronized (this) {
            this.failed = true;
            if (this.process != null) {
                this.process.destroy();
            }
        }

    }

    /** {@inheritDoc} */
    public void gotProcess(final Process p) {
        synchronized (this) {
            this.process = p;
        }
    }

}
