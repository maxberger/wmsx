package hu.kfki.grid.wmsx.worker;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Controller extends Remote {
    String sayHello() throws RemoteException;
}
