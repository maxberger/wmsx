package hu.kfki.grid.wmsx.provider;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.rmi.RMISecurityManager;
import java.rmi.Remote;

import net.jini.config.Configuration;
import net.jini.config.ConfigurationException;
import net.jini.config.ConfigurationProvider;
import net.jini.core.entry.Entry;
import net.jini.core.lease.Lease;
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.jini.lookup.entry.BasicServiceType;

public class WmsxProviderServer implements DiscoveryListener, LeaseListener {
	private static final Log LOGGER = LogFactory
			.getLog(WmsxProviderServer.class);

	protected LeaseRenewalManager leaseManager = new LeaseRenewalManager();

	protected ServiceID serviceID = null;

	protected WmsxProviderProxy smartProxy = null;

	protected Remote rmiProxy = null;

	protected WmsxProviderImpl impl = null;

	public static void main(String argv[]) {
		try {
			new WmsxProviderServer(ConfigurationProvider.getInstance(argv));
		} catch (ConfigurationException e) {
			LOGGER.fatal(e);
			System.exit(1);
		}

		// keep server running forever to
		// - allow time for locator discovery and
		// - keep re-registering the lease
		Object keepAlive = new Object();
		synchronized (keepAlive) {
			try {
				keepAlive.wait();
			} catch (java.lang.InterruptedException e) {
				// do nothing
			}
		}
	}

	public WmsxProviderServer(Configuration config) {
		// Create the service
		impl = new WmsxProviderImpl();

		// Try to load the service ID from file.
		// It isn't an error if we can't load it, because
		// maybe this is the first time this service has run
		DataInputStream din = null;

		try {
			din = new DataInputStream(new FileInputStream(this.getClass()
					.getName()
					+ ".id"));
			serviceID = new ServiceID(din);
		} catch (Exception e) {
			// ignore
		}

		try {
			// and use this to construct an exporter
			Exporter exporter = (Exporter) config.getEntry("JiniServiceServer",
					"exporter", Exporter.class);
			// export an object of this class
			this.rmiProxy = (IRemoteWmsxProvider) exporter.export(impl);
		} catch (Exception e) {
			LOGGER.fatal(e);
			System.exit(1);
		}

		System.setSecurityManager(new RMISecurityManager());

		// proxy primed with impl
		smartProxy = new WmsxProviderProxy(rmiProxy);

		LookupDiscovery discover = null;

		try {
			discover = new LookupDiscovery(LookupDiscovery.ALL_GROUPS);
		} catch (Exception e) {
			LOGGER.fatal("Discovery failed", e);
			System.exit(1);
		}

		discover.addDiscoveryListener(this);
	}

	public Entry[] getTypes() {
		return new Entry[] { new BasicServiceType("AService") };
	}

	public void discovered(DiscoveryEvent evt) {
		ServiceRegistrar[] registrars = evt.getRegistrars();

		for (int n = 0; n < registrars.length; n++) {
			ServiceRegistrar registrar = registrars[n];

			ServiceItem item = new ServiceItem(serviceID, smartProxy,
					getTypes());
			ServiceRegistration reg = null;

			try {
				reg = registrar.register(item, Lease.FOREVER);
			} catch (java.rmi.RemoteException e) {
				LOGGER.warn("Register exception: ", e);
				continue;
			}

			LOGGER.info("Service registered with id " + reg.getServiceID());

			// set lease renewal in place
			leaseManager.renewUntil(reg.getLease(), Lease.FOREVER, this);

			// set the serviceID if necessary
			if (serviceID == null) {
				serviceID = reg.getServiceID();

				// try to save the service ID in a file
				DataOutputStream dout = null;
				try {
					dout = new DataOutputStream(new FileOutputStream(this
							.getClass().getName()
							+ ".id"));
					serviceID.writeBytes(dout);
					dout.flush();
				} catch (Exception e) {
					// ignore
				}
			}
		}
	}

	public void notify(LeaseRenewalEvent evt) {
		LOGGER.debug("Lease expired " + evt.toString());
	}

	public void discarded(DiscoveryEvent arg0) {
		// do nothing
	}

} // JiniServiceServer
