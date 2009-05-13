/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.ac.uibk.dps.wmsxgui.presentation.util;

import hu.kfki.grid.wmsx.JobInfo;
import java.awt.Component;
import java.util.Random;
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
            JobInfo jobinfo = (JobInfo) ((DefaultMutableTreeNode)value).getUserObject();

            Random rand = new Random();
            switch(rand.nextInt(3))
            {
                case 0: setIcon(greenCircle);
                        break;
                case 1: setIcon(orangeCircle);
                        break;
                case 2: setIcon(redCircle);
                        break;
            }

            setToolTipText(jobinfo.toString());
            setText(jobinfo.toString());

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

        if (classname.equals("JobInfo")){
            return true;
        }

        return false;
    }
}
