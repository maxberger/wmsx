/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.ac.uibk.dps.wmsxgui.presentation.util;

import at.ac.uibk.dps.wmsxgui.business.JobData;
import hu.kfki.grid.wmsx.job.JobState;
import java.awt.Component;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *
 * @author bafu
 */
public class MyTreeCellRenderer extends DefaultTreeCellRenderer {
    private Icon greenCircle;
    private Icon orangeCircle;
    private Icon redCircle;
    private Icon grid;

    public MyTreeCellRenderer() {
        greenCircle =  (Icon)new ImageIcon(getClass().getResource("/greenCircle.png"));
        orangeCircle =  (Icon)new ImageIcon(getClass().getResource("/orangeCircle.png"));
        redCircle =  (Icon)new ImageIcon(getClass().getResource("/redCircle.png"));
        grid =  (Icon)new ImageIcon(getClass().getResource("/grid.png"));
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
            boolean expanded,
            boolean leaf,
            int row,
            boolean hasFocus)
    {

        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        
        if (isJob(value)) {
            JobData job = (JobData) ((DefaultMutableTreeNode)value).getUserObject();

            JobState state = job.getJobinfo().getStatus();
            if (state.equals(JobState.RUNNING))
                setIcon(greenCircle);
            else if (state.equals(JobState.STARTUP))
                setIcon(orangeCircle);
            else if (state.equals(JobState.SUCCESS))
                setIcon(redCircle);

            setToolTipText(job.getJobinfo().getDescription());
            setText(job.getTransportJobUID().getLocalId());

        } else {
            setIcon(grid);
            setToolTipText(null); //no tool tip
        }

        return this;
    }

    protected boolean isJob(Object value)
    {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
        String classname = node.getUserObject().getClass().getSimpleName();

        if (classname.equals("JobData")){
            return true;
        }

        return false;
    }
}
