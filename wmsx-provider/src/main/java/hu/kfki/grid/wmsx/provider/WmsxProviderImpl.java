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

package hu.kfki.grid.wmsx.provider;

import hu.kfki.grid.wmsx.JobInfo;
import hu.kfki.grid.wmsx.SubmissionResult;
import hu.kfki.grid.wmsx.TransportJobUID;
import hu.kfki.grid.wmsx.backends.Backend;
import hu.kfki.grid.wmsx.backends.Backends;
import hu.kfki.grid.wmsx.backends.JobUid;
import hu.kfki.grid.wmsx.backends.SubmissionResults;
import hu.kfki.grid.wmsx.job.JobListener;
import hu.kfki.grid.wmsx.job.JobWatcher;
import hu.kfki.grid.wmsx.job.LogListener;
import hu.kfki.grid.wmsx.job.description.JDLJobDescription;
import hu.kfki.grid.wmsx.job.description.JobDescription;
import hu.kfki.grid.wmsx.job.result.ResultListener;
import hu.kfki.grid.wmsx.job.shadow.ShadowListener;
import hu.kfki.grid.wmsx.provider.arglist.LaszloJobFactory;
import hu.kfki.grid.wmsx.renewer.AFS;
import hu.kfki.grid.wmsx.renewer.Renewer;
import hu.kfki.grid.wmsx.renewer.RenewerUtil;
import hu.kfki.grid.wmsx.util.LogUtil;
import hu.kfki.grid.wmsx.util.ScriptLauncher;
import hu.kfki.grid.wmsx.worker.ControllerServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.WritableByteChannel;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import net.jini.core.event.RemoteEventListener;
import net.jini.core.lease.Lease;
import net.jini.id.Uuid;
import net.jini.id.UuidFactory;
import at.ac.uibk.dps.wmsx.leases.LandlordImpl;

import com.sun.jini.admin.DestroyAdmin;

/**
 * My Jini Service Implementation!
 * 
 * @version $Date$
 */
public class WmsxProviderImpl implements IRemoteWmsxProvider, RemoteDestroy,
        JobListener, Runnable {

    private static final String PREEXEC_SUFFIX = "_preexec";

    private static final int WAIT_TIME_BEFORE_SHUTDOWN = 1000;

    private static final int WAIT_TIME_BETWEEN_SUBMISSIONS = 100;

    private static final int WAIT_BEFORE_FORGET_GRID_MIN = 5;

    private static final int SEC_TO_MS = 1000;

    private static final int MIN_TO_SEC = 60;

    private static final int MAX_WORKERS_PER_CALL = 50;

    private static final int INITIAL_MAX_JOBS = 100;

    private static final String JOBIDS_ALL = "jobids.all";

    private static final String JOBIDS_RUNNING = "jobids.running";

    private static final String JOBIDS_DONE = "jobids.done";

    private static final String JOBIDS_FAILED = "jobids.failed";

    private static final long serialVersionUID = 2L;

    private static final Logger LOGGER = Logger
            .getLogger(WmsxProviderImpl.class.toString());

    private static WmsxProviderImpl instance;

    private final DestroyAdmin destroyAdmin;

    private final File workDir;

    private final File outDir;

    private final File debugDir;

    private String vo;

    private int maxJobs = WmsxProviderImpl.INITIAL_MAX_JOBS;

    private final List<JobFactory> pendingJobFactories = new LinkedList<JobFactory>();

    private Renewer afsRenewer;

    private final Map<String, File> dirs = new HashMap<String, File>();

    private Backend currentBackend = Backends.getInstance().get("glitewms");

    /**
     * Default constructor.
     * 
     * @param dadm
     *            reference to DestroyAdmin for shutdown.
     * @param workdir
     *            Work directory.
     */
    public WmsxProviderImpl(final DestroyAdmin dadm, final File workdir) {
        this.destroyAdmin = dadm;
        this.workDir = workdir;

        this.outDir = this.syncableDir(workdir, "out");
        this.debugDir = this.syncableDir(workdir, "debug");
        WmsxProviderImpl.instance = this;
        ControllerServer.getInstance().prepareWorker(
                this.syncableDir(this.debugDir, "worker"));
    }

    private File syncableDir(final File parentdir, final String subdir) {
        File dirFile = new File(parentdir, subdir);
        try {
            final String canon = dirFile.getCanonicalPath();
            synchronized (this.dirs) {
                final File existing = this.dirs.get(canon);
                if (existing == null) {
                    this.dirs.put(canon, dirFile);
                    if (!dirFile.exists() && !dirFile.mkdirs()) {
                        throw new IOException("Failed to create " + dirFile);
                    }
                } else {
                    dirFile = existing;
                }
            }
        } catch (final IOException e) {
            WmsxProviderImpl.LOGGER.warning(LogUtil.logException(e));
        }
        return dirFile;
    }

    /**
     * @return the singleton instance.
     */
    public static WmsxProviderImpl getInstance() {
        return WmsxProviderImpl.instance;
    }

    /** {@inheritDoc} */
    public SubmissionResult submitJdl(final String jdlFile,
            final String output, final String resultDir) {
        return this.submitJdl(jdlFile, output, resultDir, 0);
    }

    /**
     * Submit an existing jdl file.
     * 
     * @param jdlFile
     *            name of the file
     * @param output
     *            file where to store stdout if interactive
     * @param resultDir
     *            directory where to retrieve the result to *
     * @param appId
     *            if not 0, use this as an application id for new workflows.
     * @return a jobid
     */
    public synchronized SubmissionResult submitJdl(final String jdlFile,
            final String output, final String resultDir, final int appId) {
        final int current = JobWatcher.getInstance().getNumJobsRunning();
        final int avail = this.maxJobs - current;
        SubmissionResult result;
        try {
            final JobFactory factory = new JdlJobFactory(new JDLJobDescription(
                    new File(jdlFile)), output, resultDir, this.currentBackend,
                    appId);
            if (avail > 0) {
                final JobUid id = this.reallySubmitFactory(factory);
                if (id != null) {
                    result = new SubmissionResult(id.toTransportJobUid());
                } else {
                    result = new SubmissionResult("failed in backend");
                }
            } else {
                this.pendingJobFactories.add(factory);
                result = new SubmissionResult("pending");
            }
        } catch (final IOException io) {
            WmsxProviderImpl.LOGGER.info(LogUtil.logException(io));
            result = new SubmissionResult("Error opening " + jdlFile);
        }
        return result;
    }

    /**
     * Submit a {@link JobFactory} now.
     * 
     * @param factory
     *            The Factory to submit
     * @return the {@link JobUid} or null if the job failed to submit.
     */
    public JobUid reallySubmitFactory(final JobFactory factory) {
        final JdlJob jd = factory.createJdlJob();
        return this.reallySubmitJdl(jd, jd.getBackend());
    }

    private JobUid reallySubmitJdl(final JdlJob job, final Backend backend) {
        final String output = job.getOutput();
        this.runPreexec(job, output);
        WmsxProviderImpl.LOGGER.info("Submitting " + job);
        SubmissionResults result;
        try {
            final JobUid id;
            result = backend.submitJob(job.getJobDescription(), this.vo);
            if (result != null) {
                id = result.getJobId();

                WmsxProviderImpl.LOGGER.info("Job id is: " + id);

                this.fillInInfo(id, job);

                JobWatcher.getInstance()
                        .addWatch(id, LogListener.getInstance());
                JobWatcher.getInstance().addWatch(id, this);

                if (ResultListener.getInstance().setJob(id, job)) {
                    JobWatcher.getInstance().addWatch(id,
                            ResultListener.getInstance());
                }
                final WritableByteChannel oChannel;
                if (output != null && result.getOStream() != null) {
                    new File(output).getParentFile().mkdirs();
                    oChannel = new FileOutputStream(output).getChannel();
                } else {
                    oChannel = null;
                }
                JobWatcher.getInstance().addWatch(id,
                        ShadowListener.listen(result, oChannel));
                synchronized (this.workDir) {
                    this.appendURILine(id, new File(this.workDir,
                            WmsxProviderImpl.JOBIDS_ALL));
                    this.appendURILine(id, new File(this.workDir,
                            WmsxProviderImpl.JOBIDS_RUNNING));
                }
            } else {
                id = null;
            }
            return id;
        } catch (final IOException e) {
            WmsxProviderImpl.LOGGER.warning(LogUtil.logException(e));
        } catch (final NullPointerException e) {
            WmsxProviderImpl.LOGGER.warning(LogUtil.logException(e));
        }
        return null;
    }

    /**
     * @param id
     * @param job
     */
    private void fillInInfo(final JobUid id, final JdlJob job) {
        final JobInfo info = JobWatcher.getInstance().getInfoForJob(id);

        info.setExecutable(job.getJobDescription().getStringEntry(
                JobDescription.EXECUTABLE));
        info.setOutput(job.getOutput());
        info.setDescription(job.getJobDescription().getName());
    }

    private void runPreexec(final JdlJob job, final String output) {
        final String preexec = job.getPreexec();
        if (preexec != null) {
            WmsxProviderImpl.LOGGER.info("Running " + preexec);

            final List<String> cmdVec = new Vector<String>();
            cmdVec.add(preexec);
            cmdVec.add(job.getCommand());
            cmdVec.add(job.getResultDir());
            cmdVec.addAll(Arrays.asList(job.getArgs()));

            ScriptLauncher.getInstance().launchScript(
                    cmdVec.toArray(new String[0]),
                    output + WmsxProviderImpl.PREEXEC_SUFFIX,
                    output + WmsxProviderImpl.PREEXEC_SUFFIX,
                    new File(job.getResultDir()));
        }
    }

    private void removeURILine(final JobUid uid, final File file) {
        final String line = uid.getBackend().jobUidToUri(uid);
        if (line != null) {
            try {
                final List<String> lines = new Vector<String>();
                final BufferedReader in = new BufferedReader(new FileReader(
                        file));
                String inLine = in.readLine();
                while (inLine != null) {
                    if (!inLine.equals(line)) {
                        lines.add(inLine);
                    }
                    inLine = in.readLine();
                }
                in.close();
                final BufferedWriter out = new BufferedWriter(new FileWriter(
                        file, false));
                final Iterator<String> it = lines.iterator();
                while (it.hasNext()) {
                    final String outLine = it.next();
                    out.write(outLine);
                    out.newLine();
                }
                out.close();
            } catch (final IOException e) {
                WmsxProviderImpl.LOGGER.warning(LogUtil.logException(e));
            }
        }
    }

    private void appendURILine(final JobUid uid, final File file) {
        final String uri = uid.getBackend().jobUidToUri(uid);
        if (uri != null) {
            try {
                final BufferedWriter out = new BufferedWriter(new FileWriter(
                        file, true));
                out.write(uri);
                out.newLine();
                out.close();
            } catch (final IOException e) {
                WmsxProviderImpl.LOGGER.warning(LogUtil.logException(e));
            }
        }
    }

    /** {@inheritDoc} */
    public void destroy() throws RemoteException {
        new Thread(new Runnable() {

            public void run() {
                try {
                    JobWatcher.getInstance().shutdown();
                    Thread.sleep(WmsxProviderImpl.WAIT_TIME_BEFORE_SHUTDOWN);
                    WmsxProviderImpl.this.destroyAdmin.destroy();
                } catch (final RemoteException e) {
                    // ignore
                } catch (final InterruptedException e) {
                    // ignore
                }
            }
        }).start();
    }

    /** {@inheritDoc} */
    public synchronized void setMaxJobs(final int maxj) {
        WmsxProviderImpl.LOGGER.info("setMaxJobs to " + maxj);
        this.maxJobs = maxj;
        this.investigateLater();
    }

    /** {@inheritDoc} */
    public void startWorkers(final int num) {
        final int n = Math.max(1, Math.min(
                WmsxProviderImpl.MAX_WORKERS_PER_CALL, num));
        ControllerServer.getInstance().submitWorkers(this.currentBackend, n);
    }

    private synchronized void investigateNumJobs() {
        if (this.pendingJobFactories.isEmpty()
                && JobWatcher.getInstance().getNumJobsRunning() == 0) {
            new Thread(new Runnable() {

                public void run() {
                    try {
                        Thread
                                .sleep(WmsxProviderImpl.WAIT_BEFORE_FORGET_GRID_MIN
                                        * WmsxProviderImpl.MIN_TO_SEC
                                        * WmsxProviderImpl.SEC_TO_MS);
                    } catch (final InterruptedException e) {
                        // ignore
                    }
                    synchronized (WmsxProviderImpl.this) {
                        if (WmsxProviderImpl.this.pendingJobFactories.isEmpty()
                                && JobWatcher.getInstance().getNumJobsRunning() == 0) {
                            WmsxProviderImpl.this.currentBackend
                                    .forgetPassword();
                        }
                    }
                }
            }).start();
        }
        while (!this.pendingJobFactories.isEmpty()
                && this.maxJobs - JobWatcher.getInstance().getNumJobsRunning() > 0) {
            final JobFactory jf = this.pendingJobFactories.remove(0);
            this.reallySubmitFactory(jf);
            try {
                this.wait(WmsxProviderImpl.WAIT_TIME_BETWEEN_SUBMISSIONS);
            } catch (final InterruptedException e) {
                // Ignore
            }
        }
    }

    /** {@inheritDoc} */
    public void done(final JobUid id, final boolean success) {
        this.investigateLater();
        synchronized (this.workDir) {
            this.appendURILine(id, new File(this.workDir,
                    WmsxProviderImpl.JOBIDS_DONE));
            this.removeURILine(id, new File(this.workDir,
                    WmsxProviderImpl.JOBIDS_RUNNING));
            if (!success) {
                this.appendURILine(id, new File(this.workDir,
                        WmsxProviderImpl.JOBIDS_FAILED));
            }

        }
    }

    /** {@inheritDoc} */
    public void running(final JobUid id) {
        // ignore
    }

    /** {@inheritDoc} */
    public void startup(final JobUid id) {
        // ignore
    }

    /** {@inheritDoc} */
    public void ping() throws RemoteException {
        // do nothing.
    }

    /**
     * Add a new JobFactory.
     * 
     * @param f
     *            JobFactory to add.
     */
    public void addJobFactory(final JobFactory f) {
        synchronized (this) {
            this.pendingJobFactories.add(f);
        }
        this.investigateLater();
    }

    /** {@inheritDoc} */
    public synchronized void submitLaszlo(
            final List<IRemoteWmsxProvider.LaszloCommand> commands,
            final boolean interactive, final String prefix, final String name) {
        WmsxProviderImpl.LOGGER
                .info("Adding " + commands.size() + " Commands.");
        final List<JobFactory> jobs = new Vector<JobFactory>(commands.size());
        final Iterator<LaszloCommand> it = commands.iterator();
        int line = 1;

        final File tmpDir;
        final File outputDir;
        if (name == null) {
            tmpDir = this.debugDir;
            outputDir = this.outDir;
        } else {
            tmpDir = this.syncableDir(this.debugDir, name);
            outputDir = this.syncableDir(this.outDir, name);
        }

        while (it.hasNext()) {
            final IRemoteWmsxProvider.LaszloCommand lcmd = it.next();
            jobs.add(new LaszloJobFactory(lcmd.getCommand(), lcmd.getArgs(),
                    outputDir, tmpDir, line, interactive, prefix, name,
                    this.currentBackend));
            line++;
            // final String cmd = lcmd.getCommand();
            // final String args = lcmd.getArgs();
            // WmsxProviderImpl.LOGGER.info("C: " + cmd + ", A:" + args);

        }
        synchronized (this) {
            this.pendingJobFactories.addAll(jobs);
        }
        this.investigateLater();
    }

    private void investigateLater() {
        new Thread(this).start();
    }

    /** {@inheritDoc} */
    public void run() {
        this.investigateNumJobs();
    }

    /** {@inheritDoc} */
    public synchronized void forgetAfs() throws RemoteException {
        if (this.afsRenewer != null) {
            WmsxProviderImpl.LOGGER.info("Forgetting AFS Password");
            this.afsRenewer.shutdown();
            this.afsRenewer = null;
        }

    }

    /** {@inheritDoc} */
    public synchronized boolean rememberAfs(final String password)
            throws RemoteException {
        WmsxProviderImpl.LOGGER.info("New AFS Remeberer");
        this.forgetAfs();
        this.afsRenewer = new AFS(password);
        final boolean success = RenewerUtil.startupRenewer(this.afsRenewer);
        if (!success) {
            WmsxProviderImpl.LOGGER.info("AFS Password failed");
            this.afsRenewer = null;
        }
        return success;
    }

    /** {@inheritDoc} */
    public synchronized boolean rememberGrid(final String password)
            throws RemoteException {
        return this.currentBackend.provideCredentials(password, this.vo);
    }

    /** {@inheritDoc} */
    public synchronized void setVo(final String newVo) {
        this.vo = newVo;
        if (newVo == null) {
            WmsxProviderImpl.LOGGER.info("VO unset");
        } else {
            WmsxProviderImpl.LOGGER.info("VO is now: " + newVo);
        }
    }

    /** {@inheritDoc} */
    public synchronized void setBackend(final String newBackend) {
        WmsxProviderImpl.LOGGER.info("Setting backend to: " + newBackend);
        final Backend newBack = Backends.getInstance().get(
                newBackend.toLowerCase(Locale.ENGLISH));
        if (newBack == null) {
            WmsxProviderImpl.LOGGER.warning("Unsupported backend: "
                    + newBackend);
        } else {
            this.currentBackend = newBack;
        }
    }

    /** {@inheritDoc} */
    public void shutdownWorkers() {
        ControllerServer.getInstance().shutdownWorkers();
    }

    /** {@inheritDoc} */
    public Iterable<String> listBackends() throws RemoteException {
        return new ArrayList<String>(Backends.getInstance().listBackends());
    }

    /** {@inheritDoc} */
    public void cancelJob(final TransportJobUID jobId) throws RemoteException {
        final JobUid juid = this.getJobUidForTransportUid(jobId);
        if (juid == null) {
            WmsxProviderImpl.LOGGER.info("Could not identify job to cancel: "
                    + jobId);
        } else {
            WmsxProviderImpl.LOGGER.info("Cancelling " + juid);
            new Thread(new Runnable() {
                public void run() {
                    juid.getBackend().cancelJob(juid);
                }
            }).start();
        }
    }

    private JobUid getJobUidForTransportUid(final TransportJobUID jobId) {
        JobUid retVal = null;
        final Backend b = Backends.getInstance().get(jobId.getBackend());
        if (b != null) {
            retVal = b.getJobUidForBackendId(jobId.getLocalId());
        }
        return retVal;
    }

    /** {@inheritDoc} */
    public JobInfo getJobInfo(final TransportJobUID jobId)
            throws RemoteException {
        return JobWatcher.getInstance().getInfoForJob(jobId);
    }

    /** {@inheritDoc} */
    public Iterable<TransportJobUID> listJobs() throws RemoteException {
        return JobWatcher.getInstance().getActiveJobs();
    }

    /** {@inheritDoc} */
    public Lease registerEventListener(final RemoteEventListener r)
            throws RemoteException {
        final Lease l = LandlordImpl.getInstance().getLeaseFactory().newLease(
                UuidFactory.generate(), Lease.ANY);
        JobWatcher.getInstance().addGenericListener(
                new JobChangeEventEmitter(r));
        return l;
    }

    /** {@inheritDoc} */
    public void shutdownWorker(final Uuid workerId) throws RemoteException {
        ControllerServer.getInstance().shutdownWorker(workerId);
    }

}
