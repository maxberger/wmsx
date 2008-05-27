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

/* $Id: vasblasd$ */

package hu.kfki.grid.wmsx.job.description;

import java.io.File;
import java.util.List;

public interface JobDescription {

    String EXECUTABLE = "Executable";

    String JOBTYPE = "JobType";

    String INPUTSANDBOX = "InputSandBox";

    String OUTPUTSANDBOX = "OutputSandBox";

    String ARGUMENTS = "Arguments";

    String STDOUTPUT = "StdOutput";

    String STDERROR = "StdError";

    String RESULTDIR = "ResultDir";

    String POSTEXEC = "PostExec";

    String PREEXEC = "PreExec";

    String CHAIN = "Chain";

    String OUTPUTDIRECTORY = "OutputDirectory";

    String INTERACTIVE = "Interactive";

    String SOFTWARE = "Software";

    String AFS = "AFS";

    String ARCHIVE = "Archive";

    String PROGRAMDIRECTORY = "ProgramDirectory";

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

    String getStringEntry(final String key, final String defaultValue);

    String getStringEntry(final String key);

    List<String> getListEntry(final String key);

    String toJDL();

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

    File getBaseDir();

}
