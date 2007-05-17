package hu.kfki.grid.wmsx.provider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * My Jini Service Implementation!
 *
 */
public class WmsxProviderImpl implements IRemoteWmsxProvider {
    
    private static final long serialVersionUID = 2L;
        private static final Log LOGGER =LogFactory.getLog(WmsxProviderImpl.class);

    public WmsxProviderImpl() {
        // default constructor
    }
    public String hello() {
        return "Hello, World!";
    }
    
    public void submitJdl(String jdlFile) {
    LOGGER.info("Submitting "+jdlFile);
    }
    
}