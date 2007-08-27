package hu.kfki.grid.wmsx.provider;

import hu.kfki.grid.wmsx.Wmsx;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import net.jini.admin.Administrable;

/**
 * A smart proxy which wraps the remote Jini service calls.
 */
public class WmsxProviderProxy implements Serializable, Wmsx, Administrable {

    private static final long serialVersionUID = 2L;

    private IRemoteWmsxProvider remoteService = null;

    private static final Logger LOGGER = Logger
            .getLogger(WmsxProviderProxy.class.toString());

    public WmsxProviderProxy(final Remote remote) {
        this.remoteService = (IRemoteWmsxProvider) remote;
    }

    public String submitJdl(final String jdlFile, final String output,
            final String resultDir) throws IOException {
        try {
            final File f = new File(jdlFile);
            if (!f.exists()) {
                throw new FileNotFoundException("File not Found: " + jdlFile);
            }
            final String outputPath;
            if (output != null) {
                final File f2 = new File(output);
                if (f2.exists()) {
                    throw new IOException("File exists: " + output);
                }
                outputPath = f2.getCanonicalPath();
            } else {
                outputPath = null;
            }
            final String resultPath;
            if (resultDir != null) {
                resultPath = new File(resultDir).getCanonicalPath();
            } else {
                resultPath = null;
            }

            return this.remoteService.submitJdl(f.getCanonicalPath(),
                    outputPath, resultPath);
        } catch (final RemoteException re) {
            WmsxProviderProxy.LOGGER.warning(re.getMessage());
            return null;
        }
    }

    public Object getAdmin() throws RemoteException {
        return this.remoteService;
    }

    public void setMaxJobs(final int maxJobs) {
        try {
            this.remoteService.setMaxJobs(maxJobs);
        } catch (final RemoteException re) {
            WmsxProviderProxy.LOGGER.warning(re.getMessage());
        }
    }

    public boolean ping(final boolean remote) {
        if (remote) {
            try {
                this.remoteService.ping();
            } catch (final RemoteException re) {
                WmsxProviderProxy.LOGGER.fine(re.getMessage());
                return false;
            }
        }
        return true;
    }

    public void submitLaszlo(final String argFile, final boolean interactive)
            throws IOException {
        final File f = new File(argFile).getCanonicalFile();
        if (!f.exists()) {
            throw new FileNotFoundException("File not Found: " + argFile);
        }
        final List commands = new Vector();
        final FileReader freader = new FileReader(f);
        final BufferedReader reader = new BufferedReader(freader);
        String line = reader.readLine();
        while (line != null) {
            line = line.trim();
            if (line.length() > 0) {
                if (line.charAt(0) != '#') {
                    final int spacePos = line.indexOf(" ");
                    final String command = line.substring(0, spacePos);
                    final File cmdWithPath = new File(f.getParentFile(),
                            command);
                    final String args = line.substring(spacePos + 1).trim();
                    commands.add(new IRemoteWmsxProvider.LaszloCommand(
                            cmdWithPath.getAbsolutePath(), args));
                }
            }
            line = reader.readLine();
        }
        if (commands.isEmpty()) {
            WmsxProviderProxy.LOGGER.warning("List of commands is empty!");
        } else {
            this.remoteService.submitLaszlo(commands, interactive, null);
        }
    }

    public void forgetAfs() {
        try {
            this.remoteService.forgetAfs();
        } catch (final RemoteException re) {
            WmsxProviderProxy.LOGGER.warning(re.getMessage());
        }
    }

    public boolean rememberAfs(final String password) {
        try {
            return this.remoteService.rememberAfs(password);
        } catch (final RemoteException re) {
            WmsxProviderProxy.LOGGER.warning(re.getMessage());
            return false;
        }
    }

    public boolean rememberGrid(final String password) {
        try {
            return this.remoteService.rememberGrid(password);
        } catch (final RemoteException re) {
            WmsxProviderProxy.LOGGER.warning(re.getMessage());
            return false;
        }
    }

}
