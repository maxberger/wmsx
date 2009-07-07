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
 * @author WmsxGUI Team
 * @version 1.0
 */
public class MyTreeCellRenderer extends DefaultTreeCellRenderer {
    /**
     * 
     */
    private static final long serialVersionUID = 6155147853626136087L;
    private final Icon runningIcon;
    private final Icon startupIcon;
    private final Icon successIcon;
    private final Icon failedIcon;
    private final Icon runningWorkerIcon;
    private final Icon startupWorkerIcon;
    private final Icon successWorkerIcon;
    private final Icon failedWorkerIcon;
    private final Icon gridIcon;

    /**
     * Creates a new TreeCellrenderer object.
     */
    public MyTreeCellRenderer() {
        this.runningIcon = new ImageIcon(this.getClass()
                .getResource("/jobstate_running.png"));
        this.startupIcon = new ImageIcon(this.getClass()
                .getResource("/jobstate_startup.png"));
        this.successIcon = new ImageIcon(this.getClass()
                .getResource("/jobstate_success.png"));
        this.failedIcon = new ImageIcon(this.getClass()
                .getResource("/jobstate_failed.png"));
        this.runningWorkerIcon = new ImageIcon(this.getClass()
                .getResource("/jobstate_running_worker.png"));
        this.startupWorkerIcon = new ImageIcon(this.getClass()
                .getResource("/jobstate_startup_worker.png"));
        this.successWorkerIcon = new ImageIcon(this.getClass()
                .getResource("/jobstate_success_worker.png"));
        this.failedWorkerIcon = new ImageIcon(this.getClass()
                .getResource("/jobstate_failed_worker.png"));
        this.gridIcon = new ImageIcon(this.getClass().getResource("/grid.png"));
    }

    /**
     * getTreeCellRendererComponent returns the cell content for the given cell
     * value.
     * 
     * @param tree
     *            JTree, which contains the cell
     * @param value
     *            cell value (JobData)
     * @param sel
     *            is the cell selected?
     * @param expanded
     *            is the cell expanded?
     * @param leaf
     *            is the cell a leaf?
     * @param row
     *            row count of the cell
     * @param hasFocus
     *            has the cell a focus?
     * @return cell component object with content for the given cell value
     */
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
                this
                        .setIcon((job.getJobinfo().isWorker()) ? this.runningWorkerIcon
                                : this.runningIcon);
            } else if (state.equals(JobState.STARTUP)) {
                this
                        .setIcon((job.getJobinfo().isWorker()) ? this.startupWorkerIcon
                                : this.startupIcon);
            } else if (state.equals(JobState.SUCCESS)) {
                this
                        .setIcon((job.getJobinfo().isWorker()) ? this.successWorkerIcon
                                : this.successIcon);
            } else if (state.equals(JobState.FAILED)) {
                this
                        .setIcon((job.getJobinfo().isWorker()) ? this.failedWorkerIcon
                                : this.failedIcon);
            }

            this.setToolTipText(job.getTransportJobUID().toString() + " "
                    + state + "...");
            this.setText(job.getTransportJobUID().toString());

        } else {
            this.setIcon(this.gridIcon);
            this.setToolTipText(null);
        }

        return this;
    }

    /**
     * isJob checks if the given object is an instance of JobData.
     * 
     * @param value
     *            object, which should be checked
     * @return true, if the given object is an instance of JobData, otherwise
     *         false
     */
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
