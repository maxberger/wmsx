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

package hu.kfki.grid.wmsx.provider.arglist;

import hu.kfki.grid.wmsx.job.description.EmptyJobDescription;
import hu.kfki.grid.wmsx.job.description.JDLJobDescription;
import hu.kfki.grid.wmsx.job.description.JobDescription;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * Read a wjdl file, which gives additional info for jobs executed through
 * arglist files.
 * 
 * @version $Date$
 */
public class ArglistJdlReader {

    private static final Logger LOGGER = Logger
            .getLogger(ArglistJdlReader.class.toString());

    private final String executable;

    private final String outputDir;

    private final List<String> software;

    private final boolean afs;

    private final boolean interactive;

    private final String archive;

    private final String programDir;

    private final String requirements;

    /**
     * Read an arglist special jdl file (wjdl).
     * 
     * @param jdlFile
     *            path to the wjdl file.
     * @param cmdName
     *            name of the command to execute.
     */
    public ArglistJdlReader(final String jdlFile, final String cmdName) {

        JobDescription desc;
        try {
            desc = new JDLJobDescription(new File(jdlFile));
        } catch (final IOException io) {
            ArglistJdlReader.LOGGER.warning("Error Reading JDL file: "
                    + io.getMessage() + ", assuming defaults");
            desc = new EmptyJobDescription();
        }

        this.executable = desc.getStringEntry(JobDescription.EXECUTABLE,
                cmdName);
        this.outputDir = desc.getStringEntry(JobDescription.OUTPUTDIRECTORY,
                "out");
        final String jobType = desc.getStringEntry(JobDescription.JOBTYPE);
        this.interactive = JobDescription.INTERACTIVE.equalsIgnoreCase(jobType);
        this.software = desc.getListEntry(JobDescription.SOFTWARE);
        this.afs = this.software.remove(JobDescription.AFS);
        this.archive = desc.getStringEntry(JobDescription.ARCHIVE, cmdName
                + ".tar.gz");
        final String oldProgDir = desc.getStringEntry("ProgramDir", cmdName);
        this.programDir = desc.getStringEntry(JobDescription.PROGRAMDIRECTORY,
                oldProgDir);
        this.requirements = desc.getStringEntry(JobDescription.REQUIREMENTS);
    }

    /**
     * @return the executable set in this wjdl.
     */
    public String getExecutable() {
        return this.executable;
    }

    /**
     * @return the output directory set in this wjdl.
     */
    public String getOutputDirectory() {
        return this.outputDir;
    }

    /**
     * @return required software listed in this wjdl.
     */
    public List<String> getSoftware() {
        return this.software;
    }

    /**
     * @return true if AFS is required.
     */
    public boolean getAfs() {
        return this.afs;
    }

    /**
     * @return true if jobs are to be run interactive.
     */
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
