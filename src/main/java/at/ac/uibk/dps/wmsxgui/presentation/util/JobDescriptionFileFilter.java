/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.ac.uibk.dps.wmsxgui.presentation.util;

import java.io.File;

import java.util.Locale;
import javax.swing.filechooser.FileFilter;

/**
 * 
 * @author WmsxGUI Team
 * @version 1.0
 */
public class JobDescriptionFileFilter extends FileFilter {

    // Accept all directories and jdl files.

    /**
     * Constructor for the JobDescriptionFileFilter.
     */
    public JobDescriptionFileFilter() {
    }

    /**
     * accept checks the given File, if it's a directory or a jdl file.
     * 
     * @param f
     *            File which should be checked
     * @return true if the given file is a directoriy or jdl file, otherwise
     *         false
     */
    @Override
    public boolean accept(final File f) {
        if (f.isDirectory()) {
            return true;
        }

        final String extension = this.getExtension(f);

        return "jdl".equals(extension);
    }

    /**
     * getDescription returns the description of this filter.
     * 
     * @return description of this filter
     */
    @Override
    public String getDescription() {
        return "Job Description Files";
    }

    private String getExtension(final File f) {
        String ext = null;
        final String s = f.getName();
        final int i = s.lastIndexOf('.');

        if ((i > 0) && (i < s.length() - 1)) {
            ext = s.substring(i + 1).toLowerCase(Locale.getDefault());
        }
        return ext;
    }
}
