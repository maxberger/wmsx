/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.ac.uibk.dps.wmsxgui.business;

import hu.kfki.grid.wmsx.JobChangeEvent;
import hu.kfki.grid.wmsx.Wmsx;
import hu.kfki.grid.wmsx.TransportJobUID;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.event.UnknownEventException;
import net.jini.core.lease.Lease;

/**
 *
 * @author bafu
 */
public class BusinessManager extends Observable implements RemoteEventListener {

    private Requestor requestor;
    private Wmsx wmsx_service;
    private boolean isOnline;

    private Map<String, List<JobData>> jobmap = new HashMap<String, List<JobData>>();
    private Iterable<String> backends;
    
    /* Singleton Pattern */
	private BusinessManager()
	{
        requestor = Requestor.getInstance();
        wmsx_service = requestor.getWmsxService();
        Lease lease = wmsx_service.registerEventListener(this);

        refreshData();
    }

    /** Private innere statische Klasse, realisiert Singleton Pattern
	 *
	 */
	private static class SingletonHolder
	{
		private static BusinessManager INSTANCE = new BusinessManager();
	}

	/** Gibt immer die gleiche Instanz zurück
	 * @return Instance der Game Klasse
	 */
	public static BusinessManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	/* Singleton */


   public Wmsx getWmsxService()
   {
       return wmsx_service;
   }

   public boolean isOnline()
   {
       if (wmsx_service!=null)
           return true;
       else
           return false;
   }

   public Iterable<String> getBackends() {
       return backends;
   }

   public List<JobData> getJobs(String backend)
   {
       return jobmap.get(backend);
   }

   public void refreshData()
   {
       System.out.println("BusinessManager: refreshData...");
       if (isOnline())
       {
           jobmap.clear();

           backends = wmsx_service.listBackends();
           for (String backend : backends)
               jobmap.put(backend, new ArrayList<JobData>());

           for (TransportJobUID transJobUID : wmsx_service.listJobs())
           {
               jobmap.get(transJobUID.getBackend()).add(new JobData(transJobUID,wmsx_service.getJobInfo(transJobUID)));
           }
       }
       updateObservers();
   }

   private void updateObservers()
   {
       setChanged();
       notifyObservers();
   }


   public void notify(RemoteEvent re) throws UnknownEventException, RemoteException {
       System.out.println("BusinessManager: notified by provider...");
       JobChangeEvent e = (JobChangeEvent)re;
       for (JobData job : jobmap.get(e.getJobUid().getBackend()))
           if (job.getTransportJobUID().equals(e.getJobUid()))
           {
               job.getJobinfo().setStatus(e.getState());
               updateObservers();
           }
   }
}
