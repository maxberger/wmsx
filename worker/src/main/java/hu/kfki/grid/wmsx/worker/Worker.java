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

package hu.kfki.grid.wmsx.worker;

import hu.kfki.grid.wmsx.util.FileUtil;
import hu.kfki.grid.wmsx.util.ScriptLauncher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import net.jini.id.Uuid;
import net.jini.id.UuidFactory;

public class Worker {

    private static final int MAX_WAIT = 45 * 60;

    private final Uuid uuid;

    final Controller controller;

    final Alive alive;

    private static final Logger LOGGER = Logger.getLogger(Worker.class
            .toString());

    private Worker(final Controller cont) {
        this.controller = cont;
        this.uuid = UuidFactory.generate();
        this.alive = new Alive(cont, this.uuid);
    }

    public void start() {

        long lastChecked = 0;
        long delay = 5;
        Worker.LOGGER.info("Worker started, Uuid is " + this.uuid);
        try {
            while (lastChecked < Worker.MAX_WAIT) {
                Worker.LOGGER.info("Checking for work");
                final WorkDescription todo = this.controller
                        .retrieveWork(this.uuid);

                if (todo != null) {
                    this.performWork(todo);
                    delay = 5;
                    lastChecked = 0;
                } else {
                    Worker.LOGGER.info("Sleeping...");
                    this.alive.stop();
                    lastChecked += delay;
                    if (lastChecked < Worker.MAX_WAIT) {
                        try {
                            Thread.sleep(delay * 1000);
                        } catch (final InterruptedException e) {
                            // ignore
                        }
                    }
                    delay += Math.random() / 2.0 * delay;
                }
            }
        } catch (final RemoteException re) {
            Worker.LOGGER.warning(re.getMessage());
        }
        this.alive.stop();
        Worker.LOGGER.info("Shutting down.");
    }

    @SuppressWarnings("unchecked")
    private void performWork(final WorkDescription todo) throws RemoteException {
        this.alive.start();
        Worker.LOGGER.info("Assigned work: " + todo.getId());
        final File currentDir = new File(".").getAbsoluteFile();
        final File workDir;
        File wd;
        try {
            wd = File.createTempFile("work", "", currentDir).getCanonicalFile();
            wd.delete();
            wd.mkdirs();
        } catch (final IOException ioe) {
            Worker.LOGGER.info(ioe.getMessage());
            wd = currentDir;
        }
        workDir = wd;
        Worker.LOGGER.fine("WorkDir: " + workDir);

        FileUtil.retrieveSandbox(todo.getInputSandbox(), workDir);

        final List<String> arguments = todo.getArguments();
        final List<String> cmdArray = new Vector<String>(1 + arguments.size());
        cmdArray.add(new File(workDir, todo.getExecutable()).getAbsolutePath());
        cmdArray.addAll(arguments);

        Worker.LOGGER.info("Launching...");
        ScriptLauncher.getInstance().launchScript(
                cmdArray.toArray(new String[0]), todo.getStdout(),
                todo.getStderr(), workDir);
        Worker.LOGGER.info("Submitting results...");
        this.controller.doneWith(todo.getId(), new ResultDescription(FileUtil
                .createSandbox(todo.getOutputSandbox(), workDir)), this.uuid);
        if (!currentDir.equals(workDir)) {
            FileUtil.cleanDir(workDir);
        }
    }

    public static void main(final String args[]) {
        Worker.LOGGER.info("Initializing worker...");
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new AllSecurityManager());
        }
        try {
            final String proxyFile;
            if (args.length > 0) {
                proxyFile = args[0];
            } else {
                proxyFile = "proxyFile";
            }
            final FileInputStream fis = new FileInputStream(proxyFile);
            final ObjectInputStream in = new ObjectInputStream(fis);
            final Controller comp = (Controller) in.readObject();
            in.close();
            new Worker(comp).start();
        } catch (final IOException e) {
            Worker.LOGGER.warning(e.getMessage());
        } catch (final ClassNotFoundException e) {
            Worker.LOGGER.warning(e.getMessage());
        }
        Worker.LOGGER.info("Shutting down");
    }
}
