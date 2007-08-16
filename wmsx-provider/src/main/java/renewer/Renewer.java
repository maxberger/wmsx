package renewer;

public abstract class Renewer implements Runnable {

    private final String opassword;

    private boolean shutdown;

    public Renewer(final String password) {
        this.shutdown = false;
        this.opassword = this.obfuscate(password);
    }

    private String obfuscate(final String what) {
        final StringBuffer b = new StringBuffer(what.length());
        for (int i = 0; i < what.length(); i++) {
            b.append((char) (what.charAt(i) ^ 0xffff));
        }
        return b.toString();
    }

    private String deobfuscate(final String what) {
        return this.obfuscate(what);
    }

    public void shutdown() {
        this.shutdown = true;
    }

    public boolean renew() {
        final boolean retVal;
        this.preexec();
        retVal = this.exec(this.deobfuscate(this.opassword));
        this.postexec();
        return retVal;
    }

    public void run() {
        while (!this.shutdown) {
            try {
                Thread.sleep(3 * 60 * 60 * 1000);
                this.renew();
            } catch (final InterruptedException e) {
                // ignore
            }
        }
    }

    protected void preexec() {
    }

    abstract protected boolean exec(String password);

    protected void postexec() {
    }
}
