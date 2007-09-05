package hu.kfki.grid.wmsx.requestor;

import hu.kfki.grid.wmsx.Wmsx;
import hu.kfki.grid.wmsx.WmsxEntry;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
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
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.sun.jini.admin.DestroyAdmin;

/**
 * Hello world!
 * 
 */
public class App implements DiscoveryListener {

    private static final String VO = "vo";

    private static final String FORGET_AFS = "forgetafs";

    private static final String REMEMBER_AFS = "rememberafs";

    private static final String REMEMBER_GRID = "remembergrid";

    private static final String NAME = "name";

    private static final Logger LOGGER = Logger.getLogger(App.class.toString());

    private static final int CMD_SHUTDOWN = 0;

    private static final int CMD_NUMBER = 1;

    private static final int CMD_JDL = 2;

    private static final int CMD_PING = 3;

    private static final int CMD_FULLPING = 4;

    private static final int CMD_LASZLO = 5;

    private static final int CMD_REMEMBERAFS = 6;

    private static final int CMD_FORGETAFS = 7;

    private static final int CMD_REMEMBERGRID = 8;

    private static final int CMD_VO = 9;

    private final List commands;

    private final CommandLine commandLine;

    private static LookupDiscovery discover = null;

    private static boolean found = false;

    private static final Object foundLock = new Object();

    private static final String OPTION_INTERACTIVE = "interactive";

    public static void main(final String[] args) {

        final Options options = new Options();

        final OptionGroup commands = new OptionGroup();
        options.addOption(new Option("h", "help", false, "print this message"));
        commands.addOption(new Option("k", "kill", false,
                "shutdown the service provider"));
        options.addOption(new Option("p", "ping", false,
                "quick check if provider is running"));
        options.addOption(new Option("f", "full-ping", false,
                "full check if provider is running"));
        commands.addOption(new Option("j", "jdl", true, "submit a JDL file"));
        commands.addOption(new Option("a", "args", true,
                "submit a Laszlo-style args file"));
        options.addOption(new Option("n", "number", true,
                "set number of active jobs"));
        options.addOptionGroup(commands);
        options.addOption(new Option("o", "output", true,
                "redirect interactive output to file"));
        options.addOption(new Option("r", "resultDir", true,
                "retrieve and store results to dir"));
        options.addOption(new Option("i", App.OPTION_INTERACTIVE, false,
                "Run as interactive"));

        options.addOption(new Option(App.REMEMBER_AFS,
                "Asks for AFS password and remember it until told to forget"));
        options.addOption(new Option(App.REMEMBER_GRID,
                "Asks for Grid password and remember it "
                        + "until the number of managed jobs reaches 0."));
        options.addOption(new Option(App.FORGET_AFS, "Forgets AFS password"));
        options
                .addOption(new Option(App.NAME, true, "Name for this execution"));
        options.addOption(new Option(App.VO, true, "VO for job submissions"));

        final CommandLineParser parser = new GnuParser();
        try {
            final CommandLine cmd = parser.parse(options, args);
            final List cmds = new Vector();
            if (cmd.hasOption('n')) {
                cmds.add(new Integer(App.CMD_NUMBER));
            }
            if (cmd.hasOption('h')) {
                App.printHelp(options);
            }
            if (cmd.hasOption('p')) {
                cmds.add(new Integer(App.CMD_PING));
            }
            if (cmd.hasOption('f')) {
                cmds.add(new Integer(App.CMD_FULLPING));
            }
            if (cmd.hasOption(App.VO)) {
                cmds.add(new Integer(App.CMD_VO));
            }
            if (cmd.hasOption(App.REMEMBER_AFS)) {
                cmds.add(new Integer(App.CMD_REMEMBERAFS));
            }
            if (cmd.hasOption(App.FORGET_AFS)) {
                cmds.add(new Integer(App.CMD_FORGETAFS));
            }
            if (cmd.hasOption(App.REMEMBER_GRID)) {
                cmds.add(new Integer(App.CMD_REMEMBERGRID));
            }
            if (cmd.hasOption('k')) {
                cmds.add(new Integer(App.CMD_SHUTDOWN));
            } else if (cmd.hasOption('a')) {
                cmds.add(new Integer(App.CMD_LASZLO));
            } else if (cmd.hasOption('j')) {
                cmds.add(new Integer(App.CMD_JDL));
            }
            if (cmd.getOptions().length < 1) {
                App.printHelp(options);
            }
            if (!cmds.isEmpty()) {
                App.dispatch(cmds, cmd);
            }
        } catch (final ParseException e1) {
            System.out.println("Invalid command line:" + e1.getMessage());
            App.printHelp(options);
            System.exit(2);
        }
    }

    private static void printHelp(final Options options) {
        new HelpFormatter().printHelp("wmsx-requestor [arguments]", options);
    }

    private static void dispatch(final List cmd, final CommandLine cmdLine) {
        new App(cmd, cmdLine);

        // stay around long enough to receive replies
        // try {
        // Thread.sleep(100000L);
        // } catch (final java.lang.InterruptedException e) {
        // // do nothing
        // }
        // App.LOGGER
        // .info("Failed to connect to provider. Please check if its running.");

        // synchronized (App.foundLock) {
        // try {
        // App.foundLock.wait(30000);
        // } catch (final InterruptedException e) {
        // // ignore
        // }
        // }

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

    public App(final List cmds, final CommandLine cmdLine) {
        this.commands = cmds;
        this.commandLine = cmdLine;
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

        final Iterator cmdIt = this.commands.iterator();

        while (cmdIt.hasNext()) {

            final Integer com = (Integer) cmdIt.next();
            final int command = com.intValue();
            // App.LOGGER.info(myService.hello());
            try {
                switch (command) {
                case CMD_SHUTDOWN:
                    try {
                        final Administrable mys = (Administrable) myService;
                        final Object adm = mys.getAdmin();
                        final DestroyAdmin dadm = (DestroyAdmin) adm;
                        dadm.destroy();
                    } catch (final ClassCastException cc) {
                        App.LOGGER.info("ClassCast Exception: "
                                + cc.getMessage());
                    } catch (final NullPointerException npe) {
                        App.LOGGER.info("NullPointerException: "
                                + npe.getMessage());
                    }
                    break;
                case CMD_PING:
                    myService.ping(false);
                    break;
                case CMD_FULLPING:
                    myService.ping(true);
                    break;
                case CMD_NUMBER:
                    myService.setMaxJobs(Integer.parseInt(this.commandLine
                            .getOptionValue('n')));
                    break;
                case CMD_LASZLO:
                    myService.submitLaszlo(
                            this.commandLine.getOptionValue('a'),
                            this.commandLine.hasOption('i'), this.commandLine
                                    .getOptionValue(App.NAME));
                    break;
                case CMD_JDL:
                    final String s = myService.submitJdl(this.commandLine
                            .getOptionValue('j'), this.commandLine
                            .getOptionValue('o'), this.commandLine
                            .getOptionValue('r'));
                    System.out.println("" + s);
                    break;
                case CMD_FORGETAFS:
                    myService.forgetAfs();
                    break;
                case CMD_REMEMBERAFS:
                    final String password = this.askPassword("AFS Password: ");
                    if (!myService.rememberAfs(password)) {
                        App.LOGGER.warning("Error remembering AFS password");
                    }
                    break;
                case CMD_REMEMBERGRID:
                    final String gpassword = this
                            .askPassword("Grid Password: ");
                    if (!myService.rememberGrid(gpassword)) {
                        App.LOGGER.warning("Error remembering Grid password");
                    }
                    break;
                case CMD_VO:
                    myService.setVo(this.commandLine.getOptionValue(App.VO));
                    break;
                default:
                    App.LOGGER.warning("Invalid Command encountered: "
                            + command);
                }
            } catch (final IOException e) {
                App.LOGGER.warning(e.getMessage() + " " + e.getStackTrace());
            }
        }
        System.exit(0);
    }

    private String askPassword(final String prompt) {
        final BufferedReader in = new BufferedReader(new InputStreamReader(
                System.in));
        System.out.print(prompt);
        String p;
        try {
            p = in.readLine();
        } catch (final IOException e) {
            p = "";
        }
        System.out.println();
        return p;
    }

    public void discarded(final DiscoveryEvent arg0) {
        // do nothing
    }

}
