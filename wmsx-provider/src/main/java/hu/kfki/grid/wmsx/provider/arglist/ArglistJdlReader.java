package hu.kfki.grid.wmsx.provider.arglist;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import condor.classad.ClassAdParser;
import condor.classad.Constant;
import condor.classad.Expr;
import condor.classad.ListExpr;
import condor.classad.RecordExpr;

public class ArglistJdlReader {

    private String executable;

    private String outputDir;

    private List software;

    private static final Logger LOGGER = Logger
            .getLogger(ArglistJdlReader.class.toString());

    public ArglistJdlReader(final String jdlFile, final String defaultExec) {
        this.executable = defaultExec;
        this.outputDir = "out";
        try {
            final ClassAdParser parser = new ClassAdParser(new FileInputStream(
                    jdlFile));
            final Expr e = parser.parse();
            if (e instanceof RecordExpr) {
                final RecordExpr erecord = (RecordExpr) e;
                final String exec = this.getEntry(erecord, "Executable");
                if (exec != null) {
                    this.executable = exec;
                }
                final String odir = this.getEntry(erecord, "OutputDirectory");
                if (odir != null) {
                    this.outputDir = odir;
                }
                this.software = this.getList(erecord, "Software");
            }
        } catch (final FileNotFoundException e) {
            ArglistJdlReader.LOGGER.fine("No JDL file: " + jdlFile
                    + " assuming defaults");
        }

    }

    private String getEntry(final RecordExpr erecord, final String key) {
        final Expr eval = erecord.lookup(key);
        if (eval instanceof Constant) {
            final Constant econst = (Constant) eval;
            return econst.stringValue();
        }
        return null;
    }

    private List getList(final RecordExpr erecord, final String key) {
        final List theList = new Vector();
        final Expr eval = erecord.lookup(key);
        if (eval instanceof ListExpr) {
            final ListExpr elist = (ListExpr) eval;
            final int len = elist.size();
            for (int i = 0; i < len; i++) {
                final Expr sub = elist.sub(i);
                if (sub instanceof Constant) {
                    final Constant subconst = (Constant) sub;
                    theList.add(subconst.stringValue());
                }
            }
        }
        return theList;
    }

    public String getExecutable() {
        return this.executable;
    }

    public String getOutputDirectory() {
        return this.outputDir;
    }

    public List getSoftware() {
        return this.software;
    }

}
