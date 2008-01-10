package hu.kfki.grid.wmsx.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

public final class FileUtil {
    private static final int BUFSIZE = 4096;

    private FileUtil() {
    };

    public static final void copy(final InputStream fin,
            final OutputStream fout, final IOException ia) throws IOException {
        try {
            final byte[] b = new byte[FileUtil.BUFSIZE];
            int count;
            do {
                count = fin.read(b);
                if (count > 0) {
                    fout.write(b, 0, count);
                }
            } while (count > 0);
        } catch (final IOException o) {
            if (ia == null) {
                throw o;
            } else {
                throw ia;
            }
        } finally {
            if (fin != null) {
                fin.close();
            }
            if (fout != null) {
                fout.close();
            }
        }
    }

    public static final void copy(final InputStream fin, final File out)
            throws IOException {
        final FileOutputStream fout = new FileOutputStream(out);
        FileUtil.copy(fin, fout, null);
    }

    public static final void copy(final File in, final File out)
            throws IOException {

        final FileInputStream fin = new FileInputStream(in);
        final FileOutputStream fout = new FileOutputStream(out);
        final FileChannel inChannel = fin.getChannel();
        final FileChannel outChannel = fout.getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } catch (final IOException ia) {
            // This is due to a bug in Java 1.4
            FileUtil.copy(fin, fout, ia);
        } finally {
            if (inChannel != null) {
                inChannel.close();
            }
            if (outChannel != null) {
                outChannel.close();
            }
        }

        try {
            Runtime.getRuntime().exec(
                    new String[] { "/bin/chmod",
                            "--reference=" + in.getCanonicalPath(),
                            out.getCanonicalPath() }).waitFor();
        } catch (final InterruptedException e) {
            // Ignore
        }
    }

}
