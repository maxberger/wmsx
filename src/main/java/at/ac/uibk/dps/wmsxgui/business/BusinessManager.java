/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.ac.uibk.dps.wmsxgui.business;

import hu.kfki.grid.wmsx.Wmsx;
import hu.kfki.grid.wmsx.JobInfo;
import hu.kfki.grid.wmsx.TransportJobUID;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author bafu
 */
public class BusinessManager {

    private Requestor requestor;
    private Wmsx wmsx_service;
    private boolean isOnline;

    private Map<String, List<JobData>> jobmap = new HashMap<String, List<JobData>>();
    private Iterable<String> backends;
    
    /* Singleton Pattern */
	private BusinessManager()
	{
        requestor = Requestor.getInstance();
        wmsx_service = requestor.getWmsxService();;
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
   }
}
