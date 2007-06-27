package hu.kfki.grid.wmsx.provider;

import hu.kfki.grid.wmsx.job.JobListener;
import hu.kfki.grid.wmsx.job.JobWatcher;
import hu.kfki.grid.wmsx.job.LogListener;
import hu.kfki.grid.wmsx.job.result.ResultListener;
import hu.kfki.grid.wmsx.job.shadow.ShadowListener;
import hu.kfki.grid.wmsx.job.submit.ParseResult;
import hu.kfki.grid.wmsx.job.submit.Submitter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.WritableByteChannel;
import java.rmi.RemoteException;
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

    private static final long serialVersionUID = 2L;

    private static final Logger LOGGER = Logger
            .getLogger(WmsxProviderImpl.class.toString());

    private final DestroyAdmin destroyAdmin;

    private final File workDir;

    private final File outDir;

    private final File debugDir;

    private int maxJobs = Integer.MAX_VALUE;

    private final List pendingJobs = new LinkedList();

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
        try {
            final File jobFile = new File(this.debugDir, "job.sh");
            final FileOutputStream fo = new FileOutputStream(jobFile);
            final InputStream in = ClassLoader
                    .getSystemResourceAsStream("job.sh");
            final byte[] b = new byte[4096];
            int read = in.read(b);
            while (read > 0) {
                fo.write(b, 0, read);
                read = in.read(b);
            }
            in.close();
            fo.close();
            Runtime.getRuntime().exec(
                    new String[] { "chmod", "+x", jobFile.getAbsolutePath() });
        } catch (final IOException e) {
            WmsxProviderImpl.LOGGER.warning("Error copying job.sh: "
                    + e.getMessage());
        }
    }

    synchronized public String submitJdl(final String jdlFile,
            final String output, final String resultDir) {
        final int current = JobWatcher.getWatcher().getNumJobsRunning();
        final int avail = (this.maxJobs - current);
        if (avail > 0) {
            return this.reallySubmitJdl(jdlFile, output, resultDir);
        } else {
            this.pendingJobs.add(new JdlJob(jdlFile, output, resultDir));
            return "pending";
        }
    }

    private String reallySubmitJdl(final String jdlFile, final String output,
            final String resultDir) {
        WmsxProviderImpl.LOGGER.info("Submitting " + jdlFile);
        ParseResult result;
        try {
            result = Submitter.getSubmitter().submitJdl(jdlFile);
            final String jobStr = result.getJobId();
            final JobId id = new JobId(jobStr);
            WmsxProviderImpl.LOGGER.info("Job id is: " + id);
            JobWatcher.getWatcher().addWatch(id, LogListener.getLogListener());
            JobWatcher.getWatcher().addWatch(id, this);
            if (ResultListener.getResultListener().setOutputDir(id, resultDir)) {
                JobWatcher.getWatcher().addWatch(id,
                        ResultListener.getResultListener());
            }
            final WritableByteChannel oChannel;
            if (output != null) {
                oChannel = new FileOutputStream(output).getChannel();
            } else {
                oChannel = null;
            }
            JobWatcher.getWatcher().addWatch(id,
                    ShadowListener.listen(result, oChannel));
            synchronized (this.workDir) {
                try {
                    final BufferedWriter out = new BufferedWriter(
                            new FileWriter(new File(this.workDir, "jobids"),
                                    true));
                    out.write(jobStr);
                    out.newLine();
                    out.close();
                } catch (final IOException e) {
                    WmsxProviderImpl.LOGGER.warning(e.getMessage());
                }
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
        while ((!this.pendingJobs.isEmpty())
                && ((this.maxJobs - JobWatcher.getWatcher().getNumJobsRunning()) > 0)) {
            final JobDesc jd = (JobDesc) this.pendingJobs.remove(0);
            this.reallySubmitJdl(jd.getJdlFile(), jd.getOutput(), jd
                    .getResultDir());
            try {
                this.wait(100);
            } catch (final InterruptedException e) {
                // Ignore
            }
        }
    }

    public void done(final JobId id) {
        this.investigateLater();
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

    public void submitLaszlo(final List commands) throws RemoteException {
        WmsxProviderImpl.LOGGER
                .info("Adding " + commands.size() + " Commands.");
        final List jobs = new Vector(commands.size());
        final Iterator it = commands.iterator();
        int line = 1;
        while (it.hasNext()) {
            final IRemoteWmsxProvider.LaszloCommand lcmd = (IRemoteWmsxProvider.LaszloCommand) it
                    .next();
            jobs.add(new LaszloJob(lcmd.getCommand(), lcmd.getArgs(), lcmd
                    .getInputFile(), this.outDir, this.debugDir, line));
            line++;
            // final String cmd = lcmd.getCommand();
            // final String args = lcmd.getArgs();
            // WmsxProviderImpl.LOGGER.info("C: " + cmd + ", A:" + args);

        }
        synchronized (this) {
            this.pendingJobs.addAll(jobs);
        }
        this.investigateLater();
    }

    private void investigateLater() {
        new Thread(this).start();
    }

    public void run() {
        this.investigateNumJobs();
    }

}
