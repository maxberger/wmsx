/*
 * WMSX - Workload Management Extensions for gLite
 * 
 * Copyright (C) 2007-2009 Max Berger
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

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import condor.classad.ClassAdParser;
import condor.classad.Constant;
import condor.classad.Expr;
import condor.classad.ListExpr;
import condor.classad.RecordExpr;

/**
 * JobDescription backed by a JDL file.
 * 
 * @version $Date$
 */
public class JDLJobDescription extends AbstractJobDescription {

    private RecordExpr erecord;

    private File origin;

    private File baseDir;

    private boolean changed;

    /**
     * Creates a new JDLJobDescription.
     * 
     * @param jdlFile
     *            file to load.
     * @throws IOException
     *             if the file cannot be loaded.
     */
    public JDLJobDescription(final File jdlFile) throws IOException {
        this.origin = jdlFile.getCanonicalFile();
        this.baseDir = this.origin.getParentFile();
        this.changed = false;
        final Reader reader = new NoHashReader(new FileReader(jdlFile));
        final ClassAdParser parser = new ClassAdParser(reader);
        final Expr e = parser.parse();
        reader.close();
        if (e instanceof RecordExpr) {
            this.erecord = (RecordExpr) e;
        } else {
            throw new IOException("Error Reading JDL file: " + jdlFile);
        }
    }

    /** {@inheritDoc} */
    public String getStringEntry(final String key) {
        final Expr eval = this.erecord.lookup(key);
        String retVal;
        if (eval instanceof Constant) {
            final Constant econst = (Constant) eval;
            try {
                retVal = econst.stringValue();
            } catch (final ArithmeticException ae) {
                retVal = econst.toString();
            }
        } else if (eval != null) {
            retVal = eval.toString();
        } else {
            retVal = null;
        }
        return retVal;
    }

    /** {@inheritDoc} */
    public List<String> getListEntry(final String key) {
        final List<String> theList = new ArrayList<String>();
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

    /** {@inheritDoc} */
    public File toJdl() throws IOException {
        final File jdl;
        if (this.changed) {
            Writer w = null;
            try {
                jdl = File.createTempFile("jdl", null, this.baseDir);
                w = new FileWriter(jdl);
                w.write(this.toString());
            } finally {
                if (w != null) {
                    w.close();
                }
            }
            jdl.deleteOnExit();
            this.changed = false;
            this.origin = jdl;
        } else {
            jdl = this.origin;
        }
        return jdl;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return this.erecord.toString();
    }

    /** {@inheritDoc} */
    public void removeEntry(final String entry) {
        this.changed = true;
        try {
            this.erecord.removeAttribute(entry);
        } catch (final IllegalArgumentException e) {
            // ignore
        }
    }

    /** {@inheritDoc} */
    public void replaceEntry(final String entry, final String value) {
        this.changed = true;
        this.removeEntry(entry);
        this.erecord.insertAttribute(entry, Constant.getInstance(value));
    }

    /** {@inheritDoc} */
    public File getBaseDir() {
        return this.baseDir;
    }

    /** {@inheritDoc} */
    public String getName() {
        return this.origin.getName();
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public JobDescription clone() throws CloneNotSupportedException {
        final JDLJobDescription newDesc = (JDLJobDescription) super.clone();
        newDesc.origin = this.origin;
        newDesc.changed = true;
        newDesc.erecord = new RecordExpr();
        newDesc.baseDir = this.baseDir;
        final Iterator<String> it = this.erecord.attributes();
        while (it.hasNext()) {
            final String attr = it.next();
            final Expr value = this.erecord.lookup(attr);
            newDesc.erecord.insertAttribute(attr, value);
        }
        return newDesc;
    }

}
