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

package hu.kfki.grid.wmsx.job;

public final class JobState {

    private JobState() {
    };

    public static final JobState STARTUP = new JobState();

    public static final JobState RUNNING = new JobState();

    public static final JobState SUCCESS = new JobState();

    public static final JobState FAILED = new JobState();

    public static final JobState NONE = new JobState();

    @Override
    public String toString() {
        final String result;
        if (JobState.STARTUP.equals(this)) {
            result = "STARTUP";
        } else if (JobState.RUNNING.equals(this)) {
            result = "RUNNING";
        } else if (JobState.SUCCESS.equals(this)) {
            result = "SUCCESS";
        } else if (JobState.FAILED.equals(this)) {
            result = "FAILED";
        } else if (JobState.NONE.equals(this)) {
            result = "NONE";
        } else {
            result = super.toString();
        }
        return result;
    }

}
