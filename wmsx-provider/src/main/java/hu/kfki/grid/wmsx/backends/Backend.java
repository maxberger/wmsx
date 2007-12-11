package hu.kfki.grid.wmsx.backends;

import java.util.List;

public interface Backend {

    List jobOutputCommand(String absolutePath, String string);
    
    List submitJdl(String jdlFile, String vo);

}
