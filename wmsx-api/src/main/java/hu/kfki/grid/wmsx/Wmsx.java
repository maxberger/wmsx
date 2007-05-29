package hu.kfki.grid.wmsx;

import java.io.IOException;

/**
 * My Jini Service Interface!
 * 
 */
public interface Wmsx {

	boolean ping(boolean remote);
	
	String submitJdl(String jdlFile, String outputFile) throws IOException;

	void setMaxJobs(int maxJobs);

}