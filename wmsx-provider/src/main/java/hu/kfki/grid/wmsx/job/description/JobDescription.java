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

package hu.kfki.grid.wmsx.job.description;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Describes a Work job.
 * 
 * @version $Revision$
 */
public interface JobDescription extends Cloneable {

    /** Main executable. */
    String EXECUTABLE = "Executable";

    /** Job Type (Normal, Interactive). */
    String JOBTYPE = "JobType";

    /** Files for input sandbox (list). */
    String INPUTSANDBOX = "InputSandBox";

    /** Files for output sandbox (list). */
    String OUTPUTSANDBOX = "OutputSandBox";

    /** Command line arguments (string). */
    String ARGUMENTS = "Arguments";

    /** File for std-out (string). */
    String STDOUTPUT = "StdOutput";

    /** File for std-err (string). */
    String STDERROR = "StdError";

    /** Directory for results (string). */
    String RESULTDIR = "ResultDir";

    /** post exec script (string). */
    String POSTEXEC = "PostExec";

    /** pre exec script (string). */
    String PREEXEC = "PreExec";

    /** chain script (string). */
    String CHAIN = "Chain";

    /** output directory within arglist app (string). */
    String OUTPUTDIRECTORY = "OutputDirectory";

    /** Jobtype interactive. */
    String INTERACTIVE = "Interactive";

    /** software requirements within arglist app (list). */
    String SOFTWARE = "Software";

    /** AFS requirements within software list. */
    String AFS = "AFS";

    /** archive name within arglist app (string). */
    String ARCHIVE = "Archive";

    /** program directory within arglist app (string). */
    String PROGRAMDIRECTORY = "ProgramDirectory";

    /** resource requirements. */
    String REQUIREMENTS = "Requirements";

    /**
     * Next nodes in workflows. WMSX addition.
     */
    String NEXT = "Next";

    /**
     * Previous nodes in workflows. WMSX addition.
     */
    String PREV = "Prev";

    /**
     * Executable for deployment. WMSX addition.
     */
    String DEPLOY = "Deploy";

    /**
     * Id of the workflow. Used internally for Worker Task.
     */
    String WORKFLOWID = "WorkflowId";

    /**
     * Retrieve value for a String entry.
     * 
     * @param key
     *            Key to retrieve
     * @param defaultValue
     *            value to use if unset.
     * @return value for this entry the default value.
     */
    String getStringEntry(final String key, final String defaultValue);

    /**
     * Retrieve value for a String entry.
     * 
     * @param key
     *            Key to retrieve
     * @return value for this entry or null if unset.
     */
    String getStringEntry(final String key);

    /**
     * Retrieve a list entry.
     * 
     * @param key
     *            Key to retrieve
     * @return a list of Strings, an empty list if unset.
     */
    List<String> getListEntry(final String key);

    /**
     * @return a JDL representation of this Job.
     * @throws IOException
     *             if the JDL file cannot be created.
     */
    File toJdl() throws IOException;

    /**
     * Removes a given entry.
     * 
     * @param entry
     *            entry to remove.
     */
    void removeEntry(String entry);

    /**
     * Set a given entry, replacing old values.
     * 
     * @param entry
     *            entry to set
     * @param value
     *            new value
     */
    void replaceEntry(String entry, String value);

    /**
     * @return Base directory for this job. All paths are relative to this
     *         directory.
     */
    File getBaseDir();

    /**
     * @return a name for this job.
     */
    String getName();

    /**
     * @return a clone of this JobDescription.
     * @throws CloneNotSupportedException
     *             if this JobDescrition does not support cloning.
     */
    JobDescription clone() throws CloneNotSupportedException;
}
