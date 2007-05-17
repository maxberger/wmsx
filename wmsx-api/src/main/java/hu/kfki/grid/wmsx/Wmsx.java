package hu.kfki.grid.wmsx;

import java.io.FileNotFoundException;

/**
 * My Jini Service Interface!
 * 
 */
public interface Wmsx {

	String hello();

	void submitJdl(String jdlFile) throws FileNotFoundException;
}