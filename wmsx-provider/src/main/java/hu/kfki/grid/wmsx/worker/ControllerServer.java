package hu.kfki.grid.wmsx.worker;

import hu.kfki.grid.wmsx.provider.JdlJobFactory;
import hu.kfki.grid.wmsx.provider.JobFactory;
import hu.kfki.grid.wmsx.provider.WmsxProviderImpl;
import hu.kfki.grid.wmsx.util.FileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.rmi.server.ExportException;
import java.util.logging.Logger;

import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.InvocationLayerFactory;
import net.jini.jeri.ServerEndpoint;
import net.jini.jeri.tcp.TcpServerEndpoint;

public class ControllerServer {

    private static ControllerServer instance;

    private final Controller controller;

    private final Controller controllerStub;

    private JobFactory jobFactory;

    private static final Logger LOGGER = Logger
            .getLogger(ControllerServer.class.toString());

    private ControllerServer() {

        this.controller = new ControllerImpl();

        final InvocationLayerFactory invocationLayerFactory = new BasicILFactory();

        Controller stub = null;
        int port = 20000;
        while (stub == null && port < 25000) {
            try {
                final ServerEndpoint endpoint = TcpServerEndpoint
                        .getInstance(port);
                stub = (Controller) new BasicJeriExporter(endpoint,
                        invocationLayerFactory, false, true)
                        .export(this.controller);
            } catch (final ExportException e) {
                port++;
                stub = null;
            }

        }
        this.controllerStub = stub;
    }

    public void writeProxy(final File where) throws IOException {
        final FileOutputStream fos = new FileOutputStream(where);
        final ObjectOutputStream out = new ObjectOutputStream(fos);
        out.writeObject(this.controllerStub);
        out.close();
    }

    public void prepareWorker(final File tmpDir) {
        // final File tmpDir = this.syncableDir(this.debugDir, "worker");

        try {
            final File jdlFile = new File(tmpDir, "worker.jdl");
            FileUtil.copy(ClassLoader
                    .getSystemResourceAsStream("worker/worker.jdl"), jdlFile);
            FileUtil.copy(ClassLoader
                    .getSystemResourceAsStream("worker/worker.tar.gz"),
                    new File(tmpDir, "worker.tar.gz"));
            final File shFile = new File(tmpDir, "worker.sh");
            FileUtil.copy(ClassLoader
                    .getSystemResourceAsStream("worker/worker.sh"), shFile);
            try {
                Runtime.getRuntime().exec(
                        new String[] { "/bin/chmod", "+x",
                                shFile.getCanonicalPath() }).waitFor();
            } catch (final InterruptedException e) {
                // Ignore
            }
            ControllerServer.getInstance().writeProxy(
                    new File(tmpDir, "proxyFile"));
            this.jobFactory = new JdlJobFactory(jdlFile.getCanonicalPath(),
                    null, null);
        } catch (final IOException e) {
            ControllerServer.LOGGER.warning(e.toString());
        }
    }

    public void submitWorker() {
        if (this.jobFactory != null) {
            WmsxProviderImpl.getInstance().addJobFactory(this.jobFactory);
        }

    }

    public static synchronized ControllerServer getInstance() {
        if (ControllerServer.instance == null) {
            ControllerServer.instance = new ControllerServer();
        }
        return ControllerServer.instance;
    }

}
