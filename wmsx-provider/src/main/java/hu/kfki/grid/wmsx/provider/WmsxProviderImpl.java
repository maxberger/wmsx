package hu.kfki.grid.wmsx.provider;

import hu.kfki.grid.wmsx.job.JobListener;
import hu.kfki.grid.wmsx.job.JobWatcher;
import hu.kfki.grid.wmsx.job.LogListener;
import hu.kfki.grid.wmsx.job.shadow.ShadowListener;
import hu.kfki.grid.wmsx.job.submit.ParseResult;
import hu.kfki.grid.wmsx.job.submit.Submitter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.WritableByteChannel;
import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import com.sun.jini.admin.DestroyAdmin;

import edg.workload.userinterface.jclient.JobId;
import hu.kfki.grid.wmsx.util.Pair;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Iterator;

/**
 * My Jini Service Implementation!
 *
 */
public class WmsxProviderImpl implements IRemoteWmsxProvider, RemoteDestroy,
        JobListener {
    
    private static final long serialVersionUID = 2L;
    
    private static final Logger LOGGER = Logger
            .getLogger (WmsxProviderImpl.class.toString ());
    
    private final DestroyAdmin destroyAdmin;
    
    private final File workDir;
    
    private int maxJobs = Integer.MAX_VALUE;
    
    private final List pendingJobs = new LinkedList ();
    
    public WmsxProviderImpl (final DestroyAdmin dadm, final File workdir) {
        this.destroyAdmin = dadm;
        this.workDir = workdir;
    }
    
    static class JobDesc {
        final String jdlFile;
        
        final String output;
        
        public JobDesc (final String jdlFile, final String output) {
            this.jdlFile = jdlFile;
            this.output = output;
        }
        
        public String getJdlFile () {
            return this.jdlFile;
        }
        
        public String getOutput () {
            return this.output;
        }
    }
    
    synchronized public String submitJdl (final String jdlFile,
            final String output) {
        final int current = JobWatcher.getWatcher ().getNumJobsRunning ();
        final int avail = (this.maxJobs - current);
        if (avail > 0) {
            return this.reallySubmitJdl (jdlFile, output);
        } else {
            this.pendingJobs.add (new JobDesc (jdlFile, output));
            return "pending";
        }
    }
    
    private String reallySubmitJdl (final String jdlFile, final String output) {
        WmsxProviderImpl.LOGGER.info ("Submitting " + jdlFile);
        ParseResult result;
        try {
            result = Submitter.getSubmitter ().submitJdl (jdlFile);
            final String jobStr = result.getJobId ();
            final JobId id = new JobId (jobStr);
            WmsxProviderImpl.LOGGER.info ("Job id is: " + id);
            JobWatcher.getWatcher ().addWatch (id, new LogListener (id));
            JobWatcher.getWatcher ().addWatch (id, this);
            final WritableByteChannel oChannel;
            if (output != null) {
                oChannel = new FileOutputStream (output).getChannel ();
            } else {
                oChannel = null;
            }
            JobWatcher.getWatcher ().addWatch (id,
                    ShadowListener.listen (result, oChannel));
            synchronized (workDir) {
                try {
                    BufferedWriter out = new BufferedWriter (new FileWriter (new File (workDir,"jobids"), true));
                    out.write (jobStr);
                    out.newLine ();
                    out.close ();
                } catch (IOException e) {
                    WmsxProviderImpl.LOGGER.warning (e.getMessage ());
                }
            }
            return jobStr;
        } catch (final IOException e) {
            WmsxProviderImpl.LOGGER.warning (e.getMessage ());
        } catch (final NullPointerException e) {
            WmsxProviderImpl.LOGGER.warning (e.getMessage ());
        }
        return null;
    }
    
    public void destroy () throws RemoteException {
        new Thread (new Runnable () {
            
            public void run () {
                try {
                    JobWatcher.getWatcher ().shutdown ();
                    Thread.sleep (1000);
                    WmsxProviderImpl.this.destroyAdmin.destroy ();
                } catch (final RemoteException e) {
                    // ignore
                } catch (final InterruptedException e) {
                    // ignore
                }
            }
        }).start ();
    }
    
    public void setMaxJobs (final int maxj) throws RemoteException {
        WmsxProviderImpl.LOGGER.info ("setMaxJobs to " + maxj);
        this.maxJobs = maxj;
        this.investigateNumJobs ();
    }
    
    private synchronized void investigateNumJobs () {
        while ((!this.pendingJobs.isEmpty ())
                && ((this.maxJobs - JobWatcher.getWatcher ().getNumJobsRunning ()) > 0)) {
            final JobDesc jd = (JobDesc) this.pendingJobs.remove (0);
            this.reallySubmitJdl (jd.getJdlFile (), jd.getOutput ());
            try {
                this.wait (100);
            } catch (final InterruptedException e) {
                // Ignore
            }
        }
    }
    
    public void done () {
        this.investigateNumJobs ();
    }
    
    public void running () {
        // ignore
    }
    
    public void startup () {
        // ignore
    }
    
    public void ping () throws RemoteException {
        // Empty on purpose.
    }
    
    public void submitLaszlo (final List commands) throws RemoteException {
        LOGGER.info ("Adding "+commands.size ()+" Commands.");
        Iterator it = commands.iterator ();
        while (it.hasNext ()) {
            Pair p = (Pair) it.next ();
            String cmd = (String)p.getKey ();
            String args = (String)p.getValue ();
        }
    }
    
}