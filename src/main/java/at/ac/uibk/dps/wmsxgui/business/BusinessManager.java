/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.ac.uibk.dps.wmsxgui.business;

import hu.kfki.grid.wmsx.JobChangeEvent;
import hu.kfki.grid.wmsx.TransportJobUID;
import hu.kfki.grid.wmsx.Wmsx;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
 * @author WmsxGUI Team
 * @version 1.0
 */
public class BusinessManager extends Observable implements RemoteEventListener {
    private static final long serialVersionUID = 4569728891303483934L;

    private final Requestor requestor;
    private final Wmsx wmsxService;
    private final Map<String, List<JobData>> jobMap = new HashMap<String, List<JobData>>();
    private Iterable<String> backends;

    private RemoteEventListener stub;
    private LeaseRenewalManager manager;
    private Lease lease;

    private final List<Integer> expandedNodesRowIndex = new ArrayList<Integer>();
    private String currentBackend;

    /* Singleton Pattern */
    private BusinessManager() {

        this.requestor = Requestor.getInstance();
        this.wmsxService = this.requestor.getWmsxService();

        if (this.isOnline())
        {
            try {

                final Exporter myDefaultExporter = new BasicJeriExporter(
                        TcpServerEndpoint.getInstance(0), new BasicILFactory(),
                        false, true);

                this.stub = (RemoteEventListener) myDefaultExporter.export(this);

                this.lease = this.wmsxService.registerEventListener(this.stub);

                this.manager = new LeaseRenewalManager();
                this.manager.renewFor(this.lease, Lease.FOREVER, 30000,
                                         new DebugListener());

            } catch (final Exception re) {
                re.printStackTrace();
            }

            //fill BusinessData from wmsxService
            this.cleanupBusinessData();
        }
    }

    private static class DebugListener implements LeaseListener {
        @Override
        public void notify(final LeaseRenewalEvent anEvent) {
            if (anEvent.getException()!=null)
            {
                System.err.println("BusinessManager: Got lease renewal problem!");

                System.err.println(anEvent.getException());
                System.err.println(anEvent.getExpiration());
                System.err.println(anEvent.getLease());
            }
        }
    }

    /**
     * Private innere statische Klasse, realisiert Singleton Pattern
     * 
     */
    private static class SingletonHolder {
        private static BusinessManager INSTANCE = new BusinessManager();
    }

    /**
     * Gibt immer die gleiche Instanz zurück.
     * 
     * @return Instance der Game Klasse
     */
    public static BusinessManager getInstance() {
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

    /**
     * getCurrentBackend is a Getter for the current backend.
     * @return reutrns the current backend
     */
    public String getCurrentBackend() {
        return this.currentBackend;
    }

    /**
     * setCurrentBackend is a Setter for the current backend.
     * @param currentBackend Backend which should be active now
     */
    public void setCurrentBackend(final String currentBackend) {
        this.currentBackend = currentBackend;
        this.updateObservers(currentBackend);
    }

    /**
     * isOnline checks if there is a connection to the provider.
     * @return true if ther is a connection to the provider, otherwise false
     */
    public boolean isOnline() {
        if (this.wmsxService != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * saveExpansionState saves the expanded state for the given tree.
     * @param treeJobs JTreefo for which the expansion state should be saved
     */
    public void saveExpansionState(final JTree treeJobs) {
        // System.out.println("BusinessManager: saveExpansionState...");
        this.expandedNodesRowIndex.clear();
        final Enumeration<TreePath> expandedNodes = treeJobs
                .getExpandedDescendants(new TreePath(treeJobs.getModel()
                        .getRoot()));
        while (expandedNodes.hasMoreElements()) {
            final TreePath treePath = expandedNodes.nextElement();
            this.expandedNodesRowIndex.add(treeJobs.getRowForPath(treePath));
        }
        Collections.sort(this.expandedNodesRowIndex);
    }

    /**
     * getExpansionStateRows is a Getter for ExpansionState Rows, which keeps
     * information about the expanded tree.
     * @return List of Rows which should be expanded in the Tree
     */
    public List<Integer> getExpansionStateRows() {
        return this.expandedNodesRowIndex;
    }

    /**
     * getBackends is a Getter for the Backends
     * @return Backends from the BusinessData
     */
    public Iterable<String> getBackends() {
        return this.backends;
    }

    /**
     * getJobs calculates all jobs for the given Backend.
     * @param backend Backend for which you will get all Jobs
     * @return List of JobData objects for the given Backend
     */
    public synchronized List<JobData> getJobs(final String backend) {
        if ((backend != null) && (!backend.equals("Backends"))) {
            return this.jobMap.get(backend);
        } else {
            final List<JobData> joblist = new ArrayList<JobData>();
            for (final List<JobData> jl : this.jobMap.values()) {
                joblist.addAll(jl);
            }

            return joblist;
        }
    }

    /**
     * getJobData calculates the JobData for the given JobUID.
     * @param uid TransportJobUID for which you will get the JobData
     * @return JobData for the given JobUID, null if the JobUID is not found
     */
    public synchronized JobData getJobData(final TransportJobUID uid) {
        for (final JobData jd : this.getJobs(uid.getBackend())) {
            if (jd.getTransportJobUID().equals(uid)) {
                return jd;
            }
        }
        return null;
    }

    /**
     * getJobsTable calculates all jobs for the current Backend.
     * @return List of JobData objects for the current Backend
     */
    public synchronized List<JobData> getJobsTable() {
        return this.getJobs(this.getCurrentBackend());
    }

    /**
     * cleanupBusinessData clears all BusinessData and updates manually
     * the BusinessData by requesting all backends and jobs from the provider.
     */
    public synchronized void cleanupBusinessData() {
        System.out.println("BusinessManager: cleanupBusinessData...");
        if (this.isOnline()) {

            // clear cached data and update via wmsx_service
            this.jobMap.clear();

            this.backends = this.wmsxService.listBackends();
            for (final String backend : this.backends) {
                this.jobMap.put(backend, new ArrayList<JobData>());
            }

            // System.out.println("BusinessManager: refreshData... backends: "+backends.toString());

            for (final TransportJobUID transJobUID : this.wmsxService
                    .listJobs()) {
                // System.out.println("BusinessManager: refreshData... add new job - backend: "+transJobUID.getBackend().toLowerCase());
                // Achtung: fake vs. Fake
                this.jobMap.get(transJobUID.getBackend().toLowerCase(Locale.getDefault()))
                        .add(
                             new JobData(transJobUID, this.wmsxService
                                     .getJobInfo(transJobUID)));
            }
        }
        this.updateObservers(null);
    }

    /**
     * refreshBusinessData updates manually the BusinessData by requesting
     * all backends and jobs from the provider but it keeps all old jobs.
     */
    public synchronized void refreshBusinessData() {
        System.out.println("BusinessManager: refreshBusinessData...");
        if (this.isOnline()) {

            // look for new backends...
            this.backends = this.wmsxService.listBackends();
            for (final String backend : this.backends) {
                if (!jobMap.containsKey(backend))
                    this.jobMap.put(backend, new ArrayList<JobData>());
            }

            // look for new jobs, but keep old jobs!
            for (final TransportJobUID transJobUID : this.wmsxService
                    .listJobs()) {
                // System.out.println("BusinessManager: refreshData... add new job - backend: "+transJobUID.getBackend().toLowerCase());
                // Achtung: fake vs. Fake
                String backend = transJobUID.getBackend().toLowerCase(Locale.getDefault());
                List<JobData> jobs = this.jobMap.get(backend);
                JobData jobData = new JobData(transJobUID, this.wmsxService
                                     .getJobInfo(transJobUID));

                if (!jobs.contains(jobData))
                    jobs.add(jobData);
            }
        }
        this.updateObservers(null);
    }

    private void updateObservers(final Object o) {
        this.setChanged();
        this.notifyObservers(o);
    }

    /**
     * notify is executed when some JobInfo changed by the provider
     * and updates the Businesslayer and GUI.
     * @param re RemoteEvent, which includes the modified JobData
     * @throws net.jini.core.event.UnknownEventException if some unknown event occurs
     * @throws java.rmi.RemoteException if some network error occurs
     */
    @Override
    public synchronized void notify(final RemoteEvent re) throws UnknownEventException,
            RemoteException {

        final JobChangeEvent e = (JobChangeEvent) re;
        System.out.println("BusinessManager: notified by provider..."
                + e.getJobUid() + " State: " + e.getState());

        // Achtung: Fake vs. fake --> darum toLowerCase
        if (this.jobMap != null) {
            for (final JobData job : this.jobMap.get(e.getJobUid().getBackend()
                    .toLowerCase(Locale.getDefault()))) {
                if (job.getTransportJobUID().equals(e.getJobUid())) {
                    job.setJobinfo(this.wmsxService.getJobInfo(e.getJobUid()));
                    this.updateObservers(job);
                }
            }
        }

    }
}
