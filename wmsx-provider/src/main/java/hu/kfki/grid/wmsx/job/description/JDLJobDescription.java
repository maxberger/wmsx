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
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import condor.classad.ClassAdParser;
import condor.classad.Constant;
import condor.classad.Expr;
import condor.classad.ListExpr;
import condor.classad.RecordExpr;

public class JDLJobDescription extends AbstractJobDescription {

    private final RecordExpr erecord;

    final File baseDir;

    public JDLJobDescription(final String jdlFile) throws IOException {
        this.baseDir = new File(jdlFile).getAbsoluteFile().getParentFile();
        final ClassAdParser parser = new ClassAdParser(new NoHashReader(
                new FileReader(jdlFile)));
        final Expr e = parser.parse();
        if (e instanceof RecordExpr) {
            this.erecord = (RecordExpr) e;
        } else {
            throw new IOException("Error Reading JDL file: " + jdlFile);
        }
    }

    public String getStringEntry(final String key) {
        final Expr eval = this.erecord.lookup(key);
        if (eval instanceof Constant) {
            final Constant econst = (Constant) eval;
            return econst.stringValue();
        } else if (eval != null) {
            return eval.toString();
        }
        return null;
    }

    public List<String> getListEntry(final String key) {
        final List<String> theList = new Vector<String>();
        final Expr eval = this.erecord.lookup(key);
        if (eval instanceof ListExpr) {
            final ListExpr elist = (ListExpr) eval;
            final int len = elist.size();
            for (int i = 0; i < len; i++) {
                final Expr sub = elist.sub(i);
                if (sub instanceof Constant) {
                    final Constant subconst = (Constant) sub;
                    theList.add(subconst.stringValue());
                } else {
                    theList.add(sub.toString());
                }
            }
        }
        return theList;
    }

    public String toJDL() {
        return this.erecord.toString();
    }

    @Override
    public String toString() {
        return this.toJDL();
    }

    public void removeEntry(final String entry) {
        try {
            this.erecord.removeAttribute(entry);
        } catch (final IllegalArgumentException e) {
            // ignore
        }
    }

    public void replaceEntry(final String entry, final String value) {
        this.removeEntry(entry);
        this.erecord.insertAttribute(entry, Constant.getInstance(value));
    }

    public File getBaseDir() {
        return this.baseDir;
    }

}
