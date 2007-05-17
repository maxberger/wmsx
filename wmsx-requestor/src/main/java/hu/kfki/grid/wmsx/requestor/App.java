package hu.kfki.grid.wmsx.requestor;

import hu.kfki.grid.wmsx.Wmsx;
import java.rmi.RMISecurityManager;
import net.jini.discovery.LookupDiscovery;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.DiscoveryEvent;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceTemplate;

/**
 * Hello world!
 *
 */
public class App implements DiscoveryListener {
    public static void main( String[] args ) {
        new App();
        
        // stay around long enough to receive replies
        try {
            Thread.currentThread().sleep(100000L);
        } catch(java.lang.InterruptedException e) {
            // do nothing
        }
    }
    
    public App() {
        System.setSecurityManager(new RMISecurityManager());
        
        LookupDiscovery discover = null;
        try {
            discover = new LookupDiscovery(LookupDiscovery.ALL_GROUPS);
        } catch(Exception e) {
            System.err.println(e.toString());
            System.exit(1);
        }
        
        discover.addDiscoveryListener(this);
        
    }
    
    
    public void discovered(DiscoveryEvent evt) {
        ServiceRegistrar[] registrars = evt.getRegistrars();
        Class [] classes = new Class[] {Wmsx.class};
        Wmsx myService = null;
        ServiceTemplate template = new ServiceTemplate(null, classes,
                null);
        
        for (int n = 0; n < registrars.length; n++) {
            System.out.println("Lookup service found");
            ServiceRegistrar registrar = registrars[n];
            try {
                myService = (Wmsx) registrar.lookup(template);
            } catch(java.rmi.RemoteException e) {
                e.printStackTrace();
                continue;
            }
            if (myService == null) {
                System.out.println("Classifier null");
                continue;
            }
            System.out.println(myService.hello());
            System.exit(0);
        }
    }
    public void discarded(DiscoveryEvent arg0) {
        // do nothing
    }
}
