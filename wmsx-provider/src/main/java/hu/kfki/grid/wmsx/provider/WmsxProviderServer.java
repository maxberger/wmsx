package hu.kfki.grid.wmsx.provider;

import hu.kfki.grid.wmsx.WmsxEntry;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import net.jini.core.entry.Entry;
import net.jini.core.lease.Lease;
import net.jini.core.lease.UnknownLeaseException;
import net.jini.core.lookup.ServiceID;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceRegistration;
import net.jini.discovery.DiscoveryEvent;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.LookupDiscovery;
import net.jini.export.Exporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.InvocationLayerFactory;
import net.jini.jeri.ServerEndpoint;
import net.jini.jeri.tcp.TcpServerEndpoint;
import net.jini.lease.LeaseListener;
import net.jini.lease.LeaseRenewalEvent;
import net.jini.lease.LeaseRenewalManager;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import com.sun.jini.admin.DestroyAdmin;
import com.sun.jini.lookup.entry.BasicServiceType;

public class WmsxProviderServer implements DiscoveryListener, LeaseListener,
        DestroyAdmin {
    private static final Logger LOGGER = Logger
            .getLogger(WmsxProviderServer.class.toString());

    protected LeaseRenewalManager leaseManager = new LeaseRenewalManager();

    protected ServiceID serviceID = null;

    protected WmsxProviderProxy smartProxy = null;

    protected Remote rmiProxy = null;

    protected WmsxProviderImpl impl = null;

    private final List activeRegistrations = new Vector();

    private final Object keepAlive = new Object();

    private LookupDiscovery discover = null;

    private Exporter exporter = null;

    private static void printHelp(final Options options) {
        new HelpFormatter().printHelp("wmsx-provider (-h|[-v] workdir)",
                options);
    }

    public static void main(final String args[]) {

        final Options options = new Options();
        final CommandLineParser parser = new PosixParser();
        options.addOption(new Option("h", "help", false, "print this message"));
        options.addOption(new Option("v", "verbose", false,
                "print debug messages to stdout"));
        File workdir = new File("/tmp");
        boolean debugToStdOut = true;
        try {
            final CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption('h')) {
                WmsxProviderServer.printHelp(options);
                System.exit(0);
            }
            final List largs = cmd.getArgList();
            if (largs.size() != 1) {
                throw new ParseException("Need workdir!");
            }
            workdir = new File((String) largs.get(0)).getCanonicalFile();
            if (!workdir.exists()) {
                workdir.mkdirs();
            }
            if (!workdir.exists() || !workdir.isDirectory()) {
                throw new IOException("Invalid Directory: " + workdir);
            }
            debugToStdOut = cmd.hasOption('v');
        } catch (final ParseException e1) {
            System.out.println("Invalid command line: " + e1.getMessage());
            WmsxProviderServer.printHelp(options);
            System.exit(2);
        } catch (final IOException e) {
            System.out.println("Error accessing workdir: " + e.getMessage());
            WmsxProviderServer.printHelp(options);
            System.exit(2);
        }

        WmsxProviderServer.setupLogging(workdir, debugToStdOut);

        final WmsxProviderServer server = new WmsxProviderServer(workdir);

        // keep server running forever to
        // - allow time for locator discovery and
        // - keep re-registering the lease
        synchronized (server.keepAlive) {
            try {
                server.keepAlive.wait();
            } catch (final java.lang.InterruptedException e) {
                // do nothing
            }
        }
        WmsxProviderServer.LOGGER.info("Terminated.");
    }

    private static void setupLogging(final File workdir, boolean stdout) {
        final File logDir = new File(workdir, "log");
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
        try {
            final Logger rootLogger = Logger.getLogger("");

            if (!stdout) {
                final Handler[] handlers = rootLogger.getHandlers();
                for (int i = 0; i < handlers.length; i++) {
                    final Handler h = handlers[i];
                    rootLogger.removeHandler(h);
                }
            }

            final Handler logHandler = new FileHandler(new File(logDir,
                    "wmsx%g.log").getAbsolutePath(), 1024 * 1024, 7);
            logHandler.setFormatter(new SimpleFormatter());
            rootLogger.addHandler(logHandler);
        } catch (final IOException io) {
            WmsxProviderServer.LOGGER.warning(io.getMessage());
        }
        if (!stdout) {
            System.out.close();
            System.err.close();
        }
    }

    public WmsxProviderServer(final File workDir) {

        WmsxProviderServer.LOGGER.info("Using workdir: "
                + workDir.getAbsolutePath());

        // Create the service
        this.impl = new WmsxProviderImpl(this, workDir);

        // Try to load the service ID from file.
        // It isn't an error if we can't load it, because
        // maybe this is the first time this service has run
        // DataInputStream din = null;
        //
        // try {
        // din = new DataInputStream(new FileInputStream(this.getClass()
        // .getName()
        // + ".id"));
        // this.serviceID = new ServiceID(din);
        // } catch (final Exception e) {
        // // ignore
        // }

        try {

            final InvocationLayerFactory invocationLayerFactory = new BasicILFactory();
            // ServerEndpoint endpoint = TcpServerEndpoint.getInstance(0);
            final ServerEndpoint endpoint = TcpServerEndpoint.getInstance(
                    "127.0.0.1", 0);
            // ServerEndpoint endpoint = TcpServerEndpoint.getInstance(
            // "::1", 0);
            this.exporter = new BasicJeriExporter(endpoint,
                    invocationLayerFactory, false, true);

            this.rmiProxy = this.exporter.export(this.impl);
        } catch (final Exception e) {
            WmsxProviderServer.LOGGER.severe(e.getMessage());
            System.exit(1);
        }

        // System.setSecurityManager(new RMISecurityManager());

        // proxy primed with impl
        this.smartProxy = new WmsxProviderProxy(this.rmiProxy);

        this.registerTmp();
        // this.registerLocally();
        // this.registerThroughLookup();
    }

    private void registerTmp() {
        try {
            final String proxyFile = "/tmp/wmsx-"
                    + System.getProperty("user.name");
            final FileOutputStream fos = new FileOutputStream(proxyFile);
            final ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(this.smartProxy);
            out.close();
            WmsxProviderServer.LOGGER.info("Written Proxy to " + proxyFile);
        } catch (final IOException io) {
            WmsxProviderServer.LOGGER.warning(io.getMessage());
        }
    }

    // private void registerThroughLookup() {
    // try {
    // this.discover = new LookupDiscovery(LookupDiscovery.ALL_GROUPS);
    // } catch (final Exception e) {
    // WmsxProviderServer.LOGGER.severe("Discovery failed: "
    // + e.getMessage());
    // System.exit(1);
    // }
    //
    // this.discover.addDiscoveryListener(this);
    // }

    // private void registerLocally() {
    // try {
    // final LookupLocator localLocator = new LookupLocator(
    // "jini://127.0.0.1/");
    // final ServiceRegistrar reg = localLocator.getRegistrar();
    // this.register(reg);
    // } catch (final MalformedURLException e1) {
    // WmsxProviderServer.LOGGER.warning(e1.getMessage());
    // } catch (final IOException e) {
    // WmsxProviderServer.LOGGER.fine(e.getMessage());
    // } catch (final ClassNotFoundException e) {
    // WmsxProviderServer.LOGGER.warning(e.getMessage());
    // }
    // }

    public Entry[] getTypes() {
        return new Entry[] { new BasicServiceType("WMS-X"),
                new WmsxEntry(System.getProperty("user.name")) };
    }

    public void discovered(final DiscoveryEvent evt) {
        final ServiceRegistrar[] registrars = evt.getRegistrars();

        for (int n = 0; n < registrars.length; n++) {
            final ServiceRegistrar registrar = registrars[n];
            this.register(registrar);
        }
    }

    public void register(final ServiceRegistrar registrar) {
        final ServiceItem item = new ServiceItem(this.serviceID,
                this.smartProxy, this.getTypes());
        ServiceRegistration reg = null;

        try {
            reg = registrar.register(item, Lease.FOREVER);
        } catch (final java.rmi.RemoteException e) {
            WmsxProviderServer.LOGGER.warning("Register exception: "
                    + e.getMessage());
            return;
        }
        synchronized (this.activeRegistrations) {
            this.activeRegistrations.add(reg);
        }

        WmsxProviderServer.LOGGER.info("Service registered with id "
                + reg.getServiceID());

        // set lease renewal in place
        this.leaseManager.renewUntil(reg.getLease(), Lease.FOREVER, this);

        // set the serviceID if necessary
        if (this.serviceID == null) {
            this.serviceID = reg.getServiceID();

            // try to save the service ID in a file
            // DataOutputStream dout = null;
            // try {
            // dout = new DataOutputStream(new FileOutputStream(this
            // .getClass().getName()
            // + ".id"));
            // this.serviceID.writeBytes(dout);
            // dout.flush();
            // } catch (final Exception e) {
            // // ignore
            // }
        }

    }

    public void notify(final LeaseRenewalEvent evt) {
        WmsxProviderServer.LOGGER.fine("Lease expired " + evt.toString());
    }

    public void discarded(final DiscoveryEvent evt) {
        // ignore
    }

    public void destroy() throws RemoteException {
        final List regs;
        if (this.discover != null) {
            this.discover.terminate();
        }
        this.discover = null;
        synchronized (this.activeRegistrations) {
            regs = new Vector(this.activeRegistrations);
        }
        final Iterator it = regs.iterator();
        while (it.hasNext()) {
            final ServiceRegistration reg = (ServiceRegistration) it.next();
            try {
                if (this.leaseManager != null) {
                    this.leaseManager.cancel(reg.getLease());
                } else {
                    reg.getLease().cancel();
                }
            } catch (final UnknownLeaseException e) {
                // ignore
            }
        }
        this.leaseManager = null;
        if (this.exporter != null) {
            this.exporter.unexport(true);
        }
        this.exporter = null;
        synchronized (this.keepAlive) {
            this.keepAlive.notifyAll();
        }
    }

} // JiniServiceServer
