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
import net.jini.export.Exporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.tcp.TcpServerEndpoint;
import net.jini.lease.LeaseListener;
import net.jini.lease.LeaseRenewalEvent;
import net.jini.lease.LeaseRenewalManager;

/**
 *
 * @author bafu
 */
public class BusinessManager extends Observable implements RemoteEventListener {
    private static final long serialVersionUID = 4569728891303483934L;

    private Requestor requestor;
    private Wmsx wmsx_service;
    private boolean isOnline;

    private Map<String, List<JobData>> jobmap = new HashMap<String, List<JobData>>();
    private Iterable<String> backends;

    private RemoteEventListener theStub;
    private LeaseRenewalManager theManager;
    private Lease lease;

    /* Singleton Pattern */
	private BusinessManager()
    {

        requestor = Requestor.getInstance();
        wmsx_service = requestor.getWmsxService();

        try{

            Exporter myDefaultExporter =
                new BasicJeriExporter(TcpServerEndpoint.getInstance(0),
                                      new BasicILFactory(), false, true);

            theStub = (RemoteEventListener) myDefaultExporter.export(this);

            lease = wmsx_service.registerEventListener(theStub);

            theManager = new LeaseRenewalManager();
            theManager.renewFor(lease, Lease.FOREVER,30000, new DebugListener());

        } catch (Exception re) {
            re.printStackTrace();
        }


        refreshData();
    }

    RemoteEventListener getStub() {
        return theStub;
    }

    private static class DebugListener implements LeaseListener {
        public void notify(LeaseRenewalEvent anEvent) {
            System.err.println("BusinessManager: Got lease renewal problem!");

            System.err.println(anEvent.getException());
            System.err.println(anEvent.getExpiration());
            System.err.println(anEvent.getLease());
        }
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
       JobChangeEvent e = (JobChangeEvent)re;
       System.out.println("BusinessManager: notified by provider..."+e.getJobUid()+" State: "+e.getState());
       for (JobData job : jobmap.get(e.getJobUid().getBackend()))
           if (job.getTransportJobUID().equals(e.getJobUid()))
           {
               job.getJobinfo().setStatus(e.getState());
               updateObservers();
           }
   }
}
