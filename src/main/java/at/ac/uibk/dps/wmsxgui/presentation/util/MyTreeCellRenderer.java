/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.ac.uibk.dps.wmsxgui.presentation.util;

import hu.kfki.grid.wmsx.job.JobState;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import at.ac.uibk.dps.wmsxgui.business.JobData;

/**
 * 
 * @author bafu
 */
public class MyTreeCellRenderer extends DefaultTreeCellRenderer {
    /**
     * 
     */
    private static final long serialVersionUID = 6155147853626136087L;
    private final Icon greenCircle;
    private final Icon orangeCircle;
    private final Icon redCircle;
    private final Icon grid;

    public MyTreeCellRenderer() {
        this.greenCircle = new ImageIcon(this.getClass()
                .getResource("/greenCircle.png"));
        this.orangeCircle = new ImageIcon(this.getClass()
                .getResource("/orangeCircle.png"));
        this.redCircle = new ImageIcon(this.getClass()
                .getResource("/redCircle.png"));
        this.grid = new ImageIcon(this.getClass().getResource("/grid.png"));
    }

    @Override
    public Component getTreeCellRendererComponent(final JTree tree,
            final Object value, final boolean sel, final boolean expanded,
            final boolean leaf, final int row, final boolean hasFocus) {

        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
                                           row, hasFocus);

        if (this.isJob(value)) {
            final JobData job = (JobData) ((DefaultMutableTreeNode) value)
                    .getUserObject();

            final JobState state = job.getJobinfo().getStatus();
            if (state.equals(JobState.RUNNING)) {
                this.setIcon(this.greenCircle);
            } else if (state.equals(JobState.STARTUP)) {
                this.setIcon(this.orangeCircle);
            } else if (state.equals(JobState.SUCCESS)) {
                this.setIcon(this.redCircle);
            }

            this.setToolTipText(job.getJobinfo().getDescription());
            this.setText(job.getTransportJobUID().toString());

        } else {
            this.setIcon(this.grid);
            this.setToolTipText(null); // no tool tip
        }

        return this;
    }

    protected boolean isJob(final Object value) {
        final DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        final String classname = node.getUserObject().getClass()
                .getSimpleName();

        if (classname.equals("JobData")) {
            return true;
        }

        return false;
    }
}
