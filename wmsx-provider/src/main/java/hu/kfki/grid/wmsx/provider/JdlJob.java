/**
 * 
 */
package hu.kfki.grid.wmsx.provider;

class JdlJob implements JobDesc {
    final String jdlFile;

    final String output;

    public JdlJob(final String jdlFile, final String output) {
        this.jdlFile = jdlFile;
        this.output = output;
    }

    /* (non-Javadoc)
     * @see hu.kfki.grid.wmsx.provider.JobDesc#getJdlFile()
     */
    public String getJdlFile() {
        return this.jdlFile;
    }

    /* (non-Javadoc)
     * @see hu.kfki.grid.wmsx.provider.JobDesc#getOutput()
     */
    public String getOutput() {
        return this.output;
    }
}