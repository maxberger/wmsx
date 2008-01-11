package hu.kfki.grid.wmsx.util;

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

    private OutputStream prepareOutput(final String stdout) throws IOException {
        if (stdout == null) {
            return null;
        }
        final File stdoutfile = new File(stdout);
        stdoutfile.getAbsoluteFile().getParentFile().mkdirs();
        return new BufferedOutputStream(new FileOutputStream(stdout));
    }

    public int launchScript(final String cmdString, final File dir,
            final String stdout) {
        int retVal = 0;
        try {
            final OutputStream o = this.prepareOutput(stdout);
            final Process p = Runtime.getRuntime().exec(cmdString, null, dir);
            retVal = this.wrapProcess(p, o);
            if (o != null) {
                o.close();
            }
        } catch (final IOException e) {
            ScriptLauncher.LOGGER.warning("IOException launching script: "
                    + e.getMessage());
        }
        return retVal;
    }

    public int launchScript(final String[] cmdarray, final String stdout) {
        int retVal = 0;
        if (new File(cmdarray[0]).exists()) {
            try {
                final OutputStream o = this.prepareOutput(stdout);
                retVal = this.launchScript(cmdarray, o);
                if (o != null) {
                    o.close();
                }
            } catch (final IOException e) {
                ScriptLauncher.LOGGER.warning("IOException launching script: "
                        + e.getMessage());
            }
        }
        return retVal;
    }

    public int wrapProcess(final Process p, final OutputStream out) {
        int retVal = 0;
        if (p != null) {
            try {
                final InputStream i = new BufferedInputStream(p
                        .getInputStream());
                try {
                    retVal = p.waitFor();
                } catch (final InterruptedException e) {
                    // Ignore
                }
                if (out != null) {
                    final byte[] buf = new byte[4096];
                    int r = i.read(buf);
                    while (r >= 0) {
                        out.write(buf, 0, r);
                        r = i.read(buf);
                    }
                }
            } catch (final IOException e) {
                ScriptLauncher.LOGGER.warning("IOException wrapping exec: "
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
                retVal = this.wrapProcess(p, out);
            } catch (final IOException e) {
                ScriptLauncher.LOGGER.warning("IOException launching script: "
                        + e.getMessage());
            }
        }
        return retVal;
    }

}
