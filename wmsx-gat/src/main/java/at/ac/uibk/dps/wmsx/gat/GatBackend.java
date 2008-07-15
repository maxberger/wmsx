/*
 * WMSX - Workload Management Extensions for gLite
 * 
 * Copyright (C) 2007-2008 Max Berger
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
import hu.kfki.grid.wmsx.backends.JobUid;
import hu.kfki.grid.wmsx.backends.SubmissionResults;
import hu.kfki.grid.wmsx.job.JobState;
import hu.kfki.grid.wmsx.job.description.JobDescription;
import hu.kfki.grid.wmsx.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.gridlab.gat.GAT;
import org.gridlab.gat.GATContext;
import org.gridlab.gat.GATInvocationException;
import org.gridlab.gat.GATObjectCreationException;
import org.gridlab.gat.Preferences;
import org.gridlab.gat.URI;
import org.gridlab.gat.resources.HardwareResourceDescription;
import org.gridlab.gat.resources.Job;
import org.gridlab.gat.resources.ResourceBroker;
import org.gridlab.gat.resources.ResourceDescription;
import org.gridlab.gat.resources.SoftwareDescription;
import org.gridlab.gat.security.CertificateSecurityContext;

/**
 * gLite backend based on GAT.
 * 
 * @version $Revision$
 */
public class GatBackend implements Backend {
    private static final Logger LOGGER = Logger.getLogger(GatBackend.class
            .toString());
    private static final String BACKEND_NAME = "Glite";

    private final GATContext context;
    private final ResourceBroker broker;

    /**
     * Default constructor.
     */
    public GatBackend() {
        this.context = new GATContext();
        final Preferences globalPrefs = new Preferences();

        globalPrefs.put("ResourceBroker.adaptor.name", GatBackend.BACKEND_NAME);
        globalPrefs.put("AdvertService.adaptor.name", GatBackend.BACKEND_NAME);

        // e.g. /home/tom/workspace/thomas/lib/adaptors
        System.setProperty("gat.adaptor.path",
                GliteTestsConstants.GAT_ADAPTOR_PATH);

        // System.setProperty("gat.debug", "true");
        System.setProperty("gat.verbose", "true");

        /** ************************************************* */
        /** Information necessary for VOMS proxy creation ** */
        /** ************************************************* */
        // globalPrefs.put("VirtualOrganisation", "compchem");
        // globalPrefs.put("vomsServerURL", "voms.cnaf.infn.it");
        // globalPrefs.put("vomsServerPort", "15003");
        // globalPrefs.put("vomsHostDN",
        // "/C=IT/O=INFN/OU=Host/L=CNAF/CN=voms.cnaf.infn.it");
        globalPrefs.put("VirtualOrganisation", "voce");
        globalPrefs.put("vomsServerURL", "skurut19.cesnet.cz");
        globalPrefs.put("vomsServerPort", "7001");
        globalPrefs.put("vomsHostDN",
                "/DC=cz/DC=cesnet-ca/O=CESNET/CN=skurut19.cesnet.cz");

        CertificateSecurityContext secContext;
        try {
            secContext = new CertificateSecurityContext(new URI(
                    GliteTestsConstants.X509_KEY_PATH), new URI(
                    GliteTestsConstants.X509_CERT_PATH),
                    GliteTestsConstants.X509_KEY_PASSWORD);
            this.context.addSecurityContext(secContext);
        } catch (final URISyntaxException e) {
            throw new RuntimeException(e);
        }

        this.context.addPreferences(globalPrefs);
        try {
            this.broker = GAT
                    .createResourceBroker(
                            this.context,
                            new URI(
                                    "https://skurut67-6.cesnet.cz:7443/glite_wms_wmproxy_server"));
        } catch (final GATObjectCreationException e) {
            throw new RuntimeException(e);
        } catch (final URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }

    /** {@inheritDoc} */
    public JobState getState(final JobUid uid) {
        final Job job = (Job) uid.getBackendId();
        final int istate = job.getState();
        final JobState state;
        switch (istate) {
        case Job.INITIAL:
        case Job.SCHEDULED:
        case Job.PRE_STAGING:
        case Job.ON_HOLD:
            state = JobState.STARTUP;
            break;
        case Job.RUNNING:
            state = JobState.RUNNING;
            break;
        case Job.STOPPED:
        case Job.POST_STAGING:
            state = JobState.SUCCESS;
            break;
        case Job.SUBMISSION_ERROR:
            state = JobState.FAILED;
            break;
        case Job.UNKNOWN:
        default:
            state = JobState.NONE;
        }
        return state;
    }

    /** {@inheritDoc} */
    public boolean jobIdIsURI() {
        // TODO: Should be true!
        return false;
    }

    /** {@inheritDoc} */
    public void retrieveLog(final JobUid id, final File dir) {
        // TODO
    }

    /** {@inheritDoc} */
    public Process retrieveResult(final JobUid id, final File dir) {
        // TODO
        return null;
    }

    /** {@inheritDoc} */
    public SubmissionResults submitJob(final JobDescription job, final String vo)
            throws IOException {

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

        final File baseDir = job.getBaseDir();
        for (final String fileName : job
                .getListEntry(JobDescription.INPUTSANDBOX)) {
            swDescription.addPreStagedFile(this
                    .createGatFile(baseDir, fileName));
        }
        swDescription.setStdout(this.createGatFile(baseDir, job
                .getStringEntry(JobDescription.STDOUTPUT)));
        swDescription.setStdout(this.createGatFile(baseDir, job
                .getStringEntry(JobDescription.STDERROR)));
        for (final String fileName : job
                .getListEntry(JobDescription.OUTPUTSANDBOX)) {
            swDescription.addPostStagedFile(this.createGatFile(baseDir,
                    fileName));
        }

        final Map<String, Object> hwrAttrib = new HashMap<String, Object>();
        // hwrAttrib.put("memory.size", 1.0f);
        final ResourceDescription hwrDescription = new HardwareResourceDescription(
                hwrAttrib);
        final org.gridlab.gat.resources.JobDescription jobDescription = new org.gridlab.gat.resources.JobDescription(
                swDescription, hwrDescription);

        try {
            final Job jobResult = this.broker.submitJob(jobDescription);
            return new SubmissionResults(new JobUid(this, jobResult));
        } catch (final GATInvocationException e) {
            throw new IOException(e.getMessage());
        }

    }

    private org.gridlab.gat.io.File createGatFile(final File baseDir,
            final String fileName) throws IOException {
        try {
            final File inputFile = FileUtil.resolveFile(baseDir, fileName);
            final org.gridlab.gat.io.File inputFileG = GAT.createFile(
                    this.context, new URI("file:"
                            + inputFile.getCanonicalPath()));
            return inputFileG;
        } catch (final URISyntaxException e) {
            throw new IOException(e.getMessage());
        } catch (final GATObjectCreationException e) {
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
}
