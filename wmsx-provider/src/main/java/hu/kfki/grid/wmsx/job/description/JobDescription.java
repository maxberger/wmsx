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

    static final String EXECUTABLE = "Executable";

    static final String JOBTYPE = "JobType";

    static final String INPUTSANDBOX = "InputSandBox";

    static final String OUTPUTSANDBOX = "OutputSandBox";

    static final String ARGUMENTS = "Arguments";

    static final String STDOUTPUT = "StdOutput";

    static final String STDERROR = "StdError";

    static final String RESULTDIR = "ResultDir";

    static final String POSTEXEC = "PostExec";

    static final String PREEXEC = "PreExec";

    static final String CHAIN = "Chain";

    static final String OUTPUTDIRECTORY = "OutputDirectory";

    static final String INTERACTIVE = "Interactive";

    static final String SOFTWARE = "Software";

    static final String AFS = "AFS";

    static final String ARCHIVE = "Archive";

    static final String PROGRAMDIRECTORY = "ProgramDirectory";

    static final String REQUIREMENTS = "Requirements";

    String getStringEntry(final String key, final String defaultValue);

    String getStringEntry(final String key);

    List<String> getListEntry(final String key);

    String toJDL();

    void removeEntry(String entry);

    void replaceEntry(String entry, String value);

    File getBaseDir();

}
