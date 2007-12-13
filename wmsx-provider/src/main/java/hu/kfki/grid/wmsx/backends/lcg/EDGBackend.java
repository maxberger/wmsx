package hu.kfki.grid.wmsx.backends.lcg;

import java.util.List;
import java.util.Vector;

public class EDGBackend extends AbstractLCGBackend {

    private static EDGBackend instance;

    private EDGBackend() {
    }

    public static synchronized EDGBackend getInstance() {
        if (EDGBackend.instance == null) {
            EDGBackend.instance = new EDGBackend();
        }
        return EDGBackend.instance;
    }

    public List jobOutputCommand(final String absolutePath,
            final String idString) {
        final List commandLine = new Vector();
        commandLine.add("/opt/edg/bin/edg-job-get-output");
        commandLine.add("--dir");
        commandLine.add(absolutePath);
        commandLine.add("--noint");
        commandLine.add(idString);
        return commandLine;
    }

    public List submitJdlCommand(final String jdlFile, final String vo) {
        final List commandLine = new Vector();
        commandLine.add("/opt/edg/bin/edg-job-submit");
        commandLine.add("--nolisten");
        if (vo != null) {
            commandLine.add("--vo");
            commandLine.add(vo);
        }
        commandLine.add(jdlFile);
        return commandLine;
    }

    public String toString() {
        return "EDG";
    }
}
