package hu.kfki.grid.wmsx;

/**
 * My Jini Service Interface!
 *
 */
public interface Wmsx {
    
    String hello();
    
    void submitJdl(String jdlFile);
}