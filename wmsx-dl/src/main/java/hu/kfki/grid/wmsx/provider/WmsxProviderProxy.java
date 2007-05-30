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
    
    public String submitJdl(final String jdlFile, final String output)
            throws IOException {
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
                outputPath = f2.getAbsolutePath();
            } else {
                outputPath = null;
            }
            return this.remoteService
                    .submitJdl(f.getAbsolutePath(), outputPath);
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
    
    public void submitLaszlo(final String argFile) throws IOException {
        final File f = new File(argFile);
        if (!f.exists()) {
            throw new FileNotFoundException("File not Found: " + argFile);
        }
        final FileReader freader = new FileReader(f);
        final BufferedReader reader = new BufferedReader(freader);
        String line = reader.readLine();
        while (line!=null) {
            line = line.trim();
            if (line.length()>0){
                if (line.charAt(0)!='#') {
                    int spacePos = line.indexOf(" ");
                    final String command = line.substring(0, spacePos);
                    final String args = line.substring(spacePos+1).trim();
                    
                    LOGGER.info("C: "+command+", A:"+args);
                }
            }
            line = reader.readLine();
        }
    }
    
    
    
}
