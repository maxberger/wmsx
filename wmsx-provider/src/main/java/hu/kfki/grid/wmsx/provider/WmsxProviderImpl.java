package hu.kfki.grid.wmsx.provider;

import hu.kfki.grid.renewer.AFS;
import hu.kfki.grid.renewer.Renewer;
import hu.kfki.grid.renewer.VOMS;
import hu.kfki.grid.wmsx.backends.Backend;
import hu.kfki.grid.wmsx.backends.Backends;
import hu.kfki.grid.wmsx.backends.JobUid;
import hu.kfki.grid.wmsx.backends.SubmissionResults;
import hu.kfki.grid.wmsx.job.JobListener;
import hu.kfki.grid.wmsx.job.JobWatcher;
import hu.kfki.grid.wmsx.job.LogListener;
import hu.kfki.grid.wmsx.job.result.ResultListener;
import hu.kfki.grid.wmsx.job.shadow.ShadowListener;
import hu.kfki.grid.wmsx.provider.arglist.LaszloJobFactory;
import hu.kfki.grid.wmsx.provider.scripts.ScriptLauncher;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import com.sun.jini.admin.DestroyAdmin;

/**
 * My Jini Service Implementation!
 * 
 */
public class WmsxProviderImpl implements IRemoteWmsxProvider, RemoteDestroy,
        JobListener, Runnable {

    private static final String JOBIDS_ALL = "jobids.all";

    private static final String JOBIDS_RUNNING = "jobids.running";

    private static final String JOBIDS_DONE = "jobids.done";

    private static final String JOBIDS_FAILED = "jobids.failed";

    private static final long serialVersionUID = 2L;

    private static final Logger LOGGER = Logger
            .getLogger(WmsxProviderImpl.class.toString());

    private final DestroyAdmin destroyAdmin;

    private final File workDir;

    private final File outDir;

    private final File debugDir;

    private String vo = null;

    private int maxJobs = 100;

    private final List<JobFactory> pendingJobFactories = new LinkedList<JobFactory>();

    private static WmsxProviderImpl instance;

    private Renewer afsRenewer;

    private Renewer gridRenewer;

    private final Map<String, File> dirs = new HashMap<String, File>();

    private Backend backend = Backends.EDG;

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
                    if (!dirFile.exists()) {
                        dirFile.mkdirs();
                    }
                } else {
                    dirFile = existing;
                }
            }
        } catch (final IOException e) {
            WmsxProviderImpl.LOGGER.warning("IOError: " + e.getMessage());
        }
        return dirFile;
    }

    public static WmsxProviderImpl getInstance() {
        return WmsxProviderImpl.instance;
    }

    synchronized public String submitJdl(final String jdlFile,
            final String output, final String resultDir) {
        final int current = JobWatcher.getWatcher().getNumJobsRunning();
        final int avail = this.maxJobs - current;
        final String result;
        if (avail > 0) {
            final JobUid id = this.reallySubmitJdl(new JdlJobFactory(jdlFile,
                    output, resultDir).createJdlJob());
            if (id != null) {
                result = id.getBackendId().toString();
            } else {
                result = "failed";
            }
        } else {
            this.pendingJobFactories.add(new JdlJobFactory(jdlFile, output,
                    resultDir));
            result = "pending";
        }
        return result;
    }

    private JobUid reallySubmitJdl(final JdlJob job) {
        final String jdlFile = job.getJdlFile();
        final String output = job.getOutput();
        final String preexec = job.getPreexec();
        if (preexec != null) {
            WmsxProviderImpl.LOGGER.info("Running " + preexec);

            final List<String> cmdVec = new Vector<String>();
            cmdVec.add(preexec);
            cmdVec.add(job.getCommand());
            cmdVec.add(job.getResultDir());
            cmdVec.addAll(Arrays.asList(job.getArgs()));

            ScriptLauncher.getInstance().launchScript(
                    cmdVec.toArray(new String[0]), output + "_preexec");
        }
        WmsxProviderImpl.LOGGER.info("Submitting " + jdlFile);
        SubmissionResults result;
        try {
            final JobUid id;
            result = this.backend.submitJdl(jdlFile, this.vo);
            if (result != null) {
                id = result.getJobId();
                WmsxProviderImpl.LOGGER.info("Job id is: " + id);
                JobWatcher.getWatcher().addWatch(id,
                        LogListener.getLogListener());
                JobWatcher.getWatcher().addWatch(id, this);
                if (ResultListener.getResultListener().setJob(id, job)) {
                    JobWatcher.getWatcher().addWatch(id,
                            ResultListener.getResultListener());
                }
                final WritableByteChannel oChannel;
                if (output != null && result.getOStream() != null) {
                    new File(output).getParentFile().mkdirs();
                    oChannel = new FileOutputStream(output).getChannel();
                } else {
                    oChannel = null;
                }
                JobWatcher.getWatcher().addWatch(id,
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
            WmsxProviderImpl.LOGGER.warning(e.getMessage());
        } catch (final NullPointerException e) {
            WmsxProviderImpl.LOGGER.warning(e.getMessage() + " at "
                    + e.getStackTrace()[0].toString());
        }
        return null;
    }

    private void removeURILine(final JobUid uid, final File file) {
        if (uid.getBackend().jobIdIsURI()) {
            final String line = uid.getBackendId().toString();
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
                WmsxProviderImpl.LOGGER.warning(e.getMessage());
            }
        }
    }

    private void appendURILine(final JobUid uid, final File file) {
        if (uid.getBackend().jobIdIsURI()) {
            try {
                final BufferedWriter out = new BufferedWriter(new FileWriter(
                        file, true));
                out.write(uid.getBackendId().toString());
                out.newLine();
                out.close();
            } catch (final IOException e) {
                WmsxProviderImpl.LOGGER.warning(e.getMessage());
            }
        }
    }

    public void destroy() throws RemoteException {
        new Thread(new Runnable() {

            public void run() {
                try {
                    JobWatcher.getWatcher().shutdown();
                    Thread.sleep(1000);
                    WmsxProviderImpl.this.destroyAdmin.destroy();
                } catch (final RemoteException e) {
                    // ignore
                } catch (final InterruptedException e) {
                    // ignore
                }
            }
        }).start();
    }

    public void setMaxJobs(final int maxj) {
        WmsxProviderImpl.LOGGER.info("setMaxJobs to " + maxj);
        this.maxJobs = maxj;
        this.investigateLater();
    }

    public void startWorkers(int num) {
        if (num > 50) {
            num = 50;
        }
        for (int i = 0; i < num; i++) {
            ControllerServer.getInstance().submitWorker();
        }
    }

    private synchronized void investigateNumJobs() {
        if (this.pendingJobFactories.isEmpty()
                && JobWatcher.getWatcher().getNumJobsRunning() == 0) {
            new Thread(new Runnable() {

                public void run() {
                    try {
                        Thread.sleep(5 * 60 * 1000);
                    } catch (final InterruptedException e) {
                        // ignore
                    }
                    synchronized (WmsxProviderImpl.this) {
                        if (WmsxProviderImpl.this.pendingJobFactories.isEmpty()
                                && JobWatcher.getWatcher().getNumJobsRunning() == 0) {
                            WmsxProviderImpl.this.forgetGrid();
                        }
                    }
                }
            }).start();

        }
        while (!this.pendingJobFactories.isEmpty()
                && this.maxJobs - JobWatcher.getWatcher().getNumJobsRunning() > 0) {
            final JobFactory jf = this.pendingJobFactories.remove(0);
            final JdlJob jd = jf.createJdlJob();
            this.reallySubmitJdl(jd);
            try {
                this.wait(100);
            } catch (final InterruptedException e) {
                // Ignore
            }
        }
    }

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

    public void running(final JobUid id) {
        // ignore
    }

    public void startup(final JobUid id) {
        // ignore
    }

    public void ping() throws RemoteException {
        // do nothing.
    }

    public void addJobFactory(final JobFactory f) {
        synchronized (this) {
            this.pendingJobFactories.add(f);
        }
        this.investigateLater();
    }

    @SuppressWarnings("unchecked")
    public synchronized void submitLaszlo(final List commands,
            final boolean interactive, final String prefix, final String name) {
        WmsxProviderImpl.LOGGER
                .info("Adding " + commands.size() + " Commands.");
        final List jobs = new Vector(commands.size());
        final Iterator it = commands.iterator();
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
            final IRemoteWmsxProvider.LaszloCommand lcmd = (IRemoteWmsxProvider.LaszloCommand) it
                    .next();
            jobs.add(new LaszloJobFactory(lcmd.getCommand(), lcmd.getArgs(),
                    outputDir, tmpDir, line, interactive, prefix, name));
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

    public void run() {
        this.investigateNumJobs();
    }

    public synchronized void forgetAfs() throws RemoteException {
        if (this.afsRenewer != null) {
            WmsxProviderImpl.LOGGER.info("Forgetting AFS Password");
            this.afsRenewer.shutdown();
            this.afsRenewer = null;
        }

    }

    private synchronized void forgetGrid() {
        if (this.gridRenewer != null) {
            WmsxProviderImpl.LOGGER.info("Forgetting Grid Password");
            this.gridRenewer.shutdown();
            this.gridRenewer = null;
        }
    }

    public synchronized boolean rememberAfs(final String password)
            throws RemoteException {
        WmsxProviderImpl.LOGGER.info("New AFS Remeberer");
        this.forgetAfs();
        this.afsRenewer = new AFS(password);
        final boolean success = this.startupRenewer(this.afsRenewer);
        if (!success) {
            WmsxProviderImpl.LOGGER.info("AFS Password failed");
            this.afsRenewer = null;
        }
        return success;
    }

    private boolean startupRenewer(final Renewer renewer) {
        final boolean success = renewer.renew();
        if (success) {
            final Thread t = new Thread(renewer);
            t.setDaemon(true);
            t.start();
        }
        return success;
    }

    public synchronized boolean rememberGrid(final String password)
            throws RemoteException {
        WmsxProviderImpl.LOGGER.info("New Grid Remeberer");
        this.forgetGrid();
        this.gridRenewer = new VOMS(password, this.vo);
        final boolean success = this.startupRenewer(this.gridRenewer);
        if (!success) {
            WmsxProviderImpl.LOGGER.info("Grid Password failed");
            this.gridRenewer = null;
        }
        return success;

    }

    public void setVo(final String newVo) {
        this.vo = newVo;
        if (newVo == null) {
            WmsxProviderImpl.LOGGER.info("VO unset");
        } else {
            WmsxProviderImpl.LOGGER.info("VO is now: " + newVo);
        }
    }

    public void setBackend(final String newBackend) {
        WmsxProviderImpl.LOGGER.info("Setting backend to: " + newBackend);
        if ("glite".compareToIgnoreCase(newBackend) == 0) {
            this.backend = Backends.GLITE;
        } else if ("edg".compareToIgnoreCase(newBackend) == 0) {
            this.backend = Backends.EDG;
        } else if ("fake".compareToIgnoreCase(newBackend) == 0) {
            this.backend = Backends.FAKE;
        } else if ("local".compareToIgnoreCase(newBackend) == 0) {
            this.backend = Backends.LOCAL;
        } else if ("worker".compareToIgnoreCase(newBackend) == 0) {
            this.backend = Backends.WORKER;
        } else {
            WmsxProviderImpl.LOGGER.warning("Unsupported backend: "
                    + newBackend);
        }

    }

}
