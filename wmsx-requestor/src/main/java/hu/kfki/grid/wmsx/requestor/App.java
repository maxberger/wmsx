package hu.kfki.grid.wmsx.requestor;

import hu.kfki.grid.wmsx.Wmsx;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RMISecurityManager;

import net.jini.core.discovery.LookupLocator;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.discovery.DiscoveryEvent;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.LookupDiscovery;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Hello world!
 * 
 */
public class App implements DiscoveryListener {

	private static final Log LOGGER = LogFactory.getLog(App.class);

	public static void main(final String[] args) {
		new App();

		// stay around long enough to receive replies
		try {
			Thread.sleep(100000L);
		} catch (final java.lang.InterruptedException e) {
			// do nothing
		}
	}

	public App() {
		System.setSecurityManager(new RMISecurityManager());

		try {
			final LookupLocator localLocator = new LookupLocator(
					"jini://127.0.0.1/");
			final ServiceRegistrar reg = localLocator.getRegistrar();
			this.haveReg(reg);
		} catch (final MalformedURLException e1) {
			App.LOGGER.warn(e1);
		} catch (final IOException e) {
			App.LOGGER.debug(e);
		} catch (final ClassNotFoundException e) {
			App.LOGGER.warn(e);
		}

		LookupDiscovery discover = null;
		try {
			discover = new LookupDiscovery(LookupDiscovery.ALL_GROUPS);
		} catch (final Exception e) {
			App.LOGGER.fatal(e);
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
				null);
		try {
			myService = (Wmsx) registrar.lookup(template);
		} catch (final java.rmi.RemoteException e) {
			e.printStackTrace();
			return;
		}
		if (myService == null) {
			App.LOGGER.debug("Classifier null");
			return;
		}
		App.LOGGER.info(myService.hello());
		try {
			myService.submitJdl("testjob.jdl");
		} catch (final FileNotFoundException e) {
			App.LOGGER.warn(e);
		}
		System.exit(0);
	}

	public void discarded(final DiscoveryEvent arg0) {
		// do nothing
	}
}
