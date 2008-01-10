package hu.kfki.grid.wmsx.worker;

import java.io.FileInputStream;
import java.io.ObjectInputStream;

public class Worker {
    public static void main(final String args[]) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new AllSecurityManager());
        }
        try {
            final String proxyFile;
            if (args.length > 0) {
                proxyFile = args[0];
            } else {
                proxyFile = "proxyFile";
            }
            final FileInputStream fis = new FileInputStream(proxyFile);
            final ObjectInputStream in = new ObjectInputStream(fis);
            final Controller comp = (Controller) in.readObject();
            in.close();
            System.out.println(comp.sayHello());
        } catch (final Exception e) {
            System.err.println("Hello exception:");
            e.printStackTrace();
        }
    }
}
