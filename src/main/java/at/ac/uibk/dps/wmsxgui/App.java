/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.ac.uibk.dps.wmsxgui;

import at.ac.uibk.dps.wmsxgui.presentation.MainWindow;

/**
 * 
 * @author bafu
 */
public class App {

    /**
     * @param args
     *            the command line arguments
     */
    public static void main(final String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {

                System.out.println("wmsx-gui is starting...");
                new MainWindow().setVisible(true);

            }
        });
    }
}
