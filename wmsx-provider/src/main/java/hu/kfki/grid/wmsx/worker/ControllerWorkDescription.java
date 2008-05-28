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

package hu.kfki.grid.wmsx.worker;

import hu.kfki.grid.wmsx.job.description.JobDescription;
import hu.kfki.grid.wmsx.util.FileUtil;

import java.util.List;
import java.util.Map;
import java.util.Vector;

public class ControllerWorkDescription {

    private final WorkDescription workDescription;

    public ControllerWorkDescription(final Object uid,
            final JobDescription jobDesc) {
        final Map<String, byte[]> inputSandbox = FileUtil.createSandbox(jobDesc
                .getListEntry(JobDescription.INPUTSANDBOX), jobDesc
                .getBaseDir());

        final List<String> args = new Vector<String>();
        final String aString = jobDesc.getStringEntry(JobDescription.ARGUMENTS,
                "");
        for (final String sa : aString.split(" ")) {
            final String sa2 = sa.trim();
            if (sa2.length() > 0) {
                args.add(sa2);
            }
        }

        this.workDescription = new WorkDescription(uid, inputSandbox, jobDesc
                .getListEntry(JobDescription.OUTPUTSANDBOX), jobDesc
                .getStringEntry(JobDescription.EXECUTABLE), jobDesc
                .getStringEntry(JobDescription.DEPLOY), args, jobDesc
                .getStringEntry(JobDescription.STDOUTPUT), jobDesc
                .getStringEntry(JobDescription.STDERROR), jobDesc
                .getStringEntry(JobDescription.WORKFLOWID));
    }

    public WorkDescription getWorkDescription() {
        return this.workDescription;
    }
}
