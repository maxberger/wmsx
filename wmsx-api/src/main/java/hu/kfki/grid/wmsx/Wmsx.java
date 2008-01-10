package hu.kfki.grid.wmsx;

import java.io.IOException;

/**
 * My Jini Service Interface!
 * 
 */
public interface Wmsx {

    boolean ping(boolean remote);

    String submitJdl(String jdlFile, String outputFile, String resultDir)
            throws IOException;

    void submitLaszlo(String argFile, boolean interactive, String name)
            throws IOException;

    void setMaxJobs(int maxJobs);

    void startWorkers(int number);

    boolean rememberAfs(String password);

    boolean rememberGrid(String password);

    void forgetAfs();

    void setVo(String newVo);

    void setBackend(String backend);
}
