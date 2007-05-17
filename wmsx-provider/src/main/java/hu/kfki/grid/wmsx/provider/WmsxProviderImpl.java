package hu.kfki.grid.wmsx.provider;

/**
 * My Jini Service Implementation!
 *
 */
public class WmsxProviderImpl implements IRemoteWmsxProvider {
    
    private static final long serialVersionUID = 2L;
    
    public WmsxProviderImpl() {
        // default constructor
    }
    public String hello() {
        return "Hello, World!";
    }
    
    public void submitJdl(String jdlFile) {
        System.out.println("Submitting "+jdlFile);
    }
    
}