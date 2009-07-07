/*
 * WMSX - Workload Management Extensions for gLite
 * 
 * Copyright (C) 2007-2009 Max Berger
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

package hu.kfki.grid.wmsx.requestor;

import hu.kfki.grid.wmsx.SubmissionResult;
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
 * @version $Date$
 */
public class App implements DiscoveryListener {

    private static final String VO = "vo";

    private static final String BACKEND = "backend";

    private static final String FORGET_AFS = "forgetafs";

    private static final String REMEMBER_AFS = "rememberafs";

    private static final String REMEMBER_GRID = "remembergrid";

    private static final String SHUTDOWN_WORKERS = "shutdownworkers";

    private static final String LISTBACKENDS = "listbackends";

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

    private static final int CMD_BACKEND = 10;

    private static final int CMD_WORKERS = 11;

    private static final int CMD_SHUTDOWNWORKERS = 12;

    private static final int CMD_LISTBACK = 13;

    private static final String OPTION_INTERACTIVE = "interactive";

    private static final Object FOUNDLOCK = new Object();

    private static LookupDiscovery discover;

    private static boolean found;

    private final List<Integer> commands;

    private final CommandLine commandLine;

    /**
     * Entry point for the command line app.
     * 
     * @param args
     *            Commandline arguments.
     */
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
        options.addOption(new Option("w", "workers", true,
                "submit number of workers"));
        options.addOption(new Option(App.SHUTDOWN_WORKERS,
                "shutdown all workers"));
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
        options.addOption(new Option(App.BACKEND, true,
                "Backend for job submissions, use listbackends to get a list"));
        options.addOption(new Option(App.LISTBACKENDS, false,
                "lists possible backends"));

        final CommandLineParser parser = new GnuParser();
        try {
            final CommandLine cmd = parser.parse(options, args);
            final List<Integer> cmds = new Vector<Integer>();
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
            if (cmd.hasOption(App.LISTBACKENDS)) {
                cmds.add(new Integer(App.CMD_LISTBACK));
            }
            if (cmd.hasOption(App.BACKEND)) {
                cmds.add(new Integer(App.CMD_BACKEND));
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
            if (cmd.hasOption(App.SHUTDOWN_WORKERS)) {
                cmds.add(new Integer(App.CMD_SHUTDOWNWORKERS));
            }
            if (cmd.hasOption('w')) {
                cmds.add(new Integer(App.CMD_WORKERS));
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

    private static void dispatch(final List<Integer> cmd,
            final CommandLine cmdLine) {
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

    /**
     * Default Constructor.
     * 
     * @param cmds
     *            Commmands
     * @param cmdLine
     *            the actual commandline, if needed.
     */
    public App(final List<Integer> cmds, final CommandLine cmdLine) {
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

    /** {@inheritDoc} */
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
        final Class<?>[] classes = new Class<?>[] { Wmsx.class };
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
        synchronized (App.FOUNDLOCK) {
            App.found = true;
            App.FOUNDLOCK.notifyAll();
        }

        final Iterator<Integer> cmdIt = this.commands.iterator();

        while (cmdIt.hasNext()) {

            final Integer com = cmdIt.next();
            final int command = com.intValue();
            // App.LOGGER.info(myService.hello());
            try {
                switch (command) {
                case App.CMD_SHUTDOWN:
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
                case App.CMD_PING:
                    myService.ping(false);
                    break;
                case App.CMD_FULLPING:
                    myService.ping(true);
                    break;
                case App.CMD_NUMBER:
                    myService.setMaxJobs(Integer.parseInt(this.commandLine
                            .getOptionValue('n')));
                    break;
                case App.CMD_WORKERS:
                    myService.startWorkers(Integer.parseInt(this.commandLine
                            .getOptionValue('w')));
                    break;
                case App.CMD_SHUTDOWNWORKERS:
                    myService.shutdownWorkers();
                    break;
                case App.CMD_LASZLO:
                    myService.submitLaszlo(
                            this.commandLine.getOptionValue('a'),
                            this.commandLine.hasOption('i'), this.commandLine
                                    .getOptionValue(App.NAME));
                    break;
                case App.CMD_JDL:
                    final SubmissionResult s = myService.submitJdl(
                            this.commandLine.getOptionValue('j'),
                            this.commandLine.getOptionValue('o'),
                            this.commandLine.getOptionValue('r'));
                    System.out.println("" + s);
                    break;
                case App.CMD_FORGETAFS:
                    myService.forgetAfs();
                    break;
                case App.CMD_REMEMBERAFS:
                    final String password = this.askPassword("AFS Password: ");
                    if (!myService.rememberAfs(password)) {
                        App.LOGGER.warning("Error remembering AFS password");
                    }
                    break;
                case App.CMD_REMEMBERGRID:
                    final String gpassword = this
                            .askPassword("Grid Password: ");
                    if (!myService.rememberGrid(gpassword)) {
                        App.LOGGER.warning("Error remembering Grid password");
                    }
                    break;
                case App.CMD_VO:
                    myService.setVo(this.commandLine.getOptionValue(App.VO));
                    break;
                case App.CMD_BACKEND:
                    myService.setBackend(this.commandLine
                            .getOptionValue(App.BACKEND));
                    break;
                case App.CMD_LISTBACK:
                    final String bs = myService.listBackends().toString();
                    System.out.println(bs);
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

    /** {@inheritDoc} */
    public void discarded(final DiscoveryEvent arg0) {
        // do nothing
    }

}
