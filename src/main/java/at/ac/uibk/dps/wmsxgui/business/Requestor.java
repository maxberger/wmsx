package at.ac.uibk.dps.wmsxgui.business;

import hu.kfki.grid.wmsx.Wmsx;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.swing.JOptionPane;

/**
 *
 * @author WmsxGUI Team
 * @version 1.0
 */
public class Requestor {
    private static final long serialVersionUID = -8474571551490820022L;

    private Wmsx wmsxService;

    /* Singleton Pattern */
    private Requestor() {
        try {
            final FileInputStream fis = new FileInputStream("/tmp/wmsx-"
                    + System.getProperty("user.name"));
            final ObjectInputStream in = new ObjectInputStream(fis);

            this.wmsxService = (Wmsx) in.readObject();
            in.close();

        } catch (final IOException io) {
            System.out.println("IOException: " + io.getMessage());

            final int result = JOptionPane
                    .showConfirmDialog(
                                       null,
                                       io.getMessage()
                                               + "\nFailed to connect to provider. Please check if its running.\nWould you like to start in offline Demo Mode?",
                                       "WMSX GUI - IOException",
                                       JOptionPane.ERROR_MESSAGE);
            if (result != 0) {
                System.exit(0);
            }

        } catch (final ClassNotFoundException e) {
            System.out.println("ClassNotFound: " + e.getMessage());
            JOptionPane.showMessageDialog(null, e.getMessage(),
                                          "WMSX GUI - ClassNotFound",
                                          JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Private innere statische Klasse, realisiert Singleton Pattern
     * 
     */
    private static class SingletonHolder {
        private final static Requestor INSTANCE = new Requestor();
    }

    /**
     * getInstance returns always the same instance of the requestor.
     * 
     * @return Instance der Game Klasse
     */
    public static Requestor getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /* Singleton */

    /**
     * getWmsxService is  Getter which returns the WmsxService.
     * @return WmsxService to the provider
     */
    public Wmsx getWmsxService() {
        return this.wmsxService;
    }
}
