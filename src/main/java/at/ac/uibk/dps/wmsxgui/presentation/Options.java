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

import at.ac.uibk.dps.wmsxgui.business.BusinessManager;
import hu.kfki.grid.wmsx.Wmsx;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 *
 * @author lancelot
 * @version 20.4.2009
 */
public class Options extends javax.swing.JFrame {

    private BusinessManager businessman;
    private Wmsx wmsx_service;

    /** Creates new form Optionen. */
    public Options() {
        this.businessman = BusinessManager.getInstance();
        wmsx_service = businessman.getWmsxService();

        initComponents();
        centerScreen();

        // Set icon
        Image icon = (new ImageIcon(getClass().getResource("/icon.png"))).getImage();
        this.setIconImage(icon);

    }

     /** Positioniert das Fenster genau in der Mitte des Bildschirmes...
	 *
	 */
	private void centerScreen()
	{
		Dimension dim = getToolkit().getScreenSize();
		Rectangle abounds = getBounds();
		setLocation((dim.width - abounds.width) / 2,(dim.height - abounds.height) / 2);
		requestFocus();
	}

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btn_close = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        tb_maxjobcount = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        pf_afspassword = new javax.swing.JPasswordField();
        btn_forgetafspassword = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        pf_gridpassword = new javax.swing.JPasswordField();
        btn_forgetgridpassword = new javax.swing.JButton();
        btn_setafspassword = new javax.swing.JButton();
        btn_setgridpassword = new javax.swing.JButton();
        btn_setmaxjobcount = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        btn_close.setText("Close");
        btn_close.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_closeActionPerformed(evt);
            }
        });

        jLabel1.setText("Max JobCount");

        tb_maxjobcount.setText("5");

        jLabel2.setText("Afs Password");

        pf_afspassword.setText("jPasswordField1");

        btn_forgetafspassword.setText("forget");
        btn_forgetafspassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_forgetafspasswordActionPerformed(evt);
            }
        });

        jLabel3.setText("Grid Password");

        pf_gridpassword.setText("jPasswordField1");

        btn_forgetgridpassword.setText("forget");
        btn_forgetgridpassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_forgetgridpasswordActionPerformed(evt);
            }
        });

        btn_setafspassword.setText("set");
        btn_setafspassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_setafspasswordActionPerformed(evt);
            }
        });

        btn_setgridpassword.setText("set");
        btn_setgridpassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_setgridpasswordActionPerformed(evt);
            }
        });

        btn_setmaxjobcount.setText("set");
        btn_setmaxjobcount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_setmaxjobcountActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(tb_maxjobcount, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_setmaxjobcount))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(pf_gridpassword, javax.swing.GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE)
                                    .addComponent(pf_afspassword, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(btn_setafspassword)
                                    .addComponent(btn_setgridpassword))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btn_forgetgridpassword, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(btn_forgetafspassword, javax.swing.GroupLayout.Alignment.TRAILING)))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(192, 192, 192)
                        .addComponent(btn_close, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(tb_maxjobcount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_setmaxjobcount))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(pf_afspassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_forgetafspassword)
                    .addComponent(btn_setafspassword))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(pf_gridpassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_forgetgridpassword)
                    .addComponent(btn_setgridpassword))
                .addGap(18, 18, 18)
                .addComponent(btn_close)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_setafspasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_setafspasswordActionPerformed
        wmsx_service.rememberAfs(new String(pf_afspassword.getPassword()));
        System.out.println(new String(pf_afspassword.getPassword()));
    }//GEN-LAST:event_btn_setafspasswordActionPerformed

    private void btn_setgridpasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_setgridpasswordActionPerformed
        wmsx_service.rememberGrid(new String(pf_gridpassword.getPassword()));  //fehler??
        System.out.println(new String(pf_gridpassword.getPassword()));
    }//GEN-LAST:event_btn_setgridpasswordActionPerformed

    private void btn_forgetafspasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_forgetafspasswordActionPerformed

        wmsx_service.forgetAfs();
    }//GEN-LAST:event_btn_forgetafspasswordActionPerformed

    private void btn_forgetgridpasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_forgetgridpasswordActionPerformed

        //die funktion gibts nicht :-(
    }//GEN-LAST:event_btn_forgetgridpasswordActionPerformed

    private void btn_setmaxjobcountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_setmaxjobcountActionPerformed
        try
        {
            int maxjobcnt = Integer.parseInt(tb_maxjobcount.getText());

            if (maxjobcnt>0)
            {

                wmsx_service.setMaxJobs(maxjobcnt);

            } else
                JOptionPane.showMessageDialog(this, "MaxJobCount has to be greater than zero!", "WMSX GUI - Options", JOptionPane.ERROR_MESSAGE);

        } catch (final NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "MaxJobCount isn't an valid Integer!", "WMSX GUI - Options", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btn_setmaxjobcountActionPerformed

    private void btn_closeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_closeActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_btn_closeActionPerformed

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_close;
    private javax.swing.JButton btn_forgetafspassword;
    private javax.swing.JButton btn_forgetgridpassword;
    private javax.swing.JButton btn_setafspassword;
    private javax.swing.JButton btn_setgridpassword;
    private javax.swing.JButton btn_setmaxjobcount;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPasswordField pf_afspassword;
    private javax.swing.JPasswordField pf_gridpassword;
    private javax.swing.JTextField tb_maxjobcount;
    // End of variables declaration//GEN-END:variables
}
