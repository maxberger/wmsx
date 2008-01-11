package hu.kfki.grid.wmsx.worker;

import hu.kfki.grid.wmsx.util.FileUtil;
import hu.kfki.grid.wmsx.util.ScriptLauncher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.rmi.RemoteException;
import java.util.logging.Logger;

public class Worker {

    final Controller controller;

    private static final Logger LOGGER = Logger.getLogger(Worker.class
            .toString());

    private Worker(final Controller cont) {
        this.controller = cont;
    }

    public void start() {

        long lastChecked = 0;
        long delay = 60;
        Worker.LOGGER.info("Worker started");
        try {
            while (lastChecked < 15 * 60) {
                final WorkDescription todo = this.controller.retrieveWork();

                if (todo != null) {
                    this.performWork(todo);
                    delay = 60;
                    lastChecked = 0;
                } else {
                    try {
                        Thread.sleep(delay * 1000);
                    } catch (final InterruptedException e) {
                        // ignore
                    }
                    lastChecked += delay;
                    // delay *= 2;
                }
            }
        } catch (final RemoteException re) {
            Worker.LOGGER.warning(re.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void performWork(final WorkDescription todo) throws RemoteException {
        Worker.LOGGER.info("Assigned work" + todo.getId());
        final File currentDir = new File(".").getAbsoluteFile();
        FileUtil.retrieveSandbox(todo.getInputSandbox(), currentDir);

        ScriptLauncher.getInstance().launchScript(
                new File(currentDir, todo.getExecutable()).getAbsolutePath(),
                currentDir,
                new File(currentDir, todo.getStdout()).getAbsolutePath());
        this.controller.doneWith(todo.getId(), new ResultDescription(FileUtil
                .createSandbox(todo.getOutputSandbox(), currentDir)));
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
