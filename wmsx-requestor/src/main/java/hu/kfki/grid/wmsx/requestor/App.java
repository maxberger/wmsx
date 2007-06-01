package hu.kfki.grid.wmsx.requestor;

import hu.kfki.grid.wmsx.Wmsx;
import hu.kfki.grid.wmsx.WmsxEntry;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.logging.Logger;

import net.jini.admin.Administrable;
import net.jini.core.entry.Entry;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.discovery.DiscoveryEvent;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.LookupDiscovery;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import com.sun.jini.admin.DestroyAdmin;

/**
 * Hello world!
 * 
 */
public class App implements DiscoveryListener {

    private static final Logger LOGGER = Logger.getLogger(App.class.toString());

    private static final int CMD_SHUTDOWN = 0;

    private static final int CMD_NUMBER = 1;

    private static final int CMD_JDL = 2;

    private static final int CMD_PING = 3;

    private static final int CMD_FULLPING = 4;

    private static final int CMD_LASZLO = 5;

    private final int command;

    private final String cmdarg;

    private final String output;

    private static LookupDiscovery discover = null;

    private static boolean found = false;

    private static final Object foundLock = new Object();

    public static void main(final String[] args) {

        final Options options = new Options();

        final OptionGroup commands = new OptionGroup();
        commands.setRequired(true);
        commands
                .addOption(new Option("h", "help", false, "print this message"));
        commands.addOption(new Option("k", "kill", false,
                "shutdown the service provider"));
        commands.addOption(new Option("p", "ping", false,
                "quick check if provider is running"));
        commands.addOption(new Option("f", "full-ping", false,
                "full check if provider is running"));
        commands.addOption(new Option("j", "jdl", true, "submit a JDL file"));
        commands.addOption(new Option("a", "args", true,
                "submit a Laszlo-style args file"));
        commands.addOption(new Option("n", "number", true,
                "set number of active jobs"));
        options.addOptionGroup(commands);
        options.addOption(new Option("o", "output", true,
                "redirect interactive output to file"));

        final CommandLineParser parser = new PosixParser();
        try {
            final CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption('h')) {
                App.printHelp(options);
            } else if (cmd.hasOption('k')) {
                App.dispatch(App.CMD_SHUTDOWN, null, null);
            } else if (cmd.hasOption('p')) {
                App.dispatch(App.CMD_PING, null, null);
            } else if (cmd.hasOption('f')) {
                App.dispatch(App.CMD_FULLPING, null, null);
            } else if (cmd.hasOption('n')) {
                App.dispatch(App.CMD_NUMBER, cmd.getOptionValue('n'), null);
            } else if (cmd.hasOption('a')) {
                App.dispatch(App.CMD_LASZLO, cmd.getOptionValue('a'), null);
            } else if (cmd.hasOption('j')) {
                App.dispatch(App.CMD_JDL, cmd.getOptionValue('j'), cmd
                        .getOptionValue('o'));
            }
        } catch (final ParseException e1) {
            System.out.println("Invalid command line:" + e1.getMessage());
            App.printHelp(options);
            System.exit(2);
        }
    }

    private static void printHelp(final Options options) {
        new HelpFormatter()
                .printHelp(
                        "wmsx-requestor (-h|-k|-n num|-j jdlFile [-o outFile]|-a argsFile)",
                        options);
    }

    private static void dispatch(final int cmd, final String arg,
            final String out) {
        new App(cmd, arg, out);

        // stay around long enough to receive replies
        // try {
        // Thread.sleep(100000L);
        // } catch (final java.lang.InterruptedException e) {
        // // do nothing
        // }
        // App.LOGGER
        // .info("Failed to connect to provider. Please check if its running.");

        synchronized (App.foundLock) {
            try {
                App.foundLock.wait(30000);
            } catch (final InterruptedException e) {
                // ignore
            }
        }
        if (!App.found) {
            App.LOGGER
                    .info("Failed to connect to provider. Please check if its running.");
            System.exit(1);
        }
        if (App.discover != null) {
            App.discover.terminate();
            App.discover = null;
        }
    }

    public App(final int cmd, final String arg, final String outputFile) {
        this.command = cmd;
        this.cmdarg = arg;
        this.output = outputFile;
        // System.setSecurityManager(new RMISecurityManager());

        // this.discoverLookup();
        this.discoverTmp();
        // this.discoverLocally();
    }

    // private void discoverLookup() {
    // try {
    // App.discover = new LookupDiscovery(LookupDiscovery.ALL_GROUPS);
    // App.discover.addDiscoveryListener(this);
    // this.addAllRegistrars(App.discover.getRegistrars());
    // } catch (final IOException e) {
    // App.LOGGER.severe(e.getMessage());
    // System.exit(1);
    // }
    // }

    // private void discoverLocally() {
    // try {
    // final LookupLocator localLocator = new LookupLocator(
    // "jini://127.0.0.1/");
    // final ServiceRegistrar reg = localLocator.getRegistrar();
    // this.haveReg(reg);
    // } catch (final MalformedURLException e1) {
    // App.LOGGER.warning(e1.getMessage());
    // } catch (final IOException e) {
    // App.LOGGER.fine(e.getMessage());
    // } catch (final ClassNotFoundException e) {
    // App.LOGGER.fine(e.getMessage());
    // }
    // }

    private void discoverTmp() {
        try {
            final FileInputStream fis = new FileInputStream("/tmp/wmsx-"
                    + System.getProperty("user.name"));
            final ObjectInputStream in = new ObjectInputStream(fis);

            final Wmsx wmsx = (Wmsx) in.readObject();
            in.close();
            this.haveProxy(wmsx);
        } catch (final IOException io) {
            App.LOGGER.warning("IOException: " + io.getMessage());
        } catch (final ClassNotFoundException e) {
            App.LOGGER.warning("ClassNotFound: " + e.getMessage());
        }
    }

    public void discovered(final DiscoveryEvent evt) {
        final ServiceRegistrar[] registrars = evt.getRegistrars();

        this.addAllRegistrars(registrars);
    }

    private void addAllRegistrars(final ServiceRegistrar[] registrars) {
        for (int n = 0; n < registrars.length; n++) {
            // System.out.println("Lookup service found");
            final ServiceRegistrar registrar = registrars[n];
            this.haveReg(registrar);
        }
    }

    private void haveReg(final ServiceRegistrar registrar) {
        Wmsx myService = null;
        final Class[] classes = new Class[] { Wmsx.class };
        final ServiceTemplate template = new ServiceTemplate(null, classes,
                new Entry[] { new WmsxEntry(System.getProperty("user.name")) });
        try {
            myService = (Wmsx) registrar.lookup(template);
        } catch (final java.rmi.RemoteException e) {
            e.printStackTrace();
            return;
        }
        this.haveProxy(myService);
    }

    private synchronized void haveProxy(final Wmsx myService) {
        if (myService == null) {
            App.LOGGER.fine("Classifier null");
            return;
        }
        synchronized (App.foundLock) {
            App.found = true;
            App.foundLock.notifyAll();
        }

        // App.LOGGER.info(myService.hello());
        try {
            switch (this.command) {
            case CMD_SHUTDOWN:
                try {
                    final Administrable mys = (Administrable) myService;
                    final Object adm = mys.getAdmin();
                    final DestroyAdmin dadm = (DestroyAdmin) adm;
                    dadm.destroy();
                } catch (final ClassCastException cc) {
                    App.LOGGER.info("ClassCast Exception: " + cc.getMessage());
                } catch (final NullPointerException npe) {
                    App.LOGGER
                            .info("NullPointerException: " + npe.getMessage());
                }
                break;
            case CMD_PING:
                myService.ping(false);
                break;
            case CMD_FULLPING:
                myService.ping(true);
                break;
            case CMD_NUMBER:
                myService.setMaxJobs(Integer.parseInt(this.cmdarg));
                break;
            case CMD_LASZLO:
                myService.submitLaszlo(this.cmdarg);
                break;
            case CMD_JDL:
                final String s = myService.submitJdl(this.cmdarg, this.output);
                System.out.println("" + s);
                break;
            }
        } catch (final IOException e) {
            App.LOGGER.warning(e.getMessage() + " " + e.getStackTrace());
        }
        System.exit(0);
    }

    public void discarded(final DiscoveryEvent arg0) {
        // do nothing
    }

}
