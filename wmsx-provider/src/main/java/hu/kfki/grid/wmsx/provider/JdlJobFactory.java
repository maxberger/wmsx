/**
 * 
 */
package hu.kfki.grid.wmsx.provider;

public class JdlJobFactory implements JobFactory {
    private final String jdlFile;

    private final String output;

    private final String result;

    public JdlJobFactory(final String jdlFile, final String output,
            final String resultDir) {
        this.jdlFile = jdlFile;
        this.output = output;
        this.result = resultDir;
    }

    public JdlJob createJdlJob() {
        return new JdlJob(this.jdlFile, this.output, this.result, null);
    }

}
