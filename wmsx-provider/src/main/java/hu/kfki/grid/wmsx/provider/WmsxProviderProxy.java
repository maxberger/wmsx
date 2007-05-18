package hu.kfki.grid.wmsx.provider;

import hu.kfki.grid.wmsx.Wmsx;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A smart proxy which wraps the remote Jini service calls.
 */
public class WmsxProviderProxy implements Serializable, Wmsx {

	private static final long serialVersionUID = 2L;

	private IRemoteWmsxProvider remoteService = null;

	private static final Log LOGGER = LogFactory
			.getLog(WmsxProviderProxy.class);

	public WmsxProviderProxy(final Remote remote) {
		this.remoteService = (IRemoteWmsxProvider) remote;
	}

	public String hello() {
		try {
			return this.remoteService.hello();
		} catch (final RemoteException re) {
			WmsxProviderProxy.LOGGER.debug(re);
			return "No answer";
		}
	}

	public void submitJdl(final String jdlFile) throws FileNotFoundException {
		try {
			final File f = new File(jdlFile);
			if (!f.exists()) {
				throw new FileNotFoundException(jdlFile);
			}
			this.remoteService.submitJdl(f.getAbsolutePath());
		} catch (final RemoteException re) {
			WmsxProviderProxy.LOGGER.warn(re);
		}
	}

}
