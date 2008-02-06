package hu.kfki.grid.wmsx.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

public class StreamListener implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(StreamListener.class
            .toString());

    private final OutputStream out;

    private final InputStream in;

    private StreamListener(final InputStream i, final OutputStream o) {
        this.out = o;
        this.in = i;
    }

    public void run() {
        try {
            if (this.out != null) {
                final byte[] buf = new byte[4096];
                int r = this.in.read(buf);
                while (r >= 0) {
                    synchronized (this.out) {
                        this.out.write(buf, 0, r);
                        if (this.in.available() == 0) {
                            this.out.flush();
                        }
                    }
                    r = this.in.read(buf);
                }
            }
        } catch (final IOException e) {
            StreamListener.LOGGER.warning("IOException wrapping exec: "
                    + e.getMessage());
        } finally {
            try {
                this.in.close();
            } catch (final IOException e) {
                // ignore
            }
            try {
                this.out.close();
            } catch (final IOException e) {
                // ignore
            }
        }
    }

    public static void listen(final InputStream i, final OutputStream o) {
        if (i == null) {
            return;
        }
        if (o == null) {
            return;
        }
        new Thread(new StreamListener(i, o)).start();
    }
}
