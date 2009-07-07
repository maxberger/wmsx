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

import hu.kfki.grid.wmsx.backends.Backend;
import hu.kfki.grid.wmsx.job.description.JobDescription;
import hu.kfki.grid.wmsx.workflow.Workflow;
import hu.kfki.grid.wmsx.workflow.WorkflowFactory;

import java.io.File;
import java.util.logging.Logger;

/**
 * Describes a single Job based on a JDL job description.
 * 
 * @version $Date$
 */
public class JdlJob {

    private static final Logger LOGGER = Logger.getLogger(JdlJob.class
            .toString());

    private final JobDescription jobDesc;

    private String output;

    private String result;

    private String jobIdFilenameInResultDir;

    private String preexec;

    private String postexec;

    private String chain;

    private String command;

    private String[] args;

    private String prefix;

    private String name;

    private Workflow workflow;

    private final int appId;

    private final Backend back;

    /**
     * Default constructor.
     * 
     * @param job
     *            Job Description
     * @param theOutput
     *            Where to write Stdout to (file)
     * @param resultDir
     *            Where to collect the results to (dir)
     * @param wf
     *            Workflow, if this is part of one, or null.
     * @param backend
     *            Backend for this submission.
     * @param applicationId
     *            application id or 0.
     */
    public JdlJob(final JobDescription job, final String theOutput,
            final String resultDir, final Workflow wf, final Backend backend,
            final int applicationId) {
        this.output = theOutput;
        this.result = resultDir;
        this.workflow = wf;
        this.args = new String[0];
        this.appId = applicationId;
        this.back = backend;
        this.filterJdlFile(job);
        this.jobDesc = job;
    }

    /**
     * @return the command
     */
    public String getCommand() {
        return this.command;
    }

    /**
     * @param newCommand
     *            the command to set
     */
    public void setCommand(final String newCommand) {
        this.command = newCommand;
    }

    /**
     * @return the args
     */
    public String[] getArgs() {
        return this.args.clone();
    }

    /**
     * @param argss
     *            the args to set
     */
    public void setArgs(final String[] argss) {
        this.args = argss.clone();
    }

    private void filterJdlFile(final JobDescription job) {

        final File jdlFileDir = job.getBaseDir();

        this.filterResultDir(job, jdlFileDir);
        this.filterChainCommands(job, jdlFileDir);
        this.makeStdoutAbsolute(job, jdlFileDir);
        this.filterWorkflow(job);
        this.filterDeploy(job);

    }

    private void filterDeploy(final JobDescription job) {
        final String deploy = job.getStringEntry(JobDescription.DEPLOY);
        if (deploy != null) {
            if (!this.back.supportsDeploy()) {
                JdlJob.LOGGER.warning(this.back
                        + " does not yet support Deploy!");
                // TODO: Create Yet-Another-Wrapper
            }
        }
    }

    private void filterWorkflow(final JobDescription job) {
        final String jobType = job.getStringEntry(JobDescription.JOBTYPE);
        if ("workflow".equalsIgnoreCase(jobType)) {

            if (this.workflow == null) {
                this.workflow = WorkflowFactory.getInstance().createWorkflow(
                        job.getBaseDir(), this.back, this.appId);
            }
            final String newname = job.getName();
            this.setName(newname);
            if (this.command == null) {
                this.command = newname;
            }
            this.workflow.setNextNodes(this.name, job
                    .getListEntry(JobDescription.NEXT));
            job.replaceEntry(JobDescription.JOBTYPE, "normal");
            job.removeEntry(JobDescription.NEXT);
            job.removeEntry(JobDescription.PREV);
            job.replaceEntry(JobDescription.WORKFLOWID, Integer
                    .toString(this.workflow.getApplicationId()));
        }
    }

    private void filterResultDir(final JobDescription job, final File jdlFileDir) {
        final String resultDir = this.filterJobAndMakePathAbsolute(jdlFileDir,
                job, JobDescription.RESULTDIR);
        if (resultDir != null) {
            this.result = resultDir;
        }

        final String jobIdInResultDir = job
                .getStringEntry(JobDescription.JOBIDFILENAMEINRESULTDIR);
        job.removeEntry(JobDescription.JOBIDFILENAMEINRESULTDIR);
        if (jobIdInResultDir != null) {
            this.jobIdFilenameInResultDir = jobIdInResultDir;
        }
    }

    private void filterChainCommands(final JobDescription job,
            final File jdlFileDir) {
        final String postExec = this.filterJobAndMakePathAbsolute(jdlFileDir,
                job, JobDescription.POSTEXEC);
        if (postExec != null) {
            this.postexec = postExec;
        }

        final String preExec = this.filterJobAndMakePathAbsolute(jdlFileDir,
                job, JobDescription.PREEXEC);
        if (preExec != null) {
            this.preexec = preExec;
        }

        final String chainn = this.filterJobAndMakePathAbsolute(jdlFileDir,
                job, JobDescription.CHAIN);
        if (chainn != null) {
            this.chain = chainn;
        }
    }

    private void makeStdoutAbsolute(final JobDescription job,
            final File jdlFileDir) {
        final String outp = job.getStringEntry(JobDescription.STDOUTPUT);
        if (outp != null) {
            if (new File(outp).isAbsolute()) {
                this.output = outp;
            } else {
                this.makeStdoutAbsoluteFromRelavitePath(jdlFileDir, outp);
            }
        }
    }

    private void makeStdoutAbsoluteFromRelavitePath(final File jdlFileDir,
            final String outp) {
        final File parent;
        if (this.result != null) {
            parent = new File(this.result);
        } else {
            parent = jdlFileDir;
        }
        this.output = new File(parent, outp).getAbsolutePath();
    }

    private String filterJobAndMakePathAbsolute(final File jdlFileDir,
            final JobDescription job, final String which) {
        final String res;
        final String resDir = job.getStringEntry(which);
        if (resDir != null) {
            final File resFile = new File(resDir);
            if (resFile.isAbsolute()) {
                res = resFile.getAbsolutePath();
            } else {
                res = new File(jdlFileDir, resDir).getAbsolutePath();
            }
            job.removeEntry(which);
        } else {
            res = null;
        }
        return res;
    }

    /**
     * @return the preexec
     */
    public String getPreexec() {
        return this.preexec;
    }

    /**
     * @return the postexec
     */
    public String getPostexec() {
        return this.postexec;
    }

    /**
     * @return the postexec
     */
    public Workflow getWorkflow() {
        return this.workflow;
    }

    /**
     * @return the Job Description.
     */
    public JobDescription getJobDescription() {
        return this.jobDesc;
    }

    /**
     * @return the name of the stdout file.
     */
    public String getOutput() {
        return this.output;
    }

    /**
     * @return directory where the results are retrieved to.
     */
    public String getResultDir() {
        return this.result;
    }

    /**
     * @return The filename for the JobID in the result directory.
     */
    public String getJobIdFilenameInResultDir() {
        return this.jobIdFilenameInResultDir;
    }

    /**
     * @return the chain
     */
    public String getChain() {
        return this.chain;
    }

    /**
     * @param newPreexec
     *            the preexec to set
     */
    public void setPreexec(final String newPreexec) {
        this.preexec = newPreexec;
    }

    /**
     * @param newPostexec
     *            the postexec to set
     */
    public void setPostexec(final String newPostexec) {
        this.postexec = newPostexec;
    }

    /**
     * @param newChain
     *            the chain to set
     */
    public void setChain(final String newChain) {
        this.chain = newChain;
    }

    /**
     * @return prefix to distinguish between the same jobs
     */
    public final String getPrefix() {
        return this.prefix;
    }

    /**
     * @param nprefix
     *            prefix to distinguish between the same jobs.
     */
    public final void setPrefix(final String nprefix) {
        this.prefix = nprefix;
    }

    /**
     * @return name of this activity.
     */
    public final String getName() {
        return this.name;
    }

    /**
     * @param newName
     *            name of this activity.
     */
    public final void setName(final String newName) {
        this.name = newName;
    }

    /**
     * @param fileName
     *            the filename where to store the jobId in the result directory.
     */
    public final void setJobIdFilenameInResultDir(final String fileName) {
        this.jobIdFilenameInResultDir = fileName;
    }

    /**
     * @return the Backend in use
     */
    public Backend getBackend() {
        return this.back;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return this.jobDesc.getName();
    }

}
