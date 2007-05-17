package hu.kfki.grid.wmsx.provider;

import hu.kfki.grid.wmsx.Wmsx;
import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * A smart proxy which wraps the remote Jini service calls.
 */
public class WmsxProviderProxy implements Serializable, Wmsx {
    
    private static final long serialVersionUID = 2L;
    
    private IRemoteWmsxProvider remoteService = null;
    
    public WmsxProviderProxy(Remote remote) {
        this.remoteService = (IRemoteWmsxProvider) remote;
    }
    
    public String hello() {
        try {
            return remoteService.hello();
        } catch (RemoteException re) {
            return "No answer";
        }
    }
    
    public void submitJdl(String jdlFile) {
        try {
            remoteService.submitJdl(jdlFile);
        } catch (RemoteException re) {
            System.out.println("Bad, very bad!");
        }
    }
    
}
