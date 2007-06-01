package hu.kfki.grid.wmsx;

import net.jini.core.entry.Entry;

public class WmsxEntry implements Entry {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public String userName;

    public WmsxEntry() {
    };

    public WmsxEntry(final String uName) {
        this.userName = uName;
    };

}
