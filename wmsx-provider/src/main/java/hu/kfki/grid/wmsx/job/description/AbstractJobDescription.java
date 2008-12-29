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

package hu.kfki.grid.wmsx.job.description;

/**
 * Implements generic JobDescription tasks by calling other functions.
 * 
 * @version $Revision$
 */
public abstract class AbstractJobDescription implements JobDescription {

    /** {@inheritDoc} */
    public String getStringEntry(final String key, final String defaultValue) {
        final String value = this.getStringEntry(key);
        if (value == null) {
            return defaultValue;
        } else {
            return this.unquote(value);
        }

    }

    private String unquote(final String value) {
        final String retVal;
        if (value.length() < 2) {
            retVal = value;
        } else if (value.charAt(0) == '"'
                && value.charAt(value.length() - 1) == '"') {
            retVal = value.substring(1, value.length() - 1);
        } else {
            retVal = value;
        }
        return retVal;
    }

    /** {@inheritDoc} */
    @Override
    public JobDescription clone() throws CloneNotSupportedException {
        return (JobDescription) super.clone();
    }

}
