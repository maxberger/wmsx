/**
 * 
 */
package hu.kfki.grid.wmsx.provider;

public class JdlJob {
    private final String jdlFile;

    private final String output;

    private final String result;

    private String preexec;

    private String postexec;

    private String chain;

    private String command;

    private String[] args;

    private String prefix;

    private String name;

    /**
     * @return the command
     */
    public String getCommand() {
        return this.command;
    }

    /**
     * @param command
     *            the command to set
     */
    public void setCommand(final String command) {
        this.command = command;
    }

    /**
     * @return the args
     */
    public String[] getArgs() {
        return this.args;
    }

    /**
     * @param args
     *            the args to set
     */
    public void setArgs(final String[] args) {
        this.args = args;
    }

    public JdlJob(final String jdlFile, final String output,
            final String resultDir) {
        this.jdlFile = jdlFile;
        this.output = output;
        this.result = resultDir;
    }

    /**
     * @return the preexec
     */
    public String getPreexec() {
        return this.preexec;
    }

    /**
     * @return the postexec
     */
    public String getPostexec() {
        return this.postexec;
    }

    public String getJdlFile() {
        return this.jdlFile;
    }

    public String getOutput() {
        return this.output;
    }

    public String getResultDir() {
        return this.result;
    }

    /**
     * @return the chain
     */
    public String getChain() {
        return this.chain;
    }

    /**
     * @param preexec
     *            the preexec to set
     */
    public void setPreexec(final String preexec) {
        this.preexec = preexec;
    }

    /**
     * @param postexec
     *            the postexec to set
     */
    public void setPostexec(final String postexec) {
        this.postexec = postexec;
    }

    /**
     * @param chain
     *            the chain to set
     */
    public void setChain(final String chain) {
        this.chain = chain;
    }

    public final String getPrefix() {
        return prefix;
    }

    public final void setPrefix(final String nprefix) {
        this.prefix = nprefix;
    }

    public final String getName() {
        return this.name;
    }

    public final void setName(final String name) {
        this.name = name;
    }

}
