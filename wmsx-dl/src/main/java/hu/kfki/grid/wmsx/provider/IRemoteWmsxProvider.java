package hu.kfki.grid.wmsx.provider;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Remote version of Jini service.
 */
public interface IRemoteWmsxProvider extends Serializable, Remote {

	void ping() throws RemoteException;

	String submitJdl(String jdlFile, String output) throws RemoteException;

        void submitLaszlo(List commands) throws RemoteException;
        
	void setMaxJobs(int maxJobs) throws RemoteException;
}
