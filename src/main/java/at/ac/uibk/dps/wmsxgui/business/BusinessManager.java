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
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import javax.swing.JTree;
import javax.swing.tree.TreePath;
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

    private List<Integer> expandedNodesRowIndex = new ArrayList<Integer>();
    private String currentBackend;

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

   public String getCurrentBackend() {
       return currentBackend;
   }

   public void setCurrentBackend(String currentBackend) {
       this.currentBackend = currentBackend;
       updateObservers(currentBackend);
   }


   public boolean isOnline()
   {
       if (wmsx_service!=null)
           return true;
       else
           return false;
   }

   public void saveExpansionState(JTree tree_jobs)
   {
        //System.out.println("BusinessManager: saveExpansionState...");
        expandedNodesRowIndex.clear();
        Enumeration<TreePath> expandedNodes = tree_jobs.getExpandedDescendants(new TreePath(tree_jobs.getModel().getRoot()));
        while (expandedNodes.hasMoreElements())
        {
            TreePath treePath = (TreePath) expandedNodes.nextElement();
            expandedNodesRowIndex.add(tree_jobs.getRowForPath(treePath));
        }
        Collections.sort(expandedNodesRowIndex);
   }
   
   public List<Integer> getExpansionStateRows()
   {
       return expandedNodesRowIndex;
   }

   public Iterable<String> getBackends() {
       return backends;
   }

   public List<JobData> getJobs(String backend)
   {
       if ( (backend!=null) && (!backend.equals("Backends")) )
           return jobmap.get(backend);
       else
       {
           List<JobData> joblist = new ArrayList<JobData>();
           for (List<JobData> jl :  jobmap.values())
               joblist.addAll(jl);

           return joblist;
       }
   }

   public List<JobData> getJobsTable()
   {
       return getJobs(getCurrentBackend());
   }

   public void refreshData()
   {
       System.out.println("BusinessManager: refreshData...");
       if (isOnline())
       {
           
           //clear cached data and update via wmsx_service
           jobmap.clear();

           backends = wmsx_service.listBackends();
           for (String backend : backends)
               jobmap.put(backend, new ArrayList<JobData>());

           //System.out.println("BusinessManager: refreshData... backends: "+backends.toString());

           for (TransportJobUID transJobUID : wmsx_service.listJobs())
           {
               //System.out.println("BusinessManager: refreshData... add new job - backend: "+transJobUID.getBackend().toLowerCase());
               //Achtung: fake vs. Fake
               jobmap.get(transJobUID.getBackend().toLowerCase()).add(new JobData(transJobUID,wmsx_service.getJobInfo(transJobUID)));
           }
       }
       updateObservers(null);
   }

   private void updateObservers(Object o)
   {
       setChanged();
       notifyObservers(o);
   }


   public void notify(RemoteEvent re) throws UnknownEventException, RemoteException {
       JobChangeEvent e = (JobChangeEvent)re;
       System.out.println("BusinessManager: notified by provider..."+e.getJobUid()+" State: "+e.getState());
       //Achtung: Fake vs. fake --> darum toLowerCase
       for (JobData job : jobmap.get(e.getJobUid().getBackend().toLowerCase()))
           if (job.getTransportJobUID().equals(e.getJobUid()))
           {
               job.getJobinfo().setStatus(e.getState());
               updateObservers(job);
           }
   }
}
