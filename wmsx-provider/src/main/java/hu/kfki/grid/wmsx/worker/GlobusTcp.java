package hu.kfki.grid.wmsx.worker;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.logging.Logger;

public class GlobusTcp {

    private static GlobusTcp instance;

    private final int mintcp;

    private final int maxtcp;

    private static final Logger LOGGER = Logger.getLogger(GlobusTcp.class
            .toString());

    private GlobusTcp() {
        int min = 20000;
        int max = 25000;
        try {
            final String rangeStr = System.getenv("GLOBUS_TCP_PORT_RANGE");
            if (rangeStr != null) {
                final StringTokenizer tk = new StringTokenizer(rangeStr,
                        " \t\n\r\f,;:");
                min = Integer.parseInt(tk.nextToken());
                max = Integer.parseInt(tk.nextToken());
            }
        } catch (final NumberFormatException nfe) {
            // IGNORE
        } catch (final NoSuchElementException nse) {
            // IGNORE
        } catch (final Error e) {
            // Ignore. Stupid JDK 1.4
        }

        if (min < 1024) {
            min = 20000;
        }
        if (max < min) {
            max = min;
        }
        this.mintcp = min;
        this.maxtcp = max;
        GlobusTcp.LOGGER.fine("TCP Port Range: " + min + " to " + max);
    }

    public static synchronized GlobusTcp getInstance() {
        if (GlobusTcp.instance == null) {
            GlobusTcp.instance = new GlobusTcp();
        }
        return GlobusTcp.instance;
    }

    public int getMinTcp() {
        return this.mintcp;
    }

    public int getMaxTcp() {
        return this.maxtcp;
    }

}
