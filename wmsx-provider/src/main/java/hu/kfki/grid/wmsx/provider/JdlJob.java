/**
 * 
 */
package hu.kfki.grid.wmsx.provider;

class JdlJob implements JobDesc {
    final String jdlFile;

    final String output;

    final String result;

    public JdlJob(final String jdlFile, final String output,
            final String resultDir) {
        this.jdlFile = jdlFile;
        this.output = output;
        this.result = resultDir;
    }

    /*
     * (non-Javadoc)
     * 
     * @see hu.kfki.grid.wmsx.provider.JobDesc#getJdlFile()
     */
    public String getJdlFile() {
        return this.jdlFile;
    }

    /*
     * (non-Javadoc)
     * 
     * @see hu.kfki.grid.wmsx.provider.JobDesc#getOutput()
     */
    public String getOutput() {
        return this.output;
    }

    public String getResultDir() {
        return this.result;
    }
}
