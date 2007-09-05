package hu.kfki.grid.wmsx.provider.scripts;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

public class ScriptLauncher {

    private static ScriptLauncher instance;

    private static final Logger LOGGER = Logger.getLogger(ScriptLauncher.class
            .toString());

    private ScriptLauncher() {
    }

    public static synchronized ScriptLauncher getInstance() {
        if (ScriptLauncher.instance == null) {
            ScriptLauncher.instance = new ScriptLauncher();
        }
        return ScriptLauncher.instance;
    }

    public int launchScript(final String[] cmdarray, final String stdout) {
        int retVal = 0;
        if (new File(cmdarray[0]).exists()) {
            final File stdoutfile = new File(stdout);
            try {
                stdoutfile.getParentFile().mkdirs();
                final OutputStream o = new BufferedOutputStream(
                        new FileOutputStream(stdout));
                retVal = this.launchScript(cmdarray, o);
                o.close();
            } catch (final IOException e) {
                ScriptLauncher.LOGGER.warning("IOException launching script: "
                        + e.getMessage());
            }
        }
        return retVal;
    }

    public int launchScript(final String[] cmdarray, final OutputStream out) {
        int retVal = 0;
        if (new File(cmdarray[0]).exists()) {
            try {
                final Process p = Runtime.getRuntime().exec(cmdarray);
                final InputStream i = new BufferedInputStream(p
                        .getInputStream());
                try {
                    retVal = p.waitFor();
                } catch (final InterruptedException e) {
                    // Ignore
                }
                final byte[] buf = new byte[4096];
                int r = i.read(buf);
                while (r >= 0) {
                    out.write(buf, 0, r);
                    r = i.read(buf);
                }
            } catch (final IOException e) {
                ScriptLauncher.LOGGER.warning("IOException launching script: "
                        + e.getMessage());
            }
        }
        return retVal;
    }

}
