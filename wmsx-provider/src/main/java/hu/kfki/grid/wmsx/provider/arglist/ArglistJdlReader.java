package hu.kfki.grid.wmsx.provider.arglist;

import hu.kfki.grid.wmsx.job.description.EmptyJobDescription;
import hu.kfki.grid.wmsx.job.description.JDLJobDescription;
import hu.kfki.grid.wmsx.job.description.JobDescription;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

public class ArglistJdlReader {

    private final String executable;

    private final String outputDir;

    private final List software;

    private final boolean afs;

    private final boolean interactive;

    private final String archive;

    private final String programDir;

    private final String requirements;

    private static final Logger LOGGER = Logger
            .getLogger(ArglistJdlReader.class.toString());

    public ArglistJdlReader(final String jdlFile, final String cmdName) {

        JobDescription desc;
        try {
            desc = new JDLJobDescription(jdlFile);
        } catch (final IOException io) {
            ArglistJdlReader.LOGGER.warning("Error Reading JDL file: "
                    + io.getMessage() + ", assuming defaults");
            desc = new EmptyJobDescription();
        }

        this.executable = desc.getStringEntry(JobDescription.EXECUTABLE,
                cmdName);
        this.outputDir = desc.getStringEntry("OutputDirectory", "out");
        final String jobType = desc.getStringEntry(JobDescription.JOBTYPE);
        this.interactive = "Interactive".equalsIgnoreCase(jobType);
        this.software = desc.getListEntry("Software");
        this.afs = this.software.remove("AFS");
        this.archive = desc.getStringEntry("Archive", cmdName + ".tar.gz");
        this.programDir = desc.getStringEntry("ProgramDir", cmdName);
        this.requirements = desc.getStringEntry("Requirements");
    }

    public String getExecutable() {
        return this.executable;
    }

    public String getOutputDirectory() {
        return this.outputDir;
    }

    public List getSoftware() {
        return this.software;
    }

    public boolean getAfs() {
        return this.afs;
    }

    public boolean getInteractive() {
        return this.interactive;
    }

    /**
     * @return the archive
     */
    public String getArchive() {
        return this.archive;
    }

    /**
     * @return the programDir
     */
    public String getProgramDir() {
        return this.programDir;
    }

    /**
     * @return the requirements
     */
    public String getRequirements() {
        return this.requirements;
    }
}
