package hu.kfki.grid.wmsx.job;

public interface JobListener {

	void startup();

	void running();

	void done();
}
