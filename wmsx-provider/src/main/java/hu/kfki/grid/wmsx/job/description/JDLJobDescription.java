package hu.kfki.grid.wmsx.job.description;

import java.io.FileInputStream;
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

    public JDLJobDescription(final String jdlFile) throws IOException {
        final ClassAdParser parser = new ClassAdParser(new FileInputStream(
                jdlFile));
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

    public List getListEntry(final String key) {
        final List theList = new Vector();
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

}
