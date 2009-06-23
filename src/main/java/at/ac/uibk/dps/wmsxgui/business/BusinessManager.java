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

    private static class DebugListener implements LeaseListener {
        public void notify(final LeaseRenewalEvent anEvent) {
            System.err.println("BusinessManager: Got lease renewal problem!");

            System.err.println(anEvent.getException());
            System.err.println(anEvent.getExpiration());
            System.err.println(anEvent.getLease());
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
     * Gibt immer die gleiche Instanz zurück
     * 
     * @return Instance der Game Klasse
     */
    public static BusinessManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /* Singleton */

    public Wmsx getWmsxService() {
        return this.wmsxService;
    }

    public String getCurrentBackend() {
        return this.currentBackend;
    }

    public void setCurrentBackend(final String currentBackend) {
        this.currentBackend = currentBackend;
        this.updateObservers(currentBackend);
    }

    public boolean isOnline() {
        if (this.wmsxService != null) {
            return true;
        } else {
            return false;
        }
    }

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

    public List<Integer> getExpansionStateRows() {
        return this.expandedNodesRowIndex;
    }

    public Iterable<String> getBackends() {
        return this.backends;
    }

    public List<JobData> getJobs(final String backend) {
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

    public JobData getJobData(final TransportJobUID uid) {
        for (final JobData jd : this.getJobs(uid.getBackend())) {
            if (jd.getTransportJobUID().equals(uid)) {
                return jd;
            }
        }
        return null;
    }

    public List<JobData> getJobsTable() {
        return this.getJobs(this.getCurrentBackend());
    }

    public void cleanupBusinessData() {
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
                this.jobMap.get(transJobUID.getBackend().toLowerCase())
                        .add(
                             new JobData(transJobUID, this.wmsxService
                                     .getJobInfo(transJobUID)));
            }
        }
        this.updateObservers(null);
    }

    public void refreshBusinessData() {
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
                String backend = transJobUID.getBackend().toLowerCase();
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

    public void notify(final RemoteEvent re) throws UnknownEventException,
            RemoteException {
        final JobChangeEvent e = (JobChangeEvent) re;
        System.out.println("BusinessManager: notified by provider..."
                + e.getJobUid() + " State: " + e.getState());
        // Achtung: Fake vs. fake --> darum toLowerCase
        if (this.jobMap != null) {
            for (final JobData job : this.jobMap.get(e.getJobUid().getBackend()
                    .toLowerCase())) {
                if (job.getTransportJobUID().equals(e.getJobUid())) {
                    job.setJobinfo(this.wmsxService.getJobInfo(e.getJobUid()));
                    this.updateObservers(job);
                }
            }
        }
    }
}
