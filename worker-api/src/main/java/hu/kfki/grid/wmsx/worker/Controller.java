package hu.kfki.grid.wmsx.worker;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Controller extends Remote {
    WorkDescription retrieveWork() throws RemoteException;

    public void doneWith(String id, ResultDescription result)
            throws RemoteException;
}
