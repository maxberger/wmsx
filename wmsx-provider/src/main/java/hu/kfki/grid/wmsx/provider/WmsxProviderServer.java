package hu.kfki.grid.wmsx.provider;

import hu.kfki.grid.wmsx.WmsxEntry;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.rmi.RMISecurityManager;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import net.jini.config.Configuration;
import net.jini.config.ConfigurationException;
import net.jini.config.ConfigurationProvider;
import net.jini.core.discovery.LookupLocator;
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
import net.jini.lease.LeaseListener;
import net.jini.lease.LeaseRenewalEvent;
import net.jini.lease.LeaseRenewalManager;

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

	static final Object keepAlive = new Object();

	private LookupDiscovery discover = null;

	private Exporter exporter = null;

	public static void main(final String argv[]) {
		try {
			new WmsxProviderServer(ConfigurationProvider.getInstance(argv));
		} catch (final ConfigurationException e) {
			WmsxProviderServer.LOGGER.severe(e.getMessage());
			System.exit(1);
		}

		// keep server running forever to
		// - allow time for locator discovery and
		// - keep re-registering the lease
		synchronized (WmsxProviderServer.keepAlive) {
			try {
				WmsxProviderServer.keepAlive.wait();
			} catch (final java.lang.InterruptedException e) {
				// do nothing
			}
		}
		WmsxProviderServer.LOGGER.info("Terminated.");
	}

	public WmsxProviderServer(final Configuration config) {
		// Create the service
		this.impl = new WmsxProviderImpl(this);

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
			// and use this to construct an exporter
			this.exporter = (Exporter) config.getEntry("JiniServiceServer",
					"exporter", Exporter.class);
			// export an object of this class
			this.rmiProxy = this.exporter.export(this.impl);
		} catch (final Exception e) {
			WmsxProviderServer.LOGGER.severe(e.getMessage());
			System.exit(1);
		}

		System.setSecurityManager(new RMISecurityManager());

		// proxy primed with impl
		this.smartProxy = new WmsxProviderProxy(this.rmiProxy);

		try {
			final FileOutputStream fos = new FileOutputStream("/tmp/wmsx-"
					+ System.getProperty("user.name"));
			final ObjectOutputStream out = new ObjectOutputStream(fos);
			out.writeObject(this.smartProxy);
			out.close();
		} catch (final IOException io) {
			WmsxProviderServer.LOGGER.warning(io.getMessage());
		}

		try {
			final LookupLocator localLocator = new LookupLocator(
					"jini://127.0.0.1/");
			final ServiceRegistrar reg = localLocator.getRegistrar();
			this.register(reg);
		} catch (final MalformedURLException e1) {
			WmsxProviderServer.LOGGER.warning(e1.getMessage());
		} catch (final IOException e) {
			WmsxProviderServer.LOGGER.fine(e.getMessage());
		} catch (final ClassNotFoundException e) {
			WmsxProviderServer.LOGGER.warning(e.getMessage());
		}

		try {
			this.discover = new LookupDiscovery(LookupDiscovery.ALL_GROUPS);
		} catch (final Exception e) {
			WmsxProviderServer.LOGGER.severe("Discovery failed: "
					+ e.getMessage());
			System.exit(1);
		}

		this.discover.addDiscoveryListener(this);
	}

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
		synchronized (WmsxProviderServer.keepAlive) {
			WmsxProviderServer.keepAlive.notifyAll();
		}
	}

} // JiniServiceServer
