/*
 * WMSX - Workload Management Extensions for gLite
 * 
 * Copyright (C) 2007-2009 Max Berger
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see http://www.gnu.org/licenses/.
 */

/* $Id$ */

package hu.kfki.grid.wmsx.worker;

import hu.kfki.grid.wmsx.JobInfo;
import hu.kfki.grid.wmsx.backends.Backends;
import hu.kfki.grid.wmsx.backends.JobUid;
import hu.kfki.grid.wmsx.job.JobState;
import hu.kfki.grid.wmsx.job.JobWatcher;
import hu.kfki.grid.wmsx.job.description.EmptyJobDescription;
import hu.kfki.grid.wmsx.util.FileUtil;

import java.io.File;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Logger;

import net.jini.id.Uuid;
import at.ac.uibk.dps.wmsx.util.VirtualFile;

/**
 * Controller for worker jobs.
 * 
 * @version $Date$
 */
public class ControllerImpl implements Controller, Runnable {

    private final class WorkerNotifyThread implements Runnable {
        private WorkerNotifyThread() {
            // empty on purpose.
        }

        public void run() {
            final Set<Map.Entry<Uuid, WorkerInfo>> workersToNotify;
            synchronized (ControllerImpl.this.workerInfo) {
                workersToNotify = new HashSet<Map.Entry<Uuid, WorkerInfo>>(
                        ControllerImpl.this.workerInfo.entrySet());
            }
            for (final Map.Entry<Uuid, WorkerInfo> w : workersToNotify) {
                ControllerImpl.this.notifyWorker(w.getKey(), w.getValue());
            }
        }

    }

    private static final int STARTUP_DELAY = 30;

    private static final int MAX_TIME_WITHOUT_PING = 120;

    private static final int MAX_TIME_RUNNING = 600;

    private static final int MSEC_TO_SEC = 1000;

    // private static final long IGNORE_PREFERRED_AFTER = 15000;

    private static final Logger LOGGER = Logger.getLogger(ControllerImpl.class
            .toString());

    private final Map<Object, JobUid> juidMap = new TreeMap<Object, JobUid>();

    private final List<ControllerWorkDescription> pending = new LinkedList<ControllerWorkDescription>();

    private final Map<Object, ControllerWorkDescription> running = new TreeMap<Object, ControllerWorkDescription>();

    private final Map<Object, List<VirtualFile>> success = new TreeMap<Object, List<VirtualFile>>();

    private final Set<Object> failed = new TreeSet<Object>();

    private final Map<Uuid, WorkerInfo> workerInfo = new HashMap<Uuid, WorkerInfo>();

    private final ControllerWorkDescription shutdownWorkDescription;

    private boolean pendingCheckRunning;

    private final FileManager fileManager = new FileManager();

    private final FileUtil fileUtil = FileUtil.getInstance();

    ControllerImpl() {
        this.shutdownWorkDescription = new ControllerWorkDescription(
                "shutdown", new EmptyJobDescription());
        this.fileUtil.supportVirtualFiles();
    }

    private void notifyWorker(final Uuid id, final WorkerInfo wi) {
        try {
            final Worker proxy = wi.getProxy();
            if (proxy != null) {
                proxy.newWork();
            }
        } catch (final RemoteException r) {
            synchronized (this.workerInfo) {
                this.workerInfo.remove(id);
            }
        }
    }

    /** {@inheritDoc} */
    public WorkDescription retrieveWork(final Uuid uuid) {
        final Object jobid;
        this.ping(uuid);
        final ControllerWorkDescription cwd;
        final WorkDescription wd;
        synchronized (this.pending) {
            final long now = System.currentTimeMillis();
            cwd = this.getWorkdescriptionForUuid(uuid, now);
            if (cwd == null) {
                return null;
            }
            wd = cwd.getWorkDescription();
            if (cwd == this.shutdownWorkDescription) {
                return wd;
            }

            jobid = wd.getId();
            this.running.put(jobid, cwd);
            this.completeInfoOnRunning(uuid, jobid);

            this.fileManager.modifyInputSandbox(uuid, wd.getWorkflowId(), cwd
                    .getInputSandbox(), wd.getInputSandbox());
        }
        ControllerImpl.LOGGER.info("Assigning job " + jobid + " to worker "
                + uuid);
        JobWatcher.getInstance().checkWithState(this.getJuidForId(jobid),
                JobState.RUNNING);
        this.startPendingCheck();
        return wd;
    }

    /**
     * @param uuid
     * @param jobid
     */
    private void completeInfoOnRunning(final Uuid uuid, final Object jobid) {
        final JobInfo info = this.getInfoForJob(jobid);
        // Running must be set for suspicious check to work.
        info.setStartRunningTime(new Date());
        info.setWorkerId(uuid);
    }

    /**
     * @param jobid
     * @return
     */
    private JobInfo getInfoForJob(final Object jobid) {
        final JobUid jid = this.getJuidForId(jobid);
        final JobInfo info = JobWatcher.getInstance().getInfoForJob(jid);
        return info;
    }

    private ControllerWorkDescription getWorkdescriptionForUuid(
            final Uuid uuid, final long now) {

        synchronized (this.workerInfo) {
            final WorkerInfo i = this.workerInfo.get(uuid);
            if (i.shouldShutdown()) {
                return this.shutdownWorkDescription;
            }
        }

        // ControllerWorkDescription cwd = null;
        // synchronized (this.local) {
        // final Iterator<ControllerWorkDescription> it = this.pending
        // .iterator();
        // while (it.hasNext() && cwd == null) {
        // final ControllerWorkDescription cwdCur = it.next();
        // final Boolean prefLocal = cwdCur.getPreferLocal();
        // if (prefLocal == null
        // || now - cwdCur.getCreationTime() >
        // ControllerImpl.IGNORE_PREFERRED_AFTER
        // || prefLocal.booleanValue() == this.local.contains(uuid
        // .toString())) {
        // cwd = cwdCur;
        // it.remove();
        // }
        // }
        // }
        // return cwd;
        if (this.pending.isEmpty()) {
            return null;
        } else {
            return this.pending.remove(0);
        }
    }

    private void startPendingCheck() {
        synchronized (this.pending) {
            if (!this.pendingCheckRunning) {
                this.pendingCheckRunning = true;
                new Thread(this).start();
            }
        }
    }

    /**
     * Add new work to be done.
     * 
     * @param newWork
     *            work description.
     */
    public void addWork(final ControllerWorkDescription newWork) {
        synchronized (this.pending) {
            this.pending.add(newWork);
        }
        JobWatcher.getInstance().checkWithState(
                this.getJuidForId(newWork.getWorkDescription().getId()),
                JobState.STARTUP);
        this.notifyAllWorkers();
    }

    private void notifyAllWorkers() {
        new Thread(new WorkerNotifyThread()).start();
    }

    /** {@inheritDoc} */
    public void doneWith(final Object id, final ResultDescription result,
            final Uuid uuid) throws RemoteException {
        final boolean isNew;
        synchronized (this.pending) {
            if (this.running.containsKey(id)) {
                final ControllerWorkDescription cwd = this.running.remove(id);
                final List<VirtualFile> outputSandbox = result
                        .getOutputSandbox();
                this.success.put(id, outputSandbox);
                this.fileManager.parseOutputSandbox(uuid, cwd
                        .getWorkDescription().getWorkflowId(), outputSandbox);
                isNew = true;
            } else {
                isNew = false;
                ControllerImpl.LOGGER
                        .info("Retrieved duplicate result for job " + id + " ("
                                + uuid + ")");
            }
            this.pending.remove(id);
        }
        synchronized (this.workerInfo) {
            this.getWorkerInfo(uuid).resetRetries();
        }

        if (isNew) {
            ControllerImpl.LOGGER.info("Done with worker Job " + id + " ("
                    + uuid + ")");
            JobWatcher.getInstance().checkWithState(this.getJuidForId(id),
                    JobState.SUCCESS);
        }
    }

    /**
     * Check job status.
     * 
     * @param id
     *            Internal Id
     * @return current {@link JobState}.
     */
    public JobState getState(final Object id) {
        JobState retVal = JobState.NONE;
        synchronized (this.pending) {
            if (this.failed.contains(id)) {
                retVal = JobState.FAILED;
            } else if (this.success.keySet().contains(id)) {
                retVal = JobState.SUCCESS;
            } else if (this.running.keySet().contains(id)) {
                retVal = JobState.RUNNING;
            } else {
                for (final ControllerWorkDescription cwd : this.pending) {
                    if (cwd.getWorkDescription().getId().equals(id)) {
                        retVal = JobState.STARTUP;
                        break;
                    }
                }
            }
        }
        return retVal;
    }

    /**
     * Store the Sandbox from a given job into a given directory.
     * 
     * @param id
     *            Id of the (finished) job.
     * @param dir
     *            Directory to store into.
     */
    public void retrieveSandbox(final Object id, final File dir) {
        final List<VirtualFile> sandbox;
        synchronized (this.pending) {
            sandbox = this.success.get(id);
        }
        this.fileUtil.retrieveSandbox(sandbox, dir);
        synchronized (this.pending) {
            this.success.remove(id);
        }
    }

    /**
     * convert internal id to JobUid.
     * 
     * @param id
     *            internal id
     * @return a JobUid
     */
    public JobUid getJuidForId(final Object id) {
        JobUid j;
        synchronized (this.juidMap) {
            j = this.juidMap.get(id);
            if (j == null) {
                j = new JobUid(
                        Backends.getInstance().get(WorkerBackend.WORKER), id);
                this.juidMap.put(id, j);
            }
        }
        return j;
    }

    /** {@inheritDoc} */
    public void ping(final Uuid uuid) {
        if (uuid != null) {
            synchronized (this.workerInfo) {
                final WorkerInfo info = this.getWorkerInfo(uuid);
                info.updateLastSeen();
            }
        }
    }

    private WorkerInfo getWorkerInfo(final Uuid uuid) {
        final WorkerInfo w = this.workerInfo.get(uuid);
        if (w == null) {
            final WorkerInfo n = new WorkerInfo();
            this.workerInfo.put(uuid, n);
            return n;
        } else {
            return w;
        }
    }

    /** pending checker. */
    public void run() {
        boolean goon = true;
        while (goon) {
            try {
                Thread.sleep(ControllerImpl.STARTUP_DELAY
                        * ControllerImpl.MSEC_TO_SEC);
            } catch (final InterruptedException e) {
                // ignore
            }
            synchronized (this.pending) {
                final Set<Object> suspicous = this.findSuspicious();
                this.rescheduleSuspicous(suspicous);
                goon = !this.running.isEmpty();
                if (!goon) {
                    this.pendingCheckRunning = false;
                }
            }
        }
    }

    private void rescheduleSuspicous(final Set<Object> suspicous) {
        for (final Object id : suspicous) {
            boolean found = false;
            for (final ControllerWorkDescription w : this.pending) {
                if (w.getWorkDescription().getId().equals(id)) {
                    found = true;
                }
            }
            if (!found) {
                ControllerImpl.LOGGER.info("Rescheduling suspicious Job " + id);
                final ControllerWorkDescription cwd = this.running.get(id);
                if (cwd.decreaseRetry()) {
                    this.pending.add(cwd);
                } else {
                    ControllerImpl.LOGGER.info("No more retries for " + id);
                    this.running.remove(id);
                    this.failed.add(id);
                    JobWatcher.getInstance().checkWithState(
                            this.getJuidForId(id), JobState.FAILED);
                }
            }
        }
    }

    private Set<Object> findSuspicious() {
        final Set<Object> suspicous = new TreeSet<Object>();

        synchronized (this.workerInfo) {
            final long now = new Date().getTime();
            for (final Object id : this.running.keySet()) {
                final JobInfo jinfo = this.getInfoForJob(id);
                final Uuid uuid = jinfo.getWorkerId();
                final WorkerInfo info = this.getWorkerInfo(uuid);
                final long seen = info.getLastSeen();
                final long alive = now - seen;
                if (alive > ControllerImpl.MAX_TIME_WITHOUT_PING
                        * ControllerImpl.MSEC_TO_SEC) {
                    suspicous.add(id);
                }
                final long timerunning = now
                        - jinfo.getStartRunningTime().getTime();
                if (timerunning > ControllerImpl.MAX_TIME_RUNNING
                        * ControllerImpl.MSEC_TO_SEC) {
                    suspicous.add(id);
                    info.decreaseRetries();
                }
            }
        }
        return suspicous;
    }

    /**
     * schedules all currently known workers to be shut down.
     */
    public void scheduleShutdownForAllWorkers() {
        synchronized (this.workerInfo) {
            for (final WorkerInfo wi : this.workerInfo.values()) {
                wi.scheduleShutdown();
            }
        }
        this.notifyAllWorkers();
    }

    /**
     * schedules the given worker to be shut down.
     * 
     * @param uuid
     *            Id of the worker.
     */
    public void scheduleShutdownForWorker(final Uuid uuid) {
        final WorkerInfo wi;
        synchronized (this.workerInfo) {
            wi = this.workerInfo.get(uuid);
            if (wi != null) {
                wi.scheduleShutdown();
            }
        }
        if (wi != null) {
            this.notifyWorker(uuid, wi);
        }
    }

    /** {@inheritDoc} */
    public void registerWorker(final Uuid uuid, final Worker worker)
            throws RemoteException {
        ControllerImpl.LOGGER.info("Worker is registering: " + uuid);
        final WorkerInfo info;
        synchronized (this.workerInfo) {
            info = this.getWorkerInfo(uuid);
            info.setProxy(worker);
            ControllerImpl.LOGGER.info("Now there are "
                    + this.workerInfo.size() + " registered workers.");
        }
        this.notifyWorker(uuid, info);
    }

    /**
     * Set this worker as a "local" worker.
     * 
     * @param uuid
     *            Uuid of the worker
     */
    public void setIsLocal(final Uuid uuid) {
        synchronized (this.workerInfo) {
            final WorkerInfo info = this.getWorkerInfo(uuid);
            info.setIsLocal();
        }
    }

    /** {@inheritDoc} */
    public void failed(final Object id, final Uuid uuid) throws RemoteException {
        ControllerImpl.LOGGER.info("Worker job failed " + id + " (" + uuid
                + ")");

        synchronized (this.workerInfo) {
            this.getWorkerInfo(uuid).decreaseRetries();
        }
        synchronized (this.pending) {
            final ControllerWorkDescription cwd = this.running.get(id);
            final String wfid = cwd.getWorkDescription().getWorkflowId();
            this.fileManager.clearWorker(uuid, wfid);
            final Set<Object> suspicous = Collections.singleton(id);
            this.rescheduleSuspicous(suspicous);
        }
    }

    /**
     * Cancels the given job.
     * 
     * @param id
     *            Id.
     */
    public void cancelJob(final Object id) {
        boolean didcancel = false;
        synchronized (this.pending) {
            if (this.running.keySet().contains(id)) {

                final JobInfo jinfo = this.getInfoForJob(id);
                final Uuid uuid = jinfo.getWorkerId();

                new Thread(new Runnable() {

                    public void run() {
                        ControllerImpl.this.sendCancelNotification(uuid, id);
                    }
                }).start();
                didcancel = true;
            } else {
                final Iterator<ControllerWorkDescription> cwdid = this.pending
                        .iterator();
                while (cwdid.hasNext()) {
                    final ControllerWorkDescription cwd = cwdid.next();
                    if (cwd.getWorkDescription().getId().equals(id)) {
                        cwdid.remove();
                        didcancel = true;
                    }
                }
            }
            if (didcancel) {
                this.failed.add(id);
                JobWatcher.getInstance().checkWithState(this.getJuidForId(id),
                        JobState.FAILED);
            }
        }
    }

    private void sendCancelNotification(final Uuid uuid, final Object id) {
        final Worker proxy;
        synchronized (this.workerInfo) {
            final WorkerInfo wi = this.workerInfo.get(uuid);
            if (wi == null) {
                proxy = null;
            } else {
                proxy = wi.getProxy();
            }
        }
        if (proxy != null) {
            try {
                proxy.cancel(id);
            } catch (final RemoteException e) {
                ControllerImpl.LOGGER.info(e.getMessage());
            }
        }
    }
}
