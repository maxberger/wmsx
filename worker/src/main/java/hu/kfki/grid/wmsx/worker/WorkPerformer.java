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

package hu.kfki.grid.wmsx.worker;

import hu.kfki.grid.wmsx.util.FileUtil;
import hu.kfki.grid.wmsx.util.ScriptLauncher;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import java.util.logging.Logger;

import net.jini.id.Uuid;

/**
 * Performs the actual work.
 * 
 * @version $Revision$
 */
public final class WorkPerformer {

    private static final Logger LOGGER = Logger.getLogger(WorkPerformer.class
            .toString());

    private final Set<String> deployedWfs = new TreeSet<String>();

    private static final class SingletonHolder {
        private static final WorkPerformer INSTANCE = new WorkPerformer();

        private SingletonHolder() {
        }
    }

    private WorkPerformer() {
    }

    /**
     * @return Singleton instance
     */
    public static WorkPerformer getInstance() {
        return WorkPerformer.SingletonHolder.INSTANCE;
    }

    private void logWithTime(final String base) {
        WorkPerformer.LOGGER.info(base + " ( " + System.currentTimeMillis()
                + " )");
    }

    /**
     * Performs the given work.
     * 
     * @param todo
     *            The work to do
     * @param controller
     *            Controller to report back to
     * @param uuid
     *            Uuid of the worker to report back with.
     * @throws RemoteException
     *             when failing to talk back to the controller.
     */
    public void performWork(final WorkDescription todo,
            final Controller controller, final Uuid uuid)
            throws RemoteException {
        this.logWithTime("Assigned work: " + todo.getId());
        final File currentDir = new File(".").getAbsoluteFile();
        final File workDir;
        final String wfid = todo.getWorkflowId();
        final boolean partOfWf = wfid != null;
        workDir = this.prepareWorkdir(currentDir, wfid, partOfWf);
        this.logWithTime("Retrieving sandbox to WorkDir: " + workDir + " "
                + todo.getInputSandbox().keySet());

        FileUtil.retrieveSandbox(todo.getInputSandbox(), workDir);

        final boolean deploy;
        if (partOfWf) {
            if (this.deployedWfs.contains(wfid)) {
                deploy = false;
            } else {
                this.deployedWfs.add(wfid);
                deploy = true;
            }
        } else {
            deploy = true;
        }
        if (deploy) {
            this.launchDeploy(todo, workDir);
        }

        this.launchExec(todo, workDir);
        this.logWithTime("Submitting results");
        controller.doneWith(todo.getId(), new ResultDescription(FileUtil
                .createSandbox(todo.getOutputSandbox(), workDir)), uuid);
        if (!currentDir.equals(workDir)) {
            FileUtil.cleanDir(workDir, partOfWf);
        }
    }

    private File prepareWorkdir(final File currentDir, final String wfid,
            final boolean partOfWf) {
        File wd;
        try {
            if (partOfWf) {
                wd = new File(currentDir, "wfwork" + wfid).getCanonicalFile();
            } else {
                wd = File.createTempFile("swork", "", currentDir)
                        .getCanonicalFile();
                if (!wd.delete()) {
                    throw new IOException("Failed to delete workdir file: "
                            + wd.getAbsolutePath());
                }
            }
            if (!wd.exists() && !wd.mkdirs()) {
                throw new IOException("Failed to create workdir: "
                        + wd.getAbsolutePath());
            }
        } catch (final IOException ioe) {
            WorkPerformer.LOGGER.info(ioe.getMessage());
            wd = currentDir;
        }
        return wd;
    }

    private void launchDeploy(final WorkDescription todo, final File workDir) {
        final String deploy = todo.getDeploy();
        if (deploy != null) {
            final String completePath = new File(workDir, deploy)
                    .getAbsolutePath();
            this.logWithTime("Deploying");
            ScriptLauncher.getInstance().launchScript(
                    new String[] { completePath, }, todo.getStdout(),
                    todo.getStderr(), workDir);

        }
    }

    private void launchExec(final WorkDescription todo, final File workDir) {
        final List<String> arguments = todo.getArguments();
        final List<String> cmdArray = new Vector<String>(1 + arguments.size());
        cmdArray.add(new File(workDir, todo.getExecutable()).getAbsolutePath());
        cmdArray.addAll(arguments);

        this.logWithTime("Launching");
        ScriptLauncher.getInstance().launchScript(
                cmdArray.toArray(new String[0]), todo.getStdout(),
                todo.getStderr(), workDir);
    }

}
