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
public final class Requestor {
    private static final long serialVersionUID = -8474571551490820022L;

    private Wmsx wmsxService;

    /* Singleton Pattern */
    private Requestor() {
        this.reConnect(true);
    }

    /**
     * Private innere statische Klasse, realisiert Singleton Pattern
     * 
     */
    private static class SingletonHolder {
        private static final Requestor INSTANCE = new Requestor();
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

    private void reConnect(final boolean infoMessage) {
        try {
            final FileInputStream fis = new FileInputStream("/tmp/wmsx-"
                    + System.getProperty("user.name"));
            final ObjectInputStream in = new ObjectInputStream(fis);

            this.wmsxService = (Wmsx) in.readObject();
            in.close();

        } catch (final IOException io) {
            System.out.println("IOException: " + io.getMessage());

            if (infoMessage) {
                final int result = JOptionPane
                        .showConfirmDialog(
                                           null,
                                           io.getMessage()
                                                   + "\nFailed to connect to provider. Please check if its running.\nWould you like to start in offline mode?",
                                           "WMSX GUI - IOException",
                                           JOptionPane.ERROR_MESSAGE);
                if (result != 0) {
                    System.exit(0);
                }
            }

        } catch (final ClassNotFoundException e) {
            System.out.println("ClassNotFound: " + e.getMessage());
            JOptionPane.showMessageDialog(null, e.getMessage(),
                                          "WMSX GUI - ClassNotFound",
                                          JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * getWmsxService is Getter which returns the WmsxService.
     * 
     * @param reconnect
     *            if true, try reconnect, otherwise give current service object
     *            back
     * @return WmsxService to the provider
     */
    public Wmsx getWmsxService(final boolean reconnect) {
        if (reconnect) {
            this.reConnect(false);
        }

        return this.wmsxService;
    }
}
