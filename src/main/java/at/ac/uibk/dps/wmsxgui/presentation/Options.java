/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Optionen.java
 *
 * Created on 19.04.2009, 17:22:58
 */

package at.ac.uibk.dps.wmsxgui.presentation;

import hu.kfki.grid.wmsx.Wmsx;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import at.ac.uibk.dps.wmsxgui.business.BusinessManager;

/**
 * 
 * @author WmsxGUI Team
 * @version 1.0
 */
public class Options extends javax.swing.JDialog {

    /**
     * 
     */
    private static final long serialVersionUID = 5689473181735526756L;
    private final BusinessManager businessman;
    private final Wmsx wmsx_service;

    /** Creates new form Optionen.
     * 
     * @param parent parent window
     * @param modal true, if this Dialog should be modal, otherwise false
     */
    public Options(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.businessman = BusinessManager.getInstance();
        this.wmsx_service = this.businessman.getWmsxService();

        this.initComponents();
        this.centerScreen();

        // Set icon
        final Image icon = (new ImageIcon(this.getClass()
                .getResource("/icon.png"))).getImage();
        this.setIconImage(icon);

        for (final String backend : this.businessman.getBackends()) {
            this.cb_backend.addItem(backend);
        }
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
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btn_close = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        tb_maxjobcount = new javax.swing.JTextField();
        btn_setmaxjobcount = new javax.swing.JButton();
        cb_backend = new javax.swing.JComboBox();
        l_backend = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        pf_gridpassword = new javax.swing.JPasswordField();
        pf_afspassword = new javax.swing.JPasswordField();
        tfVo = new javax.swing.JTextField();
        btn_setafspassword = new javax.swing.JButton();
        btn_setgridpassword = new javax.swing.JButton();
        btn_forgetgridpassword = new javax.swing.JButton();
        btn_forgetafspassword = new javax.swing.JButton();
        btn_setVo = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        btn_close.setText("Close");
        btn_close.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_closeActionPerformed(evt);
            }
        });

        jLabel1.setText("Max JobCount");

        tb_maxjobcount.setText("5");

        btn_setmaxjobcount.setText("set");
        btn_setmaxjobcount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_setmaxjobcountActionPerformed(evt);
            }
        });

        cb_backend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cb_backendActionPerformed(evt);
            }
        });

        l_backend.setText("Backend");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel3.setText("Grid Password");

        jLabel2.setText("Afs Password");

        jLabel4.setText("VO");

        pf_gridpassword.setText("jPasswordField1");

        pf_afspassword.setText("jPasswordField1");

        btn_setafspassword.setText("set");
        btn_setafspassword.setEnabled(false);
        btn_setafspassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_setafspasswordActionPerformed(evt);
            }
        });

        btn_setgridpassword.setText("set");
        btn_setgridpassword.setEnabled(false);
        btn_setgridpassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_setgridpasswordActionPerformed(evt);
            }
        });

        btn_forgetgridpassword.setText("forget");
        btn_forgetgridpassword.setEnabled(false);
        btn_forgetgridpassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_forgetgridpasswordActionPerformed(evt);
            }
        });

        btn_forgetafspassword.setText("forget");
        btn_forgetafspassword.setEnabled(false);
        btn_forgetafspassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_forgetafspasswordActionPerformed(evt);
            }
        });

        btn_setVo.setText("set");
        btn_setVo.setEnabled(false);
        btn_setVo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_setVoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pf_gridpassword, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pf_afspassword, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfVo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btn_setafspassword)
                            .addComponent(btn_setgridpassword))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btn_forgetgridpassword)
                            .addComponent(btn_forgetafspassword)))
                    .addComponent(btn_setVo))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {pf_afspassword, pf_gridpassword, tfVo});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(btn_setafspassword)
                    .addComponent(btn_forgetafspassword)
                    .addComponent(pf_afspassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(btn_setgridpassword)
                    .addComponent(btn_forgetgridpassword)
                    .addComponent(pf_gridpassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(btn_setVo)
                    .addComponent(tfVo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(l_backend))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(tb_maxjobcount, javax.swing.GroupLayout.DEFAULT_SIZE, 328, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_setmaxjobcount))
                            .addComponent(cb_backend, 0, 387, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(btn_close, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(btn_setmaxjobcount)
                    .addComponent(tb_maxjobcount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(l_backend)
                    .addComponent(cb_backend, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btn_close)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_setVoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_setVoActionPerformed
        wmsx_service.setBackend(cb_backend.getSelectedItem().toString());
        wmsx_service.setVo(tfVo.getText());
}//GEN-LAST:event_btn_setVoActionPerformed

    private void cb_backendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cb_backendActionPerformed
        // TODO add your handling code here:
        if(cb_backend.getSelectedItem()!=null){
            btn_forgetafspassword.setEnabled(true);
            //btn_forgetgridpassword.setEnabled(true);
            btn_setVo.setEnabled(true);
            btn_setafspassword.setEnabled(true);
            btn_setgridpassword.setEnabled(true);
            btn_setmaxjobcount.setEnabled(true);
        }
    }//GEN-LAST:event_cb_backendActionPerformed

    private void btn_setmaxjobcountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_setmaxjobcountActionPerformed
        try {
            final int maxjobcnt = Integer.parseInt(this.tb_maxjobcount
                    .getText());

            if (maxjobcnt > 0) {

                this.wmsx_service.setMaxJobs(maxjobcnt);

            } else {
                JOptionPane
                        .showMessageDialog(
                                           this,
                                           "MaxJobCount has to be greater than zero!",
                                           "WMSX GUI - Options",
                                           JOptionPane.ERROR_MESSAGE);
            }

        } catch (final NumberFormatException e) {
            JOptionPane
                    .showMessageDialog(this,
                                       "MaxJobCount isn't an valid Integer!",
                                       "WMSX GUI - Options",
                                       JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_setmaxjobcountActionPerformed

    private void btn_setafspasswordActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btn_setafspasswordActionPerformed
        this.wmsx_service.setBackend(cb_backend.getSelectedItem().toString());
        boolean suc = this.wmsx_service.rememberAfs(new String(this.pf_afspassword
                .getPassword()));
        if(!suc){
            JOptionPane.showConfirmDialog(this, "Password is incorrect!", "Wrong password", JOptionPane.DEFAULT_OPTION,JOptionPane.ERROR_MESSAGE);
        }
    }// GEN-LAST:event_btn_setafspasswordActionPerformed

    private void btn_setgridpasswordActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btn_setgridpasswordActionPerformed
        this.wmsx_service.setBackend(cb_backend.getSelectedItem().toString());
        boolean suc = this.wmsx_service.rememberGrid(new String(this.pf_gridpassword.getPassword()));
        if(!suc){
            JOptionPane.showConfirmDialog(this, "Password is incorrect!", "Wrong password", JOptionPane.DEFAULT_OPTION,JOptionPane.ERROR_MESSAGE);
        }
    }// GEN-LAST:event_btn_setgridpasswordActionPerformed

    private void btn_forgetafspasswordActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btn_forgetafspasswordActionPerformed
        this.wmsx_service.setBackend(cb_backend.getSelectedItem().toString());
        this.wmsx_service.forgetAfs();
    }// GEN-LAST:event_btn_forgetafspasswordActionPerformed

    private void btn_forgetgridpasswordActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btn_forgetgridpasswordActionPerformed
            
        // die funktion gibts nicht :-(
    }// GEN-LAST:event_btn_forgetgridpasswordActionPerformed

    private void btn_closeActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btn_closeActionPerformed
        this.dispose();
    }// GEN-LAST:event_btn_closeActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_close;
    private javax.swing.JButton btn_forgetafspassword;
    private javax.swing.JButton btn_forgetgridpassword;
    private javax.swing.JButton btn_setVo;
    private javax.swing.JButton btn_setafspassword;
    private javax.swing.JButton btn_setgridpassword;
    private javax.swing.JButton btn_setmaxjobcount;
    private javax.swing.JComboBox cb_backend;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel l_backend;
    private javax.swing.JPasswordField pf_afspassword;
    private javax.swing.JPasswordField pf_gridpassword;
    private javax.swing.JTextField tb_maxjobcount;
    private javax.swing.JTextField tfVo;
    // End of variables declaration//GEN-END:variables
}
