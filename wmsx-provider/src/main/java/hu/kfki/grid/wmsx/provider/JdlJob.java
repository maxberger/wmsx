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
 * 
 */

/* $Id$ */

package hu.kfki.grid.wmsx.provider;

import hu.kfki.grid.wmsx.backends.Backend;
import hu.kfki.grid.wmsx.job.description.JDLJobDescription;
import hu.kfki.grid.wmsx.job.description.JobDescription;
import hu.kfki.grid.wmsx.workflow.Workflow;
import hu.kfki.grid.wmsx.workflow.WorkflowFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.logging.Logger;

/**
 * Describes a singe Job based on a JDL job description.
 * 
 * @version $Revision$
 */
public class JdlJob {

    private static final Logger LOGGER = Logger.getLogger(JdlJob.class
            .toString());

    private final String jdlFile;

    private String output;

    private String result;

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
     * @param theJdlFile
     *            actual JDL file
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
    public JdlJob(final String theJdlFile, final String theOutput,
            final String resultDir, final Workflow wf, final Backend backend,
            final int applicationId) {
        this.output = theOutput;
        this.result = resultDir;
        this.workflow = wf;
        this.args = new String[0];
        this.appId = applicationId;
        this.back = backend;
        this.jdlFile = this.filterJdlFile(theJdlFile);
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

    private String filterJdlFile(final String jdlFileToFilter) {
        String retVal;
        boolean isFiltered = false;
        try {
            final JobDescription job = new JDLJobDescription(jdlFileToFilter);

            final File jdlFileDir = new File(jdlFileToFilter).getAbsoluteFile()
                    .getParentFile();

            isFiltered = this.filterResultDir(isFiltered, job, jdlFileDir);
            isFiltered = this.filterChainCommands(isFiltered, job, jdlFileDir);
            this.makeStdoutAbsolute(job, jdlFileDir);
            isFiltered = this.filterWorkflow(jdlFileToFilter, isFiltered, job);
            isFiltered |= this.filterDeploy(job);

            if (isFiltered) {
                retVal = this.createFilteredJdl(jdlFileToFilter, job);
            } else {
                retVal = jdlFileToFilter;
            }
        } catch (final IOException e) {
            JdlJob.LOGGER.warning(e.getMessage());
            retVal = jdlFileToFilter;
        }
        return retVal;
    }

    private boolean filterDeploy(final JobDescription job) {
        final String deploy = job.getStringEntry(JobDescription.DEPLOY);
        if (deploy != null) {
            if (!this.back.supportsDeploy()) {
                JdlJob.LOGGER.warning(this.back
                        + " does not yet support Deploy!");
                // TODO: Create Yet-Another-Wrapper
            }
        }
        return false;
    }

    private String createFilteredJdl(final String jdlFileToFilter,
            final JobDescription job) throws IOException {
        final File dir = new File(jdlFileToFilter).getAbsoluteFile()
                .getParentFile();
        final File tmp = File.createTempFile("jdl", null, dir);
        final Writer w = new FileWriter(tmp);
        w.write(job.toJDL());
        w.close();
        tmp.deleteOnExit();
        return tmp.getAbsolutePath();
    }

    private boolean filterWorkflow(final String jdlFileToFilter,
            final boolean isFiltered, final JobDescription job) {
        final String jobType = job.getStringEntry(JobDescription.JOBTYPE);
        if ("workflow".equalsIgnoreCase(jobType)) {
            final File jdlFileFile = new File(jdlFileToFilter)
                    .getAbsoluteFile();
            if (this.workflow == null) {
                this.workflow = WorkflowFactory.getInstance().createWorkflow(
                        jdlFileFile.getParentFile(), this.back, this.appId);
            }
            final String newname = jdlFileFile.getName();
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
            return true;
        }
        return isFiltered;
    }

    private boolean filterResultDir(final boolean isFiltered,
            final JobDescription job, final File jdlFileDir) {
        final String resultDir = this.filterJob(jdlFileDir, job,
                JobDescription.RESULTDIR);
        if (resultDir != null) {
            this.result = resultDir;
            return true;
        }
        return isFiltered;
    }

    private boolean filterChainCommands(final boolean isFiltered,
            final JobDescription job, final File jdlFileDir) {
        boolean isF = isFiltered;
        final String postExec = this.filterJob(jdlFileDir, job,
                JobDescription.POSTEXEC);
        if (postExec != null) {
            this.postexec = postExec;
            isF = true;
        }

        final String preExec = this.filterJob(jdlFileDir, job,
                JobDescription.PREEXEC);
        if (preExec != null) {
            this.preexec = preExec;
            isF = true;
        }

        final String chainn = this.filterJob(jdlFileDir, job,
                JobDescription.CHAIN);
        if (chainn != null) {
            this.chain = chainn;
            isF = true;
        }
        return isF;
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

    private String filterJob(final File jdlFileDir, final JobDescription job,
            final String which) {
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
     * @return the Jdl File.
     */
    public String getJdlFile() {
        return this.jdlFile;
    }

    /**
     * @return the name of the stdout file.
     */
    public String getOutput() {
        return this.output;
    }

    /**
     * @return direcotry where the results are retrieved to.
     */
    public String getResultDir() {
        return this.result;
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
     * @return the Backend in use
     */
    public Backend getBackend() {
        return this.back;
    }

}
