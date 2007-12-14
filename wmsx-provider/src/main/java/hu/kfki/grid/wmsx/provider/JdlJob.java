/**
 * 
 */
package hu.kfki.grid.wmsx.provider;

import hu.kfki.grid.wmsx.job.description.JDLJobDescription;
import hu.kfki.grid.wmsx.job.description.JobDescription;
import hu.kfki.grid.wmsx.workflow.Workflow;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.logging.Logger;

public class JdlJob {
    private final String jdlFile;

    private final String output;

    private String result;

    private String preexec;

    private String postexec;

    private String chain;

    private String command;

    private String[] args;

    private String prefix;

    private String name;

    private Workflow workflow;

    private static final Logger LOGGER = Logger.getLogger(JdlJob.class
            .toString());

    /**
     * @return the command
     */
    public String getCommand() {
        return this.command;
    }

    /**
     * @param command
     *            the command to set
     */
    public void setCommand(final String command) {
        this.command = command;
    }

    /**
     * @return the args
     */
    public String[] getArgs() {
        return this.args;
    }

    /**
     * @param args
     *            the args to set
     */
    public void setArgs(final String[] args) {
        this.args = args;
    }

    public JdlJob(final String jdlFile, final String output,
            final String resultDir, final Workflow wf) {
        this.output = output;
        this.result = resultDir;
        this.workflow = wf;
        this.jdlFile = this.filterJdlFile(jdlFile);
    }

    private String filterJdlFile(final String jdlFileToFilter) {
        String retVal;
        boolean isFiltered = false;
        try {
            final JobDescription job = new JDLJobDescription(jdlFileToFilter);

            final String resDir = job.getStringEntry(JobDescription.RESULTDIR);
            if (resDir != null) {
                final File resFile = new File(resDir);
                if (resFile.isAbsolute()) {
                    this.result = resFile.getAbsolutePath();
                } else {
                    this.result = new File(new File(jdlFileToFilter)
                            .getAbsoluteFile().getParentFile(), resDir)
                            .getAbsolutePath();
                }
                job.removeEntry(JobDescription.RESULTDIR);
                isFiltered = true;
            }
            final String jobType = job.getStringEntry(JobDescription.JOBTYPE);
            if ("workflow".equalsIgnoreCase(jobType)) {
                final File jdlFileFile = new File(jdlFileToFilter)
                        .getAbsoluteFile();
                if (this.workflow == null) {
                    this.workflow = new Workflow(jdlFileFile.getParentFile());
                }
                final String name = jdlFileFile.getName();
                this.setName(name);
                this.workflow.setNextNodes(name, job.getListEntry("Next"));
                job.replaceEntry(JobDescription.JOBTYPE, "normal");
                job.removeEntry("Next");
                job.removeEntry("Prev");
                isFiltered = true;
            }

            if (isFiltered) {
                final File dir = new File(jdlFileToFilter).getAbsoluteFile()
                        .getParentFile();
                final File tmp = File.createTempFile("jdl", null, dir);
                final Writer w = new FileWriter(tmp);
                w.write(job.toJDL());
                w.close();
                tmp.deleteOnExit();
                retVal = tmp.getAbsolutePath();
            } else {
                retVal = jdlFileToFilter;
            }
        } catch (final IOException e) {
            JdlJob.LOGGER.warning(e.getMessage());
            retVal = jdlFileToFilter;
        }
        return retVal;
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

    public String getJdlFile() {
        return this.jdlFile;
    }

    public String getOutput() {
        return this.output;
    }

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
     * @param preexec
     *            the preexec to set
     */
    public void setPreexec(final String preexec) {
        this.preexec = preexec;
    }

    /**
     * @param postexec
     *            the postexec to set
     */
    public void setPostexec(final String postexec) {
        this.postexec = postexec;
    }

    /**
     * @param chain
     *            the chain to set
     */
    public void setChain(final String chain) {
        this.chain = chain;
    }

    public final String getPrefix() {
        return this.prefix;
    }

    public final void setPrefix(final String nprefix) {
        this.prefix = nprefix;
    }

    public final String getName() {
        return this.name;
    }

    public final void setName(final String name) {
        this.name = name;
    }

}
