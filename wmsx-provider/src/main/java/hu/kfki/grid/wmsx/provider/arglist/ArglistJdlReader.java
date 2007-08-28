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

    private final String executable;

    private final String outputDir;

    private final List software;

    private final boolean afs;

    private final boolean interactive;

    private final String archive;

    private final String programDir;

    private final String requirements;

    private static final Logger LOGGER = Logger
            .getLogger(ArglistJdlReader.class.toString());

    public ArglistJdlReader(final String jdlFile, final String cmdName) {

        RecordExpr erecord = new RecordExpr();
        try {
            final ClassAdParser parser = new ClassAdParser(new FileInputStream(
                    jdlFile));
            final Expr e = parser.parse();
            if (e instanceof RecordExpr) {
                erecord = (RecordExpr) e;
            } else {
                ArglistJdlReader.LOGGER.warning("Error Reading JDL file: "
                        + jdlFile);
            }
        } catch (final FileNotFoundException e) {
            ArglistJdlReader.LOGGER.fine("No JDL file: " + jdlFile
                    + " assuming defaults");
        }

        this.executable = this.getEntry(erecord, "Executable", cmdName);
        this.outputDir = this.getEntry(erecord, "OutputDirectory", "out");
        final String jobType = this.getEntry(erecord, "JobType");
        this.interactive = "Interactive".equalsIgnoreCase(jobType);
        this.software = this.getList(erecord, "Software");
        this.afs = this.software.remove("AFS");
        this.archive = this.getEntry(erecord, "Archive", cmdName + ".tar.gz");
        this.programDir = this.getEntry(erecord, "ProgramDir", cmdName);
        this.requirements = this.getEntry(erecord, "Requirements");
    }

    private String getEntry(final RecordExpr erecord, final String key,
            final String defaultValue) {
        final String value = this.getEntry(erecord, key);
        if (value == null) {
            return defaultValue;
        } else {
            return value;
        }

    }

    private String getEntry(final RecordExpr erecord, final String key) {
        final Expr eval = erecord.lookup(key);
        if (eval != null) {
            return eval.toString();
        }
        // if (eval instanceof Constant) {
        // final Constant econst = (Constant) eval;
        // return econst.stringValue();
        // }
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

    public boolean getAfs() {
        return this.afs;
    }

    public boolean getInteractive() {
        return this.interactive;
    }

    /**
     * @return the archive
     */
    public String getArchive() {
        return this.archive;
    }

    /**
     * @return the programDir
     */
    public String getProgramDir() {
        return this.programDir;
    }

    /**
     * @return the requirements
     */
    public String getRequirements() {
        return this.requirements;
    }
}
