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

        private final String inputFile;

        public LaszloCommand(final String _command, final String _args,
                final String _inputFile) {
            this.command = _command;
            this.args = _args;
            this.inputFile = _inputFile;
        }

        public String getArgs() {
            return args;
        }

        public String getCommand() {
            return command;
        }

        public String getInputFile() {
            return inputFile;
        }

    }

    void ping() throws RemoteException;

    String submitJdl(String jdlFile, String output, String resultDir)
            throws RemoteException;

    void submitLaszlo(List commands) throws RemoteException;

    void setMaxJobs(int maxJobs) throws RemoteException;
}
