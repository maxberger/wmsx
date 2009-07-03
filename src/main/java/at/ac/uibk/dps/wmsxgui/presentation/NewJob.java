/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * NewJob.java
 *
 * Created on 19.04.2009, 17:22:58
 */

package at.ac.uibk.dps.wmsxgui.presentation;

import hu.kfki.grid.wmsx.SubmissionResult;
import hu.kfki.grid.wmsx.Wmsx;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import at.ac.uibk.dps.wmsxgui.business.BusinessManager;
import at.ac.uibk.dps.wmsxgui.presentation.util.JobDescriptionFileFilter;

/**
 * 
 * @author WmsxGUI Team
 * @version 1.0
 */
public class NewJob extends javax.swing.JDialog {

    private static final String WMSX_GUI_NEW_JOB = "WMSX GUI - NewJob";
    /**
     * 
     */
    private static final long serialVersionUID = 8499517436924693339L;
    private final BusinessManager businessman;
    private final MainWindow mainWindow;
    private final Wmsx wmsxService;

    /**
     * Creates new form NewJob.
     * 
     * @param parent
     *            parent window
     * @param modal
     *            true, if this Dialog should be modal, otherwise false
     */
    public NewJob(final java.awt.Frame parent, final boolean modal) {
        super(parent, modal);
        this.mainWindow = (MainWindow) parent;
        this.businessman = BusinessManager.getInstance();
        this.wmsxService = this.businessman.getWmsxService();

        this.initComponents();
        this.centerScreen();

        // Set icon
        final Image icon = (new ImageIcon(this.getClass()
                .getResource("/icon.png"))).getImage();
        this.setIconImage(icon);

        this.updateBackendCombo();
    }

    private void updateBackendCombo() {
        this.cb_backend.removeAllItems();
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
    // <editor-fold defaultstate="collapsed"
    // <editor-fold defaultstate="collapsed"
    // desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        this.btnCancel = new javax.swing.JButton();
        this.btnOk = new javax.swing.JButton();
        this.cb_type = new javax.swing.JComboBox();
        this.l_creationtype = new javax.swing.JLabel();
        this.contentPanel = new javax.swing.JPanel();
        this.l_backend = new javax.swing.JLabel();
        this.cb_backend = new javax.swing.JComboBox();
        this.l_jobdesc = new javax.swing.JLabel();
        this.tb_jobdescriptionfile = new javax.swing.JTextField();
        this.btn_select_jobdesc = new javax.swing.JButton();
        this.btn_select_resultdir = new javax.swing.JButton();
        this.tb_resultdir = new javax.swing.JTextField();
        this.tb_outputfile = new javax.swing.JTextField();
        this.btn_select_outputfile = new javax.swing.JButton();
        this.l_outputfile = new javax.swing.JLabel();
        this.l_resultdir = new javax.swing.JLabel();

        this.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        this.setResizable(false);

        this.btnCancel.setText("Cancel");
        this.btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(final java.awt.event.ActionEvent evt) {
                NewJob.this.btnCancel(evt);
            }
        });

        this.btnOk.setText("OK");
        this.btnOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(final java.awt.event.ActionEvent evt) {
                NewJob.this.btnOk(evt);
            }
        });

        this.cb_type.setModel(new javax.swing.DefaultComboBoxModel(new String[] {
                "Job", "Worker" }));
        this.cb_type.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(final java.awt.event.ActionEvent evt) {
                NewJob.this.cbTypeActionPerformed(evt);
            }
        });

        this.l_creationtype.setText("CreationType");

        this.contentPanel.setBorder(javax.swing.BorderFactory
                .createTitledBorder("Create a new Job"));

        this.l_backend.setText("Backend");

        this.l_jobdesc.setText("JobDescriptionFile");

        this.btn_select_jobdesc.setText("Select");
        this.btn_select_jobdesc
                .addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(final java.awt.event.ActionEvent evt) {
                        NewJob.this.btnSelectJobdescActionPerformed(evt);
                    }
                });

        this.btn_select_resultdir.setText("Select");
        this.btn_select_resultdir
                .addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(final java.awt.event.ActionEvent evt) {
                        NewJob.this.btnSelectResultdirActionPerformed(evt);
                    }
                });

        this.btn_select_outputfile.setText("Select");
        this.btn_select_outputfile
                .addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(final java.awt.event.ActionEvent evt) {
                        NewJob.this.btnSelectOutputfileActionPerformed(evt);
                    }
                });

        this.l_outputfile.setText("Output to File");

        this.l_resultdir.setText("ResultDirectory");

        final javax.swing.GroupLayout contentPanelLayout = new javax.swing.GroupLayout(
                this.contentPanel);
        this.contentPanel.setLayout(contentPanelLayout);
        contentPanelLayout
                .setHorizontalGroup(contentPanelLayout
                        .createParallelGroup(
                                             javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(
                                  contentPanelLayout
                                          .createSequentialGroup()
                                          .addContainerGap()
                                          .addGroup(
                                                    contentPanelLayout
                                                            .createParallelGroup(
                                                                                 javax.swing.GroupLayout.Alignment.LEADING)
                                                            .addComponent(
                                                                          this.l_outputfile)
                                                            .addComponent(
                                                                          this.l_jobdesc)
                                                            .addComponent(
                                                                          this.l_backend)
                                                            .addComponent(
                                                                          this.l_resultdir))
                                          .addPreferredGap(
                                                           javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                          .addGroup(
                                                    contentPanelLayout
                                                            .createParallelGroup(
                                                                                 javax.swing.GroupLayout.Alignment.LEADING)
                                                            .addComponent(
                                                                          this.cb_backend,
                                                                          0,
                                                                          350,
                                                                          Short.MAX_VALUE)
                                                            .addGroup(
                                                                      javax.swing.GroupLayout.Alignment.TRAILING,
                                                                      contentPanelLayout
                                                                              .createSequentialGroup()
                                                                              .addComponent(
                                                                                            this.tb_resultdir,
                                                                                            javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                            292,
                                                                                            Short.MAX_VALUE)
                                                                              .addPreferredGap(
                                                                                               javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                              .addComponent(
                                                                                            this.btn_select_resultdir))
                                                            .addGroup(
                                                                      javax.swing.GroupLayout.Alignment.TRAILING,
                                                                      contentPanelLayout
                                                                              .createSequentialGroup()
                                                                              .addComponent(
                                                                                            this.tb_jobdescriptionfile,
                                                                                            javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                            292,
                                                                                            Short.MAX_VALUE)
                                                                              .addPreferredGap(
                                                                                               javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                              .addComponent(
                                                                                            this.btn_select_jobdesc))
                                                            .addGroup(
                                                                      contentPanelLayout
                                                                              .createSequentialGroup()
                                                                              .addComponent(
                                                                                            this.tb_outputfile,
                                                                                            javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                            292,
                                                                                            Short.MAX_VALUE)
                                                                              .addPreferredGap(
                                                                                               javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                              .addComponent(
                                                                                            this.btn_select_outputfile)))
                                          .addContainerGap()));
        contentPanelLayout
                .setVerticalGroup(contentPanelLayout
                        .createParallelGroup(
                                             javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(
                                  javax.swing.GroupLayout.Alignment.TRAILING,
                                  contentPanelLayout
                                          .createSequentialGroup()
                                          .addContainerGap(14, Short.MAX_VALUE)
                                          .addGroup(
                                                    contentPanelLayout
                                                            .createParallelGroup(
                                                                                 javax.swing.GroupLayout.Alignment.BASELINE)
                                                            .addComponent(
                                                                          this.l_backend)
                                                            .addComponent(
                                                                          this.cb_backend,
                                                                          javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                          javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                          javax.swing.GroupLayout.PREFERRED_SIZE))
                                          .addPreferredGap(
                                                           javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                          .addGroup(
                                                    contentPanelLayout
                                                            .createParallelGroup(
                                                                                 javax.swing.GroupLayout.Alignment.BASELINE)
                                                            .addComponent(
                                                                          this.btn_select_jobdesc)
                                                            .addComponent(
                                                                          this.tb_jobdescriptionfile,
                                                                          javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                          javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                          javax.swing.GroupLayout.PREFERRED_SIZE)
                                                            .addComponent(
                                                                          this.l_jobdesc))
                                          .addPreferredGap(
                                                           javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                          .addGroup(
                                                    contentPanelLayout
                                                            .createParallelGroup(
                                                                                 javax.swing.GroupLayout.Alignment.BASELINE)
                                                            .addComponent(
                                                                          this.tb_outputfile,
                                                                          javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                          javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                          javax.swing.GroupLayout.PREFERRED_SIZE)
                                                            .addComponent(
                                                                          this.l_outputfile)
                                                            .addComponent(
                                                                          this.btn_select_outputfile))
                                          .addPreferredGap(
                                                           javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                          .addGroup(
                                                    contentPanelLayout
                                                            .createParallelGroup(
                                                                                 javax.swing.GroupLayout.Alignment.BASELINE)
                                                            .addComponent(
                                                                          this.btn_select_resultdir)
                                                            .addComponent(
                                                                          this.tb_resultdir,
                                                                          javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                          javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                          javax.swing.GroupLayout.PREFERRED_SIZE)
                                                            .addComponent(
                                                                          this.l_resultdir))
                                          .addContainerGap()));

        final javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
                this.getContentPane());
        this.getContentPane().setLayout(layout);
        layout
                .setHorizontalGroup(layout
                        .createParallelGroup(
                                             javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(
                                  layout
                                          .createSequentialGroup()
                                          .addGroup(
                                                    layout
                                                            .createParallelGroup(
                                                                                 javax.swing.GroupLayout.Alignment.LEADING)
                                                            .addGroup(
                                                                      layout
                                                                              .createSequentialGroup()
                                                                              .addGap(
                                                                                      12,
                                                                                      12,
                                                                                      12)
                                                                              .addComponent(
                                                                                            this.l_creationtype)
                                                                              .addGap(
                                                                                      18,
                                                                                      18,
                                                                                      18)
                                                                              .addComponent(
                                                                                            this.cb_type,
                                                                                            javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                            151,
                                                                                            javax.swing.GroupLayout.PREFERRED_SIZE))
                                                            .addGroup(
                                                                      layout
                                                                              .createSequentialGroup()
                                                                              .addContainerGap()
                                                                              .addComponent(
                                                                                            this.contentPanel,
                                                                                            javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                            javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                            Short.MAX_VALUE))
                                                            .addGroup(
                                                                      layout
                                                                              .createSequentialGroup()
                                                                              .addGap(
                                                                                      166,
                                                                                      166,
                                                                                      166)
                                                                              .addComponent(
                                                                                            this.btnOk,
                                                                                            javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                            84,
                                                                                            javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                              .addPreferredGap(
                                                                                               javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                              .addComponent(
                                                                                            this.btnCancel,
                                                                                            javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                            84,
                                                                                            javax.swing.GroupLayout.PREFERRED_SIZE)))
                                          .addContainerGap()));
        layout
                .setVerticalGroup(layout
                        .createParallelGroup(
                                             javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(
                                  layout
                                          .createSequentialGroup()
                                          .addContainerGap()
                                          .addGroup(
                                                    layout
                                                            .createParallelGroup(
                                                                                 javax.swing.GroupLayout.Alignment.BASELINE)
                                                            .addComponent(
                                                                          this.l_creationtype)
                                                            .addComponent(
                                                                          this.cb_type,
                                                                          javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                          javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                          javax.swing.GroupLayout.PREFERRED_SIZE))
                                          .addGap(18, 18, 18)
                                          .addComponent(
                                                        this.contentPanel,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                          .addGap(18, 18, 18)
                                          .addGroup(
                                                    layout
                                                            .createParallelGroup(
                                                                                 javax.swing.GroupLayout.Alignment.BASELINE)
                                                            .addComponent(
                                                                          this.btnCancel)
                                                            .addComponent(this.btnOk))
                                          .addContainerGap(
                                                           javax.swing.GroupLayout.DEFAULT_SIZE,
                                                           Short.MAX_VALUE)));

        this.pack();
    } // </editor-fold>//GEN-END:initComponents

    // GEN-FIRST:event_btn_cancel
    private void btnCancel(final java.awt.event.ActionEvent evt) {
        this.dispose();
    } // GEN-LAST:event_btn_cancel

    // GEN-FIRST:event_btn_ok
    private void btnOk(final java.awt.event.ActionEvent evt) {
        // type is job
        if (this.cb_type.getSelectedIndex() == 0) {
            final String jdlFile = this.tb_jobdescriptionfile.getText();
            final String resultDir = this.tb_resultdir.getText();
            final String outputFile = this.tb_outputfile.getText();

            if ((new File(jdlFile)).exists()) {
                this.wmsxService.setBackend(this.cb_backend.getSelectedItem()
                        .toString());

                try {
                    final SubmissionResult s = this.wmsxService
                            .submitJdl(jdlFile,
                                       (outputFile.length() == 0) ? null
                                               : outputFile, resultDir);
                    System.out.println("SubmissionResult: " + s);

                } catch (final IOException e) {
                    System.out
                            .println(e.getMessage() + " " + e.getStackTrace());
                }

                this.setVisible(false);
                // businessman.refreshData();
                this.mainWindow.updateBusinessManager(true);

            } else {
                JOptionPane
                        .showMessageDialog(this,
                                           "JobDescriptionFile doesn't exist!",
                                           NewJob.WMSX_GUI_NEW_JOB,
                                           JOptionPane.ERROR_MESSAGE);
            }

        } else {
            // type is worker
            try {
                final int workercnt = Integer
                        .parseInt(this.tb_jobdescriptionfile.getText());

                if (workercnt > 0) {
                    this.wmsxService.setBackend(this.cb_backend
                            .getSelectedItem().toString());
                    this.wmsxService.startWorkers(workercnt);

                    this.setVisible(false);
                    // businessman.refreshData();
                    this.mainWindow.updateBusinessManager(true);

                } else {
                    JOptionPane
                            .showMessageDialog(
                                               this,
                                               "JobCount has to be greater than zero!",
                                               NewJob.WMSX_GUI_NEW_JOB,
                                               JOptionPane.ERROR_MESSAGE);
                }

            } catch (final NumberFormatException e) {
                JOptionPane
                        .showMessageDialog(this,
                                           "JobCount isn't an valid Integer!",
                                           NewJob.WMSX_GUI_NEW_JOB,
                                           JOptionPane.ERROR_MESSAGE);
            }
        }
    } // GEN-LAST:event_btn_ok

    private void cbTypeActionPerformed(final java.awt.event.ActionEvent evt) {
        if (this.cb_type.getSelectedIndex() == 0) {
            // type is job
            this.contentPanel.setBorder(javax.swing.BorderFactory
                    .createTitledBorder("Create a new Job"));
            this.updateBackendCombo();
            this.btn_select_jobdesc.setVisible(true);
            this.btn_select_outputfile.setVisible(true);
            this.btn_select_resultdir.setVisible(true);

            this.l_outputfile.setVisible(true);
            this.l_resultdir.setVisible(true);

            this.tb_outputfile.setVisible(true);
            this.tb_resultdir.setVisible(true);

            this.l_jobdesc.setText("JobDescriptionFile");
            this.tb_jobdescriptionfile.setText("");

        } else {
            // type is worker
            this.contentPanel.setBorder(javax.swing.BorderFactory
                    .createTitledBorder("Create a new Worker"));
            this.cb_backend.removeItem(new String("worker"));

            this.btn_select_jobdesc.setVisible(false);
            this.btn_select_outputfile.setVisible(false);
            this.btn_select_resultdir.setVisible(false);

            this.l_outputfile.setVisible(false);
            this.l_resultdir.setVisible(false);

            this.tb_outputfile.setVisible(false);
            this.tb_resultdir.setVisible(false);

            this.l_jobdesc.setText("WorkerCount");
            this.tb_jobdescriptionfile.setText("1");
        }
        this.pack();
    }

    private void btnSelectJobdescActionPerformed(
            final java.awt.event.ActionEvent evt) {
        final JFileChooser fc = new JFileChooser();
        // fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        // Add a custom file filter and disable the default
        // (Accept All) file filter.
        fc.addChoosableFileFilter(new JobDescriptionFileFilter());
        fc.setAcceptAllFileFilterUsed(false);

        final int returnVal = fc.showOpenDialog(this);
        if (returnVal == 0) {
            this.tb_jobdescriptionfile.setText(fc.getSelectedFile()
                    .getAbsolutePath());
            this.tb_resultdir.setText(fc.getSelectedFile().getParentFile()
                    .getAbsolutePath());
        }
    }

    private void btnSelectOutputfileActionPerformed(
            final java.awt.event.ActionEvent evt) {
        final JFileChooser fc = new JFileChooser();

        final int returnVal = fc.showSaveDialog(this);
        if (returnVal == 0) {
            this.tb_outputfile.setText(fc.getSelectedFile().getAbsolutePath());
        }
    }

    private void btnSelectResultdirActionPerformed(
            final java.awt.event.ActionEvent evt) {
        final JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        final int returnVal = fc.showOpenDialog(this);
        if (returnVal == 0) {
            this.tb_resultdir.setText(fc.getSelectedFile().getAbsolutePath());
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOk;
    private javax.swing.JButton btn_select_jobdesc;
    private javax.swing.JButton btn_select_outputfile;
    private javax.swing.JButton btn_select_resultdir;
    private javax.swing.JComboBox cb_backend;
    private javax.swing.JComboBox cb_type;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JLabel l_backend;
    private javax.swing.JLabel l_creationtype;
    private javax.swing.JLabel l_jobdesc;
    private javax.swing.JLabel l_outputfile;
    private javax.swing.JLabel l_resultdir;
    private javax.swing.JTextField tb_jobdescriptionfile;
    private javax.swing.JTextField tb_outputfile;
    private javax.swing.JTextField tb_resultdir;
    // End of variables declaration//GEN-END:variables

}
