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

package at.ac.uibk.dps.wmsx.gat;

import hu.kfki.grid.wmsx.backends.Backend;
import hu.kfki.grid.wmsx.backends.DelayedExecution;
import hu.kfki.grid.wmsx.backends.JobUid;
import hu.kfki.grid.wmsx.backends.SubmissionResults;
import hu.kfki.grid.wmsx.job.JobState;
import hu.kfki.grid.wmsx.job.JobWatcher;
import hu.kfki.grid.wmsx.job.description.JobDescription;
import hu.kfki.grid.wmsx.util.FileUtil;
import hu.kfki.grid.wmsx.util.LogUtil;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.gridlab.gat.GAT;
import org.gridlab.gat.GATInvocationException;
import org.gridlab.gat.GATObjectCreationException;
import org.gridlab.gat.URI;
import org.gridlab.gat.monitoring.Metric;
import org.gridlab.gat.monitoring.MetricDefinition;
import org.gridlab.gat.monitoring.MetricEvent;
import org.gridlab.gat.monitoring.MetricListener;
import org.gridlab.gat.resources.HardwareResourceDescription;
import org.gridlab.gat.resources.Job;
import org.gridlab.gat.resources.ResourceBroker;
import org.gridlab.gat.resources.ResourceDescription;
import org.gridlab.gat.resources.SoftwareDescription;

/**
 * gLite back-end based on GAT.
 * 
 * @version $Date$
 */
// CHECKSTYLE:OFF
public class GatBackend implements Backend, MetricListener {
    private static final String GLITE_JOB_ID = "adaptor.job.id";

    // CHECKSTYLE:ON
    private static final Logger LOGGER = Logger.getLogger(GatBackend.class
            .toString());

    private ResourceBroker broker;

    private final Map<Job, File> tempDir = new ConcurrentHashMap<Job, File>();

    private final Map<String, JobUid> jobForId = new ConcurrentHashMap<String, JobUid>();

    private final GatCommon gatCommon = GatCommon.getInstance();

    /**
     * Default constructor.
     */
    public GatBackend() {
        // nothing to do.
    }

    private void ensureBroker() throws IOException {
        if (this.broker == null) {
            try {
                this.broker = GAT.createResourceBroker(this.gatCommon
                        .getGatContext(), new URI("ldap:///"));
            } catch (final GATObjectCreationException e) {
                GatBackend.LOGGER.warning(LogUtil.logException(e));
                throw new IOException("Failed to load broker");
            } catch (final URISyntaxException e) {
                GatBackend.LOGGER.warning(LogUtil.logException(e));
                throw new IOException("Failed to construct broker URI");
            }
        }
    }

    /** {@inheritDoc} */
    public JobState getState(final JobUid uid) {
        final Job job = (Job) uid.getBackendId();
        final Job.JobState istate = job.getState();
        final JobState state;
        switch (istate) {
        case INITIAL:
        case SCHEDULED:
        case PRE_STAGING:
        case ON_HOLD:
            state = JobState.STARTUP;
            break;
        case RUNNING:
            state = JobState.RUNNING;
            break;
        case STOPPED:
        case POST_STAGING:
            state = JobState.SUCCESS;
            break;
        case SUBMISSION_ERROR:
            state = JobState.FAILED;
            break;
        case UNKNOWN:
        default:
            state = JobState.NONE;
        }
        return state;
    }

    /** {@inheritDoc} */
    public String jobUidToUri(final JobUid uid) {
        final Job job = (Job) uid.getBackendId();
        try {
            return (String) job.getInfo().get(GatBackend.GLITE_JOB_ID);
        } catch (final GATInvocationException e) {
            GatBackend.LOGGER.warning(LogUtil.logException(e));
            return null;
        }
    }

    /** {@inheritDoc} */
    public void retrieveLog(final JobUid id, final File dir) {
        // TODO
    }

    private static class GatDelayedExecution implements DelayedExecution {

        private static final int POST_STAGE_SLEEP_TIME = 3000;

        private final Job job;

        private final File source;

        private final File dest;

        public GatDelayedExecution(final Job j, final File s, final File d) {
            this.job = j;
            this.source = s;
            this.dest = d;
        }

        public void waitFor() {
            if (this.source == null) {
                return;
            }

            while (this.job.getState() == Job.JobState.POST_STAGING) {
                try {
                    Thread
                            .sleep(GatBackend.GatDelayedExecution.POST_STAGE_SLEEP_TIME);
                } catch (final InterruptedException ie) {
                    // ignore
                }
            }

            try {
                FileUtil.copyList(Arrays.asList(this.source.list()),
                        this.source, this.dest);
                FileUtil.cleanDir(this.source, false);
            } catch (final IOException e) {
                GatBackend.LOGGER.warning(LogUtil.logException(e));
            }
        }
    }

    /** {@inheritDoc} */
    public DelayedExecution retrieveResult(final JobUid id, final File dir) {
        final Job job = (Job) id.getBackendId();
        return new GatDelayedExecution(job, this.tempDir.remove(job), dir);
    }

    /** {@inheritDoc} */
    public SubmissionResults submitJob(final JobDescription job, final String vo)
            throws IOException {

        this.ensureBroker();
        final File targetDir = FileUtil.createTempDir();
        final SoftwareDescription swDescription = this.createSwDescription(job,
                targetDir);

        final ResourceDescription hwrDescription = this
                .createHwDescription(job);
        final org.gridlab.gat.resources.JobDescription jobDescription = new org.gridlab.gat.resources.JobDescription(
                swDescription, hwrDescription);

        try {
            final Job jobResult = this.broker.submitJob(jobDescription);
            final JobUid jobUid = new JobUid(this, jobResult);
            this.jobForId.put((String) jobResult.getInfo().get(
                    GatBackend.GLITE_JOB_ID), jobUid);
            final MetricDefinition md = jobResult
                    .getMetricDefinitionByName("job.status");
            final Metric polledMetric = new Metric(md, null);
            jobResult.addMetricListener(this, polledMetric);
            this.tempDir.put(jobResult, targetDir);
            return new SubmissionResults(jobUid);
        } catch (final GATInvocationException e) {
            GatBackend.LOGGER.warning(LogUtil.logException(e));
            FileUtil.cleanDir(targetDir, false);
            throw new IOException(e.getMessage());
        }

    }

    private ResourceDescription createHwDescription(final JobDescription job) {
        final Map<String, Object> hwrAttrib = new HashMap<String, Object>();

        final String requirements = job
                .getStringEntry(JobDescription.REQUIREMENTS);
        if (requirements != null) {
            hwrAttrib.put("glite.other", requirements);
        }

        // hwrAttrib.put("memory.size", 1.0f);
        final ResourceDescription hwrDescription = new HardwareResourceDescription(
                hwrAttrib);
        return hwrDescription;
    }

    private SoftwareDescription createSwDescription(final JobDescription job,
            final File targetDir) throws IOException {
        final SoftwareDescription swDescription = new SoftwareDescription();
        swDescription.setExecutable(job
                .getStringEntry(JobDescription.EXECUTABLE));

        final String argumentStr = job.getStringEntry(JobDescription.ARGUMENTS);
        if (argumentStr != null) {
            final String[] arguments = argumentStr.split(" ");
            swDescription.setArguments(arguments);
        } else {
            swDescription.setArguments("");
        }
        final File sourceDir = job.getBaseDir();
        for (final String fileName : job
                .getListEntry(JobDescription.INPUTSANDBOX)) {
            swDescription.addPreStagedFile(this.createGatFile(sourceDir,
                    fileName, false));
        }

        final String stdOut = job.getStringEntry(JobDescription.STDOUTPUT);
        if (stdOut != null) {
            swDescription
                    .setStdout(this.createGatFile(targetDir, stdOut, true));
        }
        final String stdErr = job.getStringEntry(JobDescription.STDERROR);
        if (stdErr != null) {
            swDescription
                    .setStderr(this.createGatFile(targetDir, stdErr, true));
        }
        for (final String fileName : job
                .getListEntry(JobDescription.OUTPUTSANDBOX)) {
            final org.gridlab.gat.io.File gatFile = this.createGatFile(
                    targetDir, fileName, true);
            swDescription.addPostStagedFile(gatFile, gatFile);
        }
        final String retryCount = job.getStringEntry(JobDescription.RETRYCOUNT);
        if (retryCount != null) {
            swDescription.addAttribute("glite.retryCount", retryCount);
        }
        return swDescription;
    }

    private org.gridlab.gat.io.File createGatFile(final File baseDir,
            final String fileName, final boolean removeFilePath)
            throws IOException {
        try {
            final File inputFile;
            if (removeFilePath) {
                inputFile = new File(baseDir, new File(fileName).getName());
            } else {
                inputFile = FileUtil.resolveFile(baseDir, fileName);
            }
            final org.gridlab.gat.io.File inputFileG = GAT.createFile(
                    this.gatCommon.getGatContext(), new URI("file:///"
                            + inputFile.getCanonicalPath()));
            return inputFileG;
        } catch (final URISyntaxException e) {
            GatBackend.LOGGER.warning(LogUtil.logException(e));
            throw new IOException(e.getMessage());
        } catch (final GATObjectCreationException e) {
            GatBackend.LOGGER.warning(LogUtil.logException(e));
            throw new IOException(e.getMessage());
        }
    }

    /** {@inheritDoc} */
    public boolean supportsDeploy() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "Gat";
    }

    /** {@inheritDoc} */
    public void processMetricEvent(final MetricEvent val) {
        try {
            final Job job = (Job) val.getSource();
            final Map<String, Object> info = job.getInfo();
            final String jid = (String) info.get(GatBackend.GLITE_JOB_ID);
            final JobUid juid = this.jobForId.get(jid);
            JobWatcher.getInstance().checkWithState(juid, this.getState(juid));
        } catch (final ClassCastException e) {
            GatBackend.LOGGER.warning(LogUtil.logException(e));
        } catch (final NullPointerException e) {
            GatBackend.LOGGER.warning(LogUtil.logException(e));
        } catch (final GATInvocationException e) {
            GatBackend.LOGGER.warning(LogUtil.logException(e));
        }
    }

    /** {@inheritDoc} */
    public void forgetPassword() {
        this.gatCommon.setPassword("");
    }

    /** {@inheritDoc} */
    public boolean provideCredentials(final String pass, final String vo) {
        this.gatCommon.setPassword(pass);
        this.gatCommon.setVo(vo);
        try {
            this.ensureBroker();
        } catch (final IOException r) {
            GatBackend.LOGGER.warning(LogUtil.logException(r));
            return false;
        }
        return true;
    }

    /** {@inheritDoc} */
    public boolean isAvailable() {
        return this.gatCommon.isAvailable("ResourceBroker",
                "GliteResourceBrokerAdaptor");
    }

    /** {@inheritDoc} */
    public void cancelJob(final JobUid id) {
        final Job job = (Job) id.getBackendId();
        try {
            job.stop();
        } catch (final GATInvocationException e) {
            GatBackend.LOGGER.warning(LogUtil.logException(e));
        }
    }
}
