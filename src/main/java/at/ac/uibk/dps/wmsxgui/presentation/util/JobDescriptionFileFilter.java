/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.ac.uibk.dps.wmsxgui.presentation.util;

/**
 *
 * @author bafu
 */
import java.io.File;

import javax.swing.filechooser.FileFilter;

public class JobDescriptionFileFilter extends FileFilter {

    // Accept all directories and jdl files.
    @Override
    public boolean accept(final File f) {
        if (f.isDirectory()) {
            return true;
        }

        final String extension = this.getExtension(f);
        if (extension != null) {
            if (extension.equals("jdl")) {
                return true;
            }
            else {
                return false;
            }
        }

        return false;
    }

    // The description of this filter
    @Override
    public String getDescription() {
        return "Job Description Files";
    }

    private String getExtension(final File f) {
        String ext = null;
        final String s = f.getName();
        final int i = s.lastIndexOf('.');

        if ((i > 0) && (i < s.length() - 1)) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }
}
