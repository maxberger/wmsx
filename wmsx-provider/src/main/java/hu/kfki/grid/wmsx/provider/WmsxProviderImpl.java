package hu.kfki.grid.wmsx.provider;

import hu.kfki.grid.renewer.AFS;
import hu.kfki.grid.renewer.Renewer;
import hu.kfki.grid.renewer.VOMS;
import hu.kfki.grid.wmsx.job.JobListener;
import hu.kfki.grid.wmsx.job.JobWatcher;
import hu.kfki.grid.wmsx.job.LogListener;
import hu.kfki.grid.wmsx.job.result.ResultListener;
import hu.kfki.grid.wmsx.job.shadow.ShadowListener;
import hu.kfki.grid.wmsx.job.submit.ParseResult;
import hu.kfki.grid.wmsx.job.submit.Submitter;
import hu.kfki.grid.wmsx.provider.arglist.LaszloJobFactory;
import hu.kfki.grid.wmsx.provider.scripts.ScriptLauncher;

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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import com.sun.jini.admin.DestroyAdmin;

import edg.workload.userinterface.jclient.JobId;

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

    private int maxJobs = 100;

    private final List pendingJobFactories = new LinkedList();

    private static WmsxProviderImpl instance;

    private Renewer afsRenewer;

    private Renewer gridRenewer;

    public WmsxProviderImpl(final DestroyAdmin dadm, final File workdir) {
        this.destroyAdmin = dadm;
        this.workDir = workdir;

        this.outDir = new File(workdir, "out");
        if (!this.outDir.exists()) {
            this.outDir.mkdirs();
        }
        this.debugDir = new File(workdir, "debug");
        if (!this.debugDir.exists()) {
            this.debugDir.mkdirs();
        }
        WmsxProviderImpl.instance = this;
    }

    public static WmsxProviderImpl getInstance() {
        return WmsxProviderImpl.instance;
    }

    synchronized public String submitJdl(final String jdlFile,
            final String output, final String resultDir) {
        final int current = JobWatcher.getWatcher().getNumJobsRunning();
        final int avail = this.maxJobs - current;
        if (avail > 0) {
            return this.reallySubmitJdl(new JdlJobFactory(jdlFile, output,
                    resultDir).createJdlJob());
        } else {
            this.pendingJobFactories.add(new JdlJobFactory(jdlFile, output,
                    resultDir));
            return "pending";
        }
    }

    private String reallySubmitJdl(final JdlJob job) {
        final String jdlFile = job.getJdlFile();
        final String output = job.getOutput();
        final String preexec = job.getPreexec();
        if (preexec != null) {
            WmsxProviderImpl.LOGGER.info("Running " + preexec);

            final List cmdVec = new Vector();
            cmdVec.add(preexec);
            cmdVec.add(job.getCommand());
            cmdVec.addAll(Arrays.asList(job.getArgs()));

            ScriptLauncher.getInstance().launchScript(
                    (String[]) cmdVec.toArray(new String[0]),
                    output + "_preexec");
        }
        WmsxProviderImpl.LOGGER.info("Submitting " + jdlFile);
        ParseResult result;
        try {
            result = Submitter.getSubmitter().submitJdl(jdlFile);
            final String jobStr = result.getJobId();
            final JobId id = new JobId(jobStr);
            WmsxProviderImpl.LOGGER.info("Job id is: " + id);
            JobWatcher.getWatcher().addWatch(id, LogListener.getLogListener());
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
                this.appendLine(jobStr, new File(this.workDir,
                        WmsxProviderImpl.JOBIDS_ALL));
                this.appendLine(jobStr, new File(this.workDir,
                        WmsxProviderImpl.JOBIDS_RUNNING));
            }
            return jobStr;
        } catch (final IOException e) {
            WmsxProviderImpl.LOGGER.warning(e.getMessage());
        } catch (final NullPointerException e) {
            WmsxProviderImpl.LOGGER.warning(e.getMessage() + " at "
                    + e.getStackTrace()[0].toString());
        }
        return null;
    }

    private void removeLine(final String line, final File file) {
        try {
            final List lines = new Vector();
            final BufferedReader in = new BufferedReader(new FileReader(file));
            String inLine = in.readLine();
            while (inLine != null) {
                if (!inLine.equals(line)) {
                    lines.add(inLine);
                }
                inLine = in.readLine();
            }
            in.close();
            final BufferedWriter out = new BufferedWriter(new FileWriter(file,
                    false));
            final Iterator it = lines.iterator();
            while (it.hasNext()) {
                final String outLine = (String) it.next();
                out.write(outLine);
                out.newLine();
            }
            out.close();
        } catch (final IOException e) {
            WmsxProviderImpl.LOGGER.warning(e.getMessage());
        }
    }

    private void appendLine(final String line, final File file) {
        try {
            final BufferedWriter out = new BufferedWriter(new FileWriter(file,
                    true));
            out.write(line);
            out.newLine();
            out.close();
        } catch (final IOException e) {
            WmsxProviderImpl.LOGGER.warning(e.getMessage());
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

    public void setMaxJobs(final int maxj) throws RemoteException {
        WmsxProviderImpl.LOGGER.info("setMaxJobs to " + maxj);
        this.maxJobs = maxj;
        this.investigateLater();
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
            final JobFactory jf = (JobFactory) this.pendingJobFactories
                    .remove(0);
            final JdlJob jd = jf.createJdlJob();
            this.reallySubmitJdl(jd);
            try {
                this.wait(100);
            } catch (final InterruptedException e) {
                // Ignore
            }
        }
    }

    public void done(final JobId id, final boolean success) {
        this.investigateLater();
        synchronized (this.workDir) {
            final String jobStr = id.toString();
            this.appendLine(jobStr, new File(this.workDir,
                    WmsxProviderImpl.JOBIDS_DONE));
            this.removeLine(jobStr, new File(this.workDir,
                    WmsxProviderImpl.JOBIDS_RUNNING));
            if (!success) {
                this.appendLine(jobStr, new File(this.workDir,
                        WmsxProviderImpl.JOBIDS_FAILED));
            }

        }
    }

    public void running(final JobId id) {
        // ignore
    }

    public void startup(final JobId id) {
        // ignore
    }

    public void ping() throws RemoteException {
        // Empty on purpose.
    }

    public synchronized void submitLaszlo(final List commands,
            final boolean interactive, final String prefix) {
        WmsxProviderImpl.LOGGER
                .info("Adding " + commands.size() + " Commands.");
        final List jobs = new Vector(commands.size());
        final Iterator it = commands.iterator();
        int line = 1;
        while (it.hasNext()) {
            final IRemoteWmsxProvider.LaszloCommand lcmd = (IRemoteWmsxProvider.LaszloCommand) it
                    .next();
            jobs.add(new LaszloJobFactory(lcmd.getCommand(), lcmd.getArgs(),
                    this.outDir, this.debugDir, line, interactive, prefix));
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
        this.gridRenewer = new VOMS(password);
        final boolean success = this.startupRenewer(this.gridRenewer);
        if (!success) {
            WmsxProviderImpl.LOGGER.info("Grid Password failed");
            this.gridRenewer = null;
        }
        return success;

    }

}
