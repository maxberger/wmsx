package hu.kfki.grid.wmsx.job.description;

import java.util.List;
import java.util.Vector;

public class EmptyJobDescription extends AbstractJobDescription {

    public List getListEntry(final String key) {
        return new Vector();
    }

    public String getStringEntry(final String key) {
        return null;
    }

}
