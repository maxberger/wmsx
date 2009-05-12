package at.ac.uibk.dps.wmsxgui.business;

import hu.kfki.grid.wmsx.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.swing.JOptionPane;


/**
 *
 * @author bafu
 */
public class Requestor {

    private Wmsx wmsx_service;
    /* Singleton Pattern */
	private Requestor()
	{
        try {
            final FileInputStream fis = new FileInputStream("/tmp/wmsx-"+System.getProperty("user.name"));
            final ObjectInputStream in = new ObjectInputStream(fis);

            wmsx_service = (Wmsx) in.readObject();
            in.close();
            
        } catch (final IOException io) {
            System.out.println("IOException: " + io.getMessage());
            JOptionPane.showMessageDialog(null, io.getMessage()+"\nFailed to connect to provider. Please check if its running.", "WMSX GUI - IOException", JOptionPane.ERROR_MESSAGE);
        } catch (final ClassNotFoundException e) {
            System.out.println("ClassNotFound: " + e.getMessage());
            JOptionPane.showMessageDialog(null, e.getMessage(), "WMSX GUI - ClassNotFound", JOptionPane.ERROR_MESSAGE);
        }
	}
	/** Private innere statische Klasse, realisiert Singleton Pattern
	 *
	 */
	private static class SingletonHolder
	{
		private final static Requestor INSTANCE = new Requestor();
	}

	/** Gibt immer die gleiche Instanz zurück
	 * @return Instance der Game Klasse
	 */
	public static Requestor getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	/* Singleton */

   public Wmsx getWmsxService()
   {
       return wmsx_service;
   }
}
