/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * NewJDialog.java
 *
 * Created on 26.06.2009, 10:56:52
 */

package at.ac.uibk.dps.wmsxgui.presentation;

import java.awt.Dimension;
import java.awt.Rectangle;

/**
 * 
 * @author WmsxGUI Team
 * @version 1.0
 */
public class About extends javax.swing.JDialog {
    private static final long serialVersionUID = 249262904881341511L;

    /**
     * Creates new form About.
     * 
     * @param parent
     *            parent window
     * @param modal
     *            true, if this Dialog should be modal, otherwise false
     */
    public About(final java.awt.Frame parent, final boolean modal) {
        super(parent, modal);
        this.initComponents();
        this.centerScreen();
    }

    /**
     * Positioniert das Fenster genau in der Mitte des Bildschirmes.
     * 
     */
    private void centerScreen() {
        final Dimension dim = this.getToolkit().getScreenSize();
        final Rectangle abounds = this.getBounds();
        this.setLocation((dim.width - abounds.width) / 2,
                         (dim.height - abounds.height) / 2);
        this.requestFocus();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed"
    // desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        this.btnClose = new javax.swing.JButton();
        this.jScrollPane1 = new javax.swing.JScrollPane();
        this.jTextArea1 = new javax.swing.JTextArea();
        this.jLabel1 = new javax.swing.JLabel();
        this.jLabel2 = new javax.swing.JLabel();
        final javax.swing.JLabel versionLabel = new javax.swing.JLabel();
        final javax.swing.JLabel appVersionLabel = new javax.swing.JLabel();
        final javax.swing.JLabel appDescLabel = new javax.swing.JLabel();
        final javax.swing.JLabel vendorLabel = new javax.swing.JLabel();
        final javax.swing.JLabel appVendorLabel = new javax.swing.JLabel();

        this
                .setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        this.setTitle("About");
        this.setResizable(false);

        this.btnClose.setText("close");
        this.btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(final java.awt.event.ActionEvent evt) {
                About.this.btnCloseActionPerformed(evt);
            }
        });

        this.jTextArea1.setColumns(20);
        this.jTextArea1.setLineWrap(true);
        this.jTextArea1.setRows(5);
        this.jTextArea1
                .setText("Thanks to tango.freedesktop.org and sweetie.sublink.ca for using their icons.");
        this.jTextArea1.setWrapStyleWord(true);
        this.jScrollPane1.setViewportView(this.jTextArea1);

        this.jLabel1.setFont(new java.awt.Font("DejaVu Sans", 0, 18));
        this.jLabel1.setText("WMSX Gui");

        this.jLabel2.setIcon(new javax.swing.ImageIcon(this.getClass()
                .getResource("/icon.png"))); // NOI18N

        versionLabel.setFont(versionLabel.getFont()
                .deriveFont(
                            versionLabel.getFont().getStyle()
                                    | java.awt.Font.BOLD));
        versionLabel.setText("Product Version:");

        appVersionLabel.setText("1.0");

        appDescLabel
                .setText("<html>A simple graphical user interface for the wmsx-requestor tool by Max Berger.");

        vendorLabel.setFont(vendorLabel.getFont()
                .deriveFont(
                            vendorLabel.getFont().getStyle()
                                    | java.awt.Font.BOLD));
        vendorLabel.setText("Author:");

        appVendorLabel.setText("Martin Illecker, Georg Bader");

        final javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this
                .getContentPane());
        this.getContentPane().setLayout(layout);
        layout
                .setHorizontalGroup(layout
                        .createParallelGroup(
                                             javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(
                                  layout
                                          .createSequentialGroup()
                                          .addContainerGap()
                                          .addGroup(
                                                    layout
                                                            .createParallelGroup(
                                                                                 javax.swing.GroupLayout.Alignment.CENTER)
                                                            .addComponent(
                                                                          this.jScrollPane1,
                                                                          javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                          297,
                                                                          Short.MAX_VALUE)
                                                            .addGroup(
                                                                      layout
                                                                              .createSequentialGroup()
                                                                              .addComponent(
                                                                                            vendorLabel)
                                                                              .addPreferredGap(
                                                                                               javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                              .addComponent(
                                                                                            appVendorLabel))
                                                            .addComponent(
                                                                          this.btnClose))
                                          .addContainerGap())
                        .addGroup(
                                  javax.swing.GroupLayout.Alignment.CENTER,
                                  layout.createSequentialGroup().addGap(113,
                                                                        113,
                                                                        113)
                                          .addComponent(this.jLabel1)
                                          .addGap(113, 113, 113))
                        .addGroup(
                                  javax.swing.GroupLayout.Alignment.CENTER,
                                  layout.createSequentialGroup().addGap(144,
                                                                        144,
                                                                        144)
                                          .addComponent(this.jLabel2)
                                          .addGap(145, 145, 145))
                        .addGroup(
                                  javax.swing.GroupLayout.Alignment.CENTER,
                                  layout
                                          .createSequentialGroup()
                                          .addGap(12, 12, 12)
                                          .addComponent(
                                                        appDescLabel,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        297, Short.MAX_VALUE)
                                          .addGap(12, 12, 12))
                        .addGroup(
                                  javax.swing.GroupLayout.Alignment.CENTER,
                                  layout.createSequentialGroup().addGap(86, 86,
                                                                        86)
                                          .addComponent(versionLabel).addGap(5,
                                                                             5,
                                                                             5)
                                          .addComponent(appVersionLabel)
                                          .addGap(87, 87, 87)));
        layout
                .setVerticalGroup(layout
                        .createParallelGroup(
                                             javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(
                                  layout
                                          .createSequentialGroup()
                                          .addContainerGap()
                                          .addComponent(this.jLabel1)
                                          .addPreferredGap(
                                                           javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                          .addComponent(this.jLabel2)
                                          .addPreferredGap(
                                                           javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                          .addComponent(appDescLabel)
                                          .addPreferredGap(
                                                           javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                          .addGroup(
                                                    layout
                                                            .createParallelGroup(
                                                                                 javax.swing.GroupLayout.Alignment.BASELINE)
                                                            .addComponent(
                                                                          versionLabel)
                                                            .addComponent(
                                                                          appVersionLabel))
                                          .addPreferredGap(
                                                           javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                          .addGroup(
                                                    layout
                                                            .createParallelGroup(
                                                                                 javax.swing.GroupLayout.Alignment.BASELINE)
                                                            .addComponent(
                                                                          vendorLabel)
                                                            .addComponent(
                                                                          appVendorLabel))
                                          .addPreferredGap(
                                                           javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                          .addComponent(this.jScrollPane1)
                                          .addPreferredGap(
                                                           javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                          .addComponent(this.btnClose)
                                          .addContainerGap()));

        this.pack();
    }

    // </editor-fold>//GEN-END:initComponents

    // GEN-FIRST:event_btnCloseActionPerformed
    private void btnCloseActionPerformed(final java.awt.event.ActionEvent evt) {
        this.dispose();
    }

    // GEN-LAST:event_btnCloseActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables

}