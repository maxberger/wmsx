package hu.kfki.grid.wmsx.requestor;

import hu.kfki.grid.wmsx.Wmsx;
import hu.kfki.grid.wmsx.WmsxEntry;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RMISecurityManager;
import java.util.logging.Logger;

import net.jini.core.discovery.LookupLocator;
import net.jini.core.entry.Entry;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.discovery.DiscoveryEvent;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.LookupDiscovery;

/**
 * Hello world!
 * 
 */
public class App implements DiscoveryListener {

	private static final Logger LOGGER = Logger.getLogger(App.class.toString());

	final String jdl, output;

	public static void main(final String[] args) {
		final String output;
		if (args.length < 2) {
			output = null;
		} else {
			output = args[1];
		}
		new App(args[0], output);

		// stay around long enough to receive replies
		try {
			Thread.sleep(100000L);
		} catch (final java.lang.InterruptedException e) {
			// do nothing
		}
	}

	public App(final String jdlFile, final String outputFile) {
		this.jdl = jdlFile;
		this.output = outputFile;
		System.setSecurityManager(new RMISecurityManager());

		try {
			final LookupLocator localLocator = new LookupLocator(
					"jini://127.0.0.1/");
			final ServiceRegistrar reg = localLocator.getRegistrar();
			this.haveReg(reg);
		} catch (final MalformedURLException e1) {
			App.LOGGER.warning(e1.getMessage());
		} catch (final IOException e) {
			App.LOGGER.fine(e.getMessage());
		} catch (final ClassNotFoundException e) {
			App.LOGGER.fine(e.getMessage());
		}

		LookupDiscovery discover = null;
		try {
			discover = new LookupDiscovery(LookupDiscovery.ALL_GROUPS);
		} catch (final Exception e) {
			App.LOGGER.severe(e.getMessage());
			System.exit(1);
		}

		discover.addDiscoveryListener(this);

	}

	public void discovered(final DiscoveryEvent evt) {
		final ServiceRegistrar[] registrars = evt.getRegistrars();

		for (int n = 0; n < registrars.length; n++) {
			System.out.println("Lookup service found");
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
		if (myService == null) {
			App.LOGGER.fine("Classifier null");
			return;
		}
		// App.LOGGER.info(myService.hello());
		try {
			final String s = myService.submitJdl(this.jdl, this.output);
			System.out.println("" + s);
		} catch (final IOException e) {
			App.LOGGER.warning(e.getMessage());
		}
		System.exit(0);
	}

	public void discarded(final DiscoveryEvent arg0) {
		// do nothing
	}
}
