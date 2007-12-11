package hu.kfki.grid.wmsx.backends;

import java.util.List;
import java.util.Vector;

public class EDGBackend implements Backend {

    EDGBackend() {
    }

    public List jobOutputCommand(String absolutePath, String idString) {
        final List commandLine = new Vector();
        commandLine.add("/opt/edg/bin/edg-job-get-output");
        commandLine.add("--dir");
        commandLine.add(absolutePath);
        commandLine.add("--noint");
        commandLine.add(idString);
        return commandLine;
    }

    public List submitJdl(String jdlFile, String vo) {
        final List commandLine =
            new Vector();
        commandLine.add("/opt/edg/bin/edg-job-submit");
        commandLine.add("--nolisten");
        if (vo != null) {
            commandLine.add("--vo");
            commandLine.add(vo);
        }
        commandLine.add(jdlFile);
        return commandLine;
    }

}
