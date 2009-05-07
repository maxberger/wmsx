package at.ac.uibk.dps.wmsxgui.business;

import hu.kfki.grid.wmsx.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.swing.JOptionPane;
import net.jini.admin.Administrable;
import com.sun.jini.admin.DestroyAdmin;
import java.rmi.RemoteException;

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
            JOptionPane.showMessageDialog(null, io.getMessage()+"\nFailed to connect to provider. Please check if its running.", "WMSX Gui - IOException", JOptionPane.ERROR_MESSAGE);
        } catch (final ClassNotFoundException e) {
            System.out.println("ClassNotFound: " + e.getMessage());
            JOptionPane.showMessageDialog(null, e.getMessage(), "WMSX Gui - ClassNotFound", JOptionPane.ERROR_MESSAGE);
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

    
    /*
    * kill          shutdown the service provider
    */
    public void shutdownServiceProvider()
    {
        try {
            final Administrable mys = (Administrable) wmsx_service;
            final Object adm = mys.getAdmin();
            final DestroyAdmin dadm = (DestroyAdmin) adm;
            dadm.destroy();

        } catch (final ClassCastException cc) {
            System.out.println("ClassCastException: "+ cc.getMessage());
        } catch (final NullPointerException npe) {
            System.out.println("NullPointerException: "+ npe.getMessage());
        } catch (final RemoteException re) {
            System.out.println("RemoteException: "+ re.getMessage());
        }
    }
    
   /*
    * full-ping     full check if provider is running
    * ping          quick check if provider is running
    */
    public boolean ping(final boolean fullping)
    {
        return wmsx_service.ping(fullping);
    }

    /*
    * number        set number of active jobs
    */
    public void setActiveJobCount(int number)
    {
         wmsx_service.setMaxJobs(number);
    }

    /*
    * workers       submit number of workers
    */
    public void setWorkerCount(int number)
    {
         wmsx_service.startWorkers(number);
    }

    /*
    * shutdownworkers   shutdown all workers
    */
    public void shutdownWorkers(int number)
    {
         wmsx_service.shutdownWorkers();
    }

    /*
    * args          submit a Laszlo-style args file
    */
    public void submitLaszlo(String argFile, boolean interactive, String name)
    {
        try
        {
            wmsx_service.submitLaszlo(argFile, interactive, name);
            
        } catch (final IOException io) {
            System.out.println("IOException: " + io.getMessage());
        }
    }

    /*
    * jdl           submit a JDL file
    */
    public SubmissionResult submitJdl(String jdlFile, String outputFile, String resultDir )
    {
        try
        {
            return wmsx_service.submitJdl(jdlFile, outputFile, resultDir);

        } catch (final IOException io) {
            System.out.println("IOException: " + io.getMessage());
            return null;
        }
       
    }
    
    /*
    * forgetafs         Forgets AFS password
    */
    public void forgetAfsPassword()
    {
         wmsx_service.forgetAfs();
    }

    /*
     * rememberafs       Asks for AFS password and remember it until told to forget
    */
    public boolean rememberAfsPassword(String password)
    {
         return wmsx_service.rememberAfs(password);
    }

    /*
     * remembergrid      Asks for Grid password and remember it until the
     *                   number of managed jobs reaches 0.
    */
    public boolean rememberGridPassword(String password)
    {
         return wmsx_service.rememberGrid(password);
    }

    /*
     * vo                VO for job submissions
    */
    public void setVo(String newVo)
    {
         wmsx_service.setVo(newVo);
    }

    /*
     * listbackends      lists possible backends
    */
    public String listBackends()
    {
         return wmsx_service.listBackends();
    }

    /*
     * backend           Backend for job submissions, use listbackends to get a list
    */
    public void setBackend(String backend)
    {
         wmsx_service.setBackend(backend);
    }

}
