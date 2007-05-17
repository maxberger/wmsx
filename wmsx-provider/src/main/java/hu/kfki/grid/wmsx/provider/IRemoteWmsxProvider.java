package hu.kfki.grid.wmsx.provider;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Remote version of Jini service.
 */
public interface IRemoteWmsxProvider extends Serializable, Remote {
    
    String hello() throws RemoteException;
    
    void submitJdl(String jdlFile) throws RemoteException;
    
}
