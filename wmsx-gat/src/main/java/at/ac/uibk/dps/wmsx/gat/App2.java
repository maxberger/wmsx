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
import hu.kfki.grid.wmsx.job.JobState;
import hu.kfki.grid.wmsx.job.description.JDLJobDescription;
import hu.kfki.grid.wmsx.job.description.JobDescription;

import java.io.File;
import java.io.IOException;

/**
 * @version $Revision$
 */
public final class App2 {

    private App2() {
    };

    /**
     * @param args
     */
    public static void main(final String[] args) {
        try {
            final JobDescription j = new JDLJobDescription(new File(
                    "/home/berger/tmp/simplejob/simplejob.jdl"));
            final Backend b = new GatBackend();
            final JobUid juid = b.submitJob(j, null).getJobId();

            while (true) {
                try {
                    Thread.sleep(30 * 1000);
                } catch (final InterruptedException io) {
                    // ignore
                }
                final JobState s = b.getState(juid);
                System.out.println(s.toString());
                if (JobState.FAILED.equals(s) || JobState.SUCCESS.equals(s)
                        || JobState.NONE.equals(s)) {
                    break;
                }
            }
        } catch (final IOException io) {
            io.printStackTrace();
        }
    }

}
