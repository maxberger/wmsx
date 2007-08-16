package renewer;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.logging.Logger;

public class PasswordAppLauncher {
    private static PasswordAppLauncher instance;

    private static final Logger LOGGER = Logger
            .getLogger(PasswordAppLauncher.class.toString());

    private PasswordAppLauncher() {
    }

    static public synchronized PasswordAppLauncher getInstance() {
        if (PasswordAppLauncher.instance == null) {
            PasswordAppLauncher.instance = new PasswordAppLauncher();
        }
        return PasswordAppLauncher.instance;
    }

    public boolean launch(final String[] cmdarray, final String password) {
        boolean retVal = false;
        try {
            final Process p = Runtime.getRuntime().exec(cmdarray);
            final BufferedInputStream bi = new BufferedInputStream(p
                    .getInputStream());
            final byte[] b = new byte[4096];
            bi.read(b);
            if (password != null) {
                Thread.sleep(1000);
                final BufferedWriter w = new BufferedWriter(
                        new OutputStreamWriter(p.getOutputStream()));
                w.write(password);
                w.newLine();
                w.flush();
            }
            retVal = p.waitFor() == 0;

        } catch (final IOException e) {
            PasswordAppLauncher.LOGGER.warning(e.getMessage());
        } catch (final InterruptedException e) {
            PasswordAppLauncher.LOGGER.warning(e.getMessage());
        }
        return retVal;
    }
}
