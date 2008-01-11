package hu.kfki.grid.wmsx.worker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.rmi.RemoteException;
import java.util.Map;
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
                    delay *= 2;
                }
            }
        } catch (final RemoteException re) {
            Worker.LOGGER.warning(re.getMessage());
        }
    }

    private void performWork(final WorkDescription todo) throws RemoteException {
        Worker.LOGGER.info("Assigned work" + todo.getId());
        this.retrieveInputSandbox(todo.getInputSandbox());
        this.controller.doneWith(todo.getId(), new ResultDescription());

    }

    private void retrieveInputSandbox(final Map<String, byte[]> inputSandbox) {
        for (final Map.Entry<String, byte[]> entry : inputSandbox.entrySet()) {
            try {
                final File f = new File(entry.getKey()).getCanonicalFile();
                final FileOutputStream fos = new FileOutputStream(f);
                fos.write(entry.getValue());
                fos.close();
                try {
                    Runtime.getRuntime().exec(
                            new String[] { "/bin/chmod", "+x",
                                    f.getAbsolutePath() }).waitFor();
                } catch (final InterruptedException e) {
                    // Ignore
                }
            } catch (final IOException ioe) {
                Worker.LOGGER.warning(ioe.getMessage());
            }
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
