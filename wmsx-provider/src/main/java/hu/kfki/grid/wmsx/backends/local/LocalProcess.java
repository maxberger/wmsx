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
 * 
 */

/* $Id: vasblasd$ */

package hu.kfki.grid.wmsx.backends.local;

import hu.kfki.grid.wmsx.backends.JobUid;
import hu.kfki.grid.wmsx.job.JobState;
import hu.kfki.grid.wmsx.job.JobWatcher;
import hu.kfki.grid.wmsx.job.description.JobDescription;
import hu.kfki.grid.wmsx.util.FileUtil;
import hu.kfki.grid.wmsx.util.ScriptLauncher;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class LocalProcess implements Runnable {

    File workdir;

    final Map<JobUid, JobState> stateMap;

    final JobUid uid;

    final JobDescription job;

    private static final Logger LOGGER = Logger.getLogger(LocalProcess.class
            .toString());

    public LocalProcess(final Map<JobUid, JobState> state, final JobUid id,
            final JobDescription desc) {
        state.put(id, JobState.NONE);
        this.stateMap = state;
        this.uid = id;
        this.job = desc;
        this.workdir = null;
    }

    public synchronized void run() {
        this.stateMap.put(this.uid, JobState.STARTUP);
        JobWatcher.getWatcher().checkWithState(this.uid, JobState.STARTUP);
        try {
            this.startup();
            this.stateMap.put(this.uid, JobState.RUNNING);
            JobWatcher.getWatcher().checkWithState(this.uid, JobState.RUNNING);
            this.running();
            this.stateMap.put(this.uid, JobState.SUCCESS);
            JobWatcher.getWatcher().checkWithState(this.uid, JobState.SUCCESS);
        } catch (final IOException e) {
            LocalProcess.LOGGER.warning(e.getMessage());
            this.stateMap.put(this.uid, JobState.FAILED);
            JobWatcher.getWatcher().checkWithState(this.uid, JobState.FAILED);
        }
    }

    private void startup() throws IOException {
        this.workdir = File.createTempFile("wmsx", null);
        this.workdir.delete();
        this.workdir.mkdirs();
        LocalProcess.LOGGER.info(this.workdir.toString());

        final List<String> inputList = this.job
                .getListEntry(JobDescription.INPUTSANDBOX);
        this.copyList(inputList, this.job.getBaseDir(), this.workdir);
    }

    private void copyList(final List<String> inputList, final File from,
            final File to) throws IOException {
        IOException ex = null;
        final Iterator<String> it = inputList.iterator();
        while (it.hasNext()) {
            final String fileName = it.next();
            final File inputFile = FileUtil.resolveFile(from, fileName);
            final File toFile = new File(to, inputFile.getName());
            try {
                FileUtil.copy(inputFile, toFile);
            } catch (final IOException e) {
                LocalProcess.LOGGER.warning(e.getMessage());
                ex = e;
            }
        }
        if (ex != null) {
            throw new IOException("Error copying some files");
        }
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
        final File realTarget = new File(dir, "sub" + this.uid.getBackendId());
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
        FileUtil.cleanDir(this.workdir);
    }

}
