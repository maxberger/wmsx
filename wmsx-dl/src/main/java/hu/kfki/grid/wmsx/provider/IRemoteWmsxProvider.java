package hu.kfki.grid.wmsx.provider;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Remote version of Jini service.
 */
public interface IRemoteWmsxProvider extends Serializable, Remote {

    static class LaszloCommand implements Serializable {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        private final String command;

        private final String args;

        public LaszloCommand(final String _commandWithPath, final String _args) {
            this.command = _commandWithPath;
            this.args = _args;
        }

        public String getArgs() {
            return this.args;
        }

        public String getCommand() {
            return this.command;
        }

    }

    void ping() throws RemoteException;

    String submitJdl(String jdlFile, String output, String resultDir)
            throws RemoteException;

    void submitLaszlo(List commands, boolean interactive, String prefix,
            String name) throws RemoteException;

    void setMaxJobs(int maxJobs) throws RemoteException;

    boolean rememberAfs(String password) throws RemoteException;

    boolean rememberGrid(String password) throws RemoteException;

    void forgetAfs() throws RemoteException;

    void setVo(String newVo) throws RemoteException;

    void setBackend(String backend) throws RemoteException;

}
