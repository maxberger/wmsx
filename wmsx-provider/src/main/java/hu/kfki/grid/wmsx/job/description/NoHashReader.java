package hu.kfki.grid.wmsx.job.description;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class NoHashReader extends Reader {

    final BufferedReader in;

    String buffer;

    int bufpos;

    public NoHashReader(final Reader orig) {
        this.in = new BufferedReader(orig);
    }

    @Override
    public void close() throws IOException {
        this.in.close();
    }

    @Override
    public int read(final char[] cbuf, final int off, final int len)
            throws IOException {

        if (this.buffer == null) {
            this.buffer = this.in.readLine();
            this.bufpos = 0;
        }
        if (this.buffer == null) {
            return -1;
        }

        int blen = this.buffer.length();
        if (blen > 0 && this.buffer.charAt(0) == '#') {
            blen = 0;
        }
        final int toRead = Math.min(len, blen - this.bufpos);

        this.buffer.getChars(this.bufpos, this.bufpos + toRead, cbuf, off);
        this.bufpos += toRead;
        if (this.bufpos == blen) {
            this.buffer = null;
        }

        return toRead;
    }
}
