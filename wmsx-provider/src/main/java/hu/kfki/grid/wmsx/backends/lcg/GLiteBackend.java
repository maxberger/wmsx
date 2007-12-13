package hu.kfki.grid.wmsx.backends.lcg;

import java.util.List;
import java.util.Vector;

public class GLiteBackend extends AbstractLCGBackend {

    private static GLiteBackend instance;

    private GLiteBackend() {
    }

    public static synchronized GLiteBackend getInstance() {
        if (GLiteBackend.instance == null) {
            GLiteBackend.instance = new GLiteBackend();
        }
        return GLiteBackend.instance;
    }

    public List jobOutputCommand(final String absolutePath,
            final String idString) {
        final List commandLine = new Vector();
        commandLine.add("/opt/glite/bin/glite-job-output");
        commandLine.add("--dir");
        commandLine.add(absolutePath);
        commandLine.add("--noint");
        commandLine.add(idString);
        return commandLine;
    }

    public List submitJdlCommand(final String jdlFile, final String vo) {
        final List commandLine = new Vector();
        commandLine.add("/opt/glite/bin/glite-job-submit");
        commandLine.add("--nolisten");
        if (vo != null) {
            commandLine.add("--vo");
            commandLine.add(vo);
        }
        commandLine.add(jdlFile);
        return commandLine;
    }

    public String toString() {
        return "GLite";
    }
}
