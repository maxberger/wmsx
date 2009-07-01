/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MainWindow.java
 *
 * Created on 19.03.2009, 15:22:12
 */

package at.ac.uibk.dps.wmsxgui.presentation;

import hu.kfki.grid.wmsx.TransportJobUID;
import hu.kfki.grid.wmsx.Wmsx;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.Enumeration;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import net.jini.id.Uuid;
import at.ac.uibk.dps.wmsxgui.business.BusinessManager;
import at.ac.uibk.dps.wmsxgui.business.JobData;
import at.ac.uibk.dps.wmsxgui.business.JobTableModel;
import at.ac.uibk.dps.wmsxgui.presentation.util.MyTreeCellRenderer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * 
 * @author WmsxGUI Team
 * @version 1.0
 */
public class MainWindow extends javax.swing.JFrame implements Observer {
    private static final long serialVersionUID = -1929304607781335690L;

    private final BusinessManager businessman;
    private Wmsx wmsxService;
    private DefaultMutableTreeNode rootNode;
    private DefaultTreeModel treeModel;
    private JobData currentJobData;
    private static final String DATEFORMATSTR = "HH:mm:ss - EEE d. MMM yyyy";

    /**
     * MainWindow creates a new main frame window.
     */
    public MainWindow() {
        this.businessman = BusinessManager.getInstance();
        this.wmsxService = this.businessman.getWmsxService();

        this.businessman.addObserver(this);

        this.initComponents();

        this.table_jobs.setModel(new JobTableModel());
        this.updateTreeModel();

        if (!this.businessman.isOnline())
            setGUIOfflineMode(false);
        else
            setGUIOnlineMode();

        // verstecke tabelle
        // panel_table.setVisible(false);
        this.panel_jobdetails.setVisible(false);

        // disable remove buttons
        this.shortcutRemoveHard.setEnabled(false);
        this.shortcutRemoveSoft.setEnabled(false);
        this.btnKill.setEnabled(false);
        this.btnStop.setEnabled(false);

        this.treeJobs.setCellRenderer(new MyTreeCellRenderer());

        // Set icon
        final Image icon = (new ImageIcon(this.getClass()
                .getResource("/icon.png"))).getImage();
        this.setIconImage(icon);

        this.centerScreen();
    }

    private void setGUIOnlineMode()
    {
        this.setTitle("WMSX GUI");

        panel_table.setVisible(true);
        this.panel_jobdetails.setVisible(false);

        this.menuItemNewjob.setEnabled(true);
        this.menuItemOptions.setEnabled(true);
        this.menuItemStopWorkers.setEnabled(true);

        this.shortcutAdd.setEnabled(true);
        this.shortcutPing.setEnabled(true);
        this.shortcutCleanup.setEnabled(true);
        this.shortcutRefresh.setEnabled(true);
        this.shortcutRemoveHard.setEnabled(false);
        this.shortcutRemoveSoft.setEnabled(false);

        this.btnKill.setEnabled(false);
        this.btnRefresh.setEnabled(true);
        this.btnCleanup.setEnabled(true);
        this.btnStop.setEnabled(false);

        this.menuItemReconnect.setEnabled(false);
        updateTreeModel();
        this.treeJobs.setEnabled(true);
    }

    private void setGUIOfflineMode(boolean showMessage)
    {
        if (showMessage)
            JOptionPane.showMessageDialog(this, "Connection to provider lost!\nSwitch to offline mode.",
                                              "WMSX GUI - Connection lost",
                                              JOptionPane.ERROR_MESSAGE);

        this.setTitle("WMSX GUI - Offline Mode");

        this.menuItemNewjob.setEnabled(false);
        this.menuItemOptions.setEnabled(false);
        this.menuItemStopWorkers.setEnabled(false);

        this.shortcutAdd.setEnabled(false);
        this.shortcutPing.setEnabled(false);
        this.shortcutCleanup.setEnabled(false);
        this.shortcutRefresh.setEnabled(false);
        this.shortcutRemoveHard.setEnabled(false);
        this.shortcutRemoveSoft.setEnabled(false);

        this.btnKill.setEnabled(false);
        this.btnRefresh.setEnabled(false);
        this.btnCleanup.setEnabled(false);
        this.btnStop.setEnabled(false);

        this.menuItemReconnect.setEnabled(true);
        this.treeJobs.setEnabled(false);
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
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        jPanelRight = new javax.swing.JPanel();
        panel_top = new javax.swing.JPanel();
        panel_table = new javax.swing.JScrollPane();
        table_jobs = new javax.swing.JTable();
        panel_jobdetails = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        tb_jobdetails_jobuid = new javax.swing.JTextField();
        tb_jobdetails_state = new javax.swing.JTextField();
        tb_jobdetails_siteid = new javax.swing.JTextField();
        tb_jobdetails_creationtime = new javax.swing.JTextField();
        tb_jobdetails_startedtime = new javax.swing.JTextField();
        tb_jobdetails_donetime = new javax.swing.JTextField();
        panel_description = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        tb_jobdetails_executable = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        tb_jobdetails_output = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        tb_jobdetails_description = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        tb_jobdetails_workerID = new javax.swing.JTextField();
        panel_buttons = new javax.swing.JPanel();
        l_selectedjob = new javax.swing.JLabel();
        btnKill = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        btnCleanup = new javax.swing.JButton();
        btnStop = new javax.swing.JButton();
        sp_tree = new javax.swing.JScrollPane();
        treeJobs = new javax.swing.JTree();
        toolbar_main = new javax.swing.JToolBar();
        shortcutAdd = new javax.swing.JButton();
        shortcutRemoveSoft = new javax.swing.JButton();
        shortcutRemoveHard = new javax.swing.JButton();
        shortcutPing = new javax.swing.JButton();
        shortcutRefresh = new javax.swing.JButton();
        shortcutCleanup = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        menuFile = new javax.swing.JMenu();
        menuItemNewjob = new javax.swing.JMenuItem();
        menuItemStopWorkers = new javax.swing.JMenuItem();
        menuItemOptions = new javax.swing.JMenuItem();
        menuItemReconnect = new javax.swing.JMenuItem();
        menuItemExit = new javax.swing.JMenuItem();
        menuHelp = new javax.swing.JMenu();
        menuItemAbout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("WMSX GUI");
        setMinimumSize(new java.awt.Dimension(500, 424));

        jSplitPane1.setDividerSize(5);

        jPanelRight.setMinimumSize(new java.awt.Dimension(360, 0));
        jPanelRight.setPreferredSize(new java.awt.Dimension(200, 200));

        panel_top.setMinimumSize(new java.awt.Dimension(360, 250));
        panel_top.setPreferredSize(new java.awt.Dimension(596, 250));

        panel_table.setAutoscrolls(true);
        panel_table.setMinimumSize(new java.awt.Dimension(300, 25));

        table_jobs.setAutoCreateRowSorter(true);
        table_jobs.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        table_jobs.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                table_jobsMouseClicked(evt);
            }
        });
        panel_table.setViewportView(table_jobs);

        panel_jobdetails.setBorder(javax.swing.BorderFactory.createTitledBorder("Job Details"));
        panel_jobdetails.setMinimumSize(new java.awt.Dimension(300, 241));

        jLabel1.setText("JobUID");

        jLabel2.setText("State");

        jLabel3.setText("SiteID");

        jLabel4.setText("Created");

        jLabel5.setText("Started");

        jLabel6.setText("Finished");

        tb_jobdetails_jobuid.setEditable(false);

        tb_jobdetails_state.setEditable(false);

        tb_jobdetails_siteid.setEditable(false);

        tb_jobdetails_creationtime.setEditable(false);

        tb_jobdetails_startedtime.setEditable(false);

        tb_jobdetails_donetime.setEditable(false);

        jLabel7.setText("Executable");

        tb_jobdetails_executable.setEditable(false);

        jLabel9.setText("Output");

        tb_jobdetails_output.setEditable(false);

        jLabel10.setText("Description");

        tb_jobdetails_description.setEditable(false);

        javax.swing.GroupLayout panel_descriptionLayout = new javax.swing.GroupLayout(panel_description);
        panel_description.setLayout(panel_descriptionLayout);
        panel_descriptionLayout.setHorizontalGroup(
            panel_descriptionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_descriptionLayout.createSequentialGroup()
                .addGroup(panel_descriptionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_descriptionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tb_jobdetails_executable, javax.swing.GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE)
                    .addComponent(tb_jobdetails_output, javax.swing.GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE)
                    .addComponent(tb_jobdetails_description, javax.swing.GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE)))
        );
        panel_descriptionLayout.setVerticalGroup(
            panel_descriptionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_descriptionLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(panel_descriptionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tb_jobdetails_executable, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_descriptionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(tb_jobdetails_output, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_descriptionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(tb_jobdetails_description, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jLabel8.setText("Worker?");

        tb_jobdetails_workerID.setEditable(false);

        javax.swing.GroupLayout panel_jobdetailsLayout = new javax.swing.GroupLayout(panel_jobdetails);
        panel_jobdetails.setLayout(panel_jobdetailsLayout);
        panel_jobdetailsLayout.setHorizontalGroup(
            panel_jobdetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_jobdetailsLayout.createSequentialGroup()
                .addGroup(panel_jobdetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panel_jobdetailsLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(panel_description, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panel_jobdetailsLayout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addGroup(panel_jobdetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel_jobdetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panel_jobdetailsLayout.createSequentialGroup()
                                .addGroup(panel_jobdetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(tb_jobdetails_jobuid, javax.swing.GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE)
                                    .addComponent(tb_jobdetails_state, javax.swing.GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE)
                                    .addComponent(tb_jobdetails_siteid, javax.swing.GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE))
                                .addGap(13, 13, 13)
                                .addGroup(panel_jobdetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panel_jobdetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(tb_jobdetails_creationtime, javax.swing.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)
                                    .addComponent(tb_jobdetails_startedtime, javax.swing.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)
                                    .addComponent(tb_jobdetails_donetime, javax.swing.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE)))
                            .addComponent(tb_jobdetails_workerID, javax.swing.GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE))))
                .addContainerGap())
        );
        panel_jobdetailsLayout.setVerticalGroup(
            panel_jobdetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_jobdetailsLayout.createSequentialGroup()
                .addGroup(panel_jobdetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(tb_jobdetails_jobuid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(tb_jobdetails_creationtime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_jobdetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tb_jobdetails_state, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel5)
                    .addComponent(tb_jobdetails_startedtime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(panel_jobdetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(tb_jobdetails_siteid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(tb_jobdetails_donetime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_jobdetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(tb_jobdetails_workerID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel_description, javax.swing.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panel_topLayout = new javax.swing.GroupLayout(panel_top);
        panel_top.setLayout(panel_topLayout);
        panel_topLayout.setHorizontalGroup(
            panel_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel_table, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 578, Short.MAX_VALUE)
            .addGroup(panel_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(panel_jobdetails, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel_topLayout.setVerticalGroup(
            panel_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel_table, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
            .addGroup(panel_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(panel_jobdetails, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panel_buttons.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        panel_buttons.setMinimumSize(new java.awt.Dimension(360, 68));

        l_selectedjob.setText("job/worker");

        btnKill.setText("kill");
        btnKill.setToolTipText("Kill Worker (hard)");
        btnKill.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKillActionPerformed(evt);
            }
        });

        btnRefresh.setText("refresh");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });

        btnCleanup.setText("cleanup");
        btnCleanup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCleanupActionPerformed(evt);
            }
        });

        btnStop.setText("shutdown");
        btnStop.setToolTipText("Cancel Job or shutdown Worker (soft)");
        btnStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStopActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panel_buttonsLayout = new javax.swing.GroupLayout(panel_buttons);
        panel_buttons.setLayout(panel_buttonsLayout);
        panel_buttonsLayout.setHorizontalGroup(
            panel_buttonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_buttonsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_buttonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(l_selectedjob)
                    .addGroup(panel_buttonsLayout.createSequentialGroup()
                        .addComponent(btnStop, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnKill)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRefresh)
                        .addGap(6, 6, 6)
                        .addComponent(btnCleanup)))
                .addContainerGap(229, Short.MAX_VALUE))
        );

        panel_buttonsLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnCleanup, btnRefresh});

        panel_buttonsLayout.setVerticalGroup(
            panel_buttonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_buttonsLayout.createSequentialGroup()
                .addComponent(l_selectedjob)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_buttonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnKill)
                    .addComponent(btnStop)
                    .addComponent(btnRefresh)
                    .addComponent(btnCleanup))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanelRightLayout = new javax.swing.GroupLayout(jPanelRight);
        jPanelRight.setLayout(jPanelRightLayout);
        jPanelRightLayout.setHorizontalGroup(
            jPanelRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel_top, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 578, Short.MAX_VALUE)
            .addComponent(panel_buttons, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanelRightLayout.setVerticalGroup(
            jPanelRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelRightLayout.createSequentialGroup()
                .addComponent(panel_top, javax.swing.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel_buttons, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jSplitPane1.setRightComponent(jPanelRight);

        sp_tree.setMinimumSize(new java.awt.Dimension(100, 25));

        treeJobs.setModel(treeModel);
        treeJobs.setMinimumSize(new java.awt.Dimension(100, 100));
        treeJobs.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                treeJobsValueChanged(evt);
            }
        });
        sp_tree.setViewportView(treeJobs);

        jSplitPane1.setLeftComponent(sp_tree);

        toolbar_main.setFloatable(false);
        toolbar_main.setRollover(true);

        shortcutAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/add.png"))); // NOI18N
        shortcutAdd.setToolTipText("Add a new Job or Worker");
        shortcutAdd.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        shortcutAdd.setFocusable(false);
        shortcutAdd.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        shortcutAdd.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        shortcutAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdd(evt);
            }
        });
        toolbar_main.add(shortcutAdd);

        shortcutRemoveSoft.setIcon(new javax.swing.ImageIcon(getClass().getResource("/removesoft.png"))); // NOI18N
        shortcutRemoveSoft.setToolTipText("Cancel Job or shutdown Worker (soft)");
        shortcutRemoveSoft.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        shortcutRemoveSoft.setFocusable(false);
        shortcutRemoveSoft.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        shortcutRemoveSoft.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        shortcutRemoveSoft.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStopActionPerformed(evt);
            }
        });
        toolbar_main.add(shortcutRemoveSoft);

        shortcutRemoveHard.setIcon(new javax.swing.ImageIcon(getClass().getResource("/removehard.png"))); // NOI18N
        shortcutRemoveHard.setToolTipText("Kill Worker (hard)");
        shortcutRemoveHard.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        shortcutRemoveHard.setFocusable(false);
        shortcutRemoveHard.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        shortcutRemoveHard.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        shortcutRemoveHard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKillActionPerformed(evt);
            }
        });
        toolbar_main.add(shortcutRemoveHard);

        shortcutPing.setText("Ping");
        shortcutPing.setToolTipText("Test Connection to Provider");
        shortcutPing.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPing(evt);
            }
        });
        toolbar_main.add(shortcutPing);

        shortcutRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/view-refresh.png"))); // NOI18N
        shortcutRefresh.setToolTipText("Refresh");
        shortcutRefresh.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        shortcutRefresh.setFocusable(false);
        shortcutRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        shortcutRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        shortcutRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                shortcutRefreshActionPerformed(evt);
            }
        });
        toolbar_main.add(shortcutRefresh);

        shortcutCleanup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/edit-clear.png"))); // NOI18N
        shortcutCleanup.setToolTipText("Cleanup");
        shortcutCleanup.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        shortcutCleanup.setFocusable(false);
        shortcutCleanup.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        shortcutCleanup.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        shortcutCleanup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                shortcutCleanupActionPerformed(evt);
            }
        });
        toolbar_main.add(shortcutCleanup);

        menuFile.setText("File");

        menuItemNewjob.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        menuItemNewjob.setIcon(new javax.swing.ImageIcon(getClass().getResource("/addsmall.png"))); // NOI18N
        menuItemNewjob.setText("New Job");
        menuItemNewjob.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemNewjobActionPerformed(evt);
            }
        });
        menuFile.add(menuItemNewjob);

        menuItemStopWorkers.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_K, java.awt.event.InputEvent.CTRL_MASK));
        menuItemStopWorkers.setIcon(new javax.swing.ImageIcon(getClass().getResource("/removesmall.png"))); // NOI18N
        menuItemStopWorkers.setText("Stop all Workers");
        menuItemStopWorkers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemStopWorkersActionPerformed(evt);
            }
        });
        menuFile.add(menuItemStopWorkers);

        menuItemOptions.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        menuItemOptions.setIcon(new javax.swing.ImageIcon(getClass().getResource("/optionssmall.png"))); // NOI18N
        menuItemOptions.setText("Options");
        menuItemOptions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemOptionsActionPerformed(evt);
            }
        });
        menuFile.add(menuItemOptions);

        menuItemReconnect.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
        menuItemReconnect.setIcon(new javax.swing.ImageIcon(getClass().getResource("/reconnect.png"))); // NOI18N
        menuItemReconnect.setText("Reconnect");
        menuItemReconnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemReconnectActionPerformed(evt);
            }
        });
        menuFile.add(menuItemReconnect);

        menuItemExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
        menuItemExit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/exit.png"))); // NOI18N
        menuItemExit.setText("Exit");
        menuItemExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemExitActionPerformed(evt);
            }
        });
        menuFile.add(menuItemExit);

        jMenuBar1.add(menuFile);

        menuHelp.setText("Help");

        menuItemAbout.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.CTRL_MASK));
        menuItemAbout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/help-browser.png"))); // NOI18N
        menuItemAbout.setText("About");
        menuItemAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemAboutActionPerformed(evt);
            }
        });
        menuHelp.add(menuItemAbout);

        jMenuBar1.add(menuHelp);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 685, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(toolbar_main, javax.swing.GroupLayout.DEFAULT_SIZE, 709, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(toolbar_main, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 367, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void menuItemAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemAboutActionPerformed
            final About dialog = new About(this, rootPaneCheckingEnabled);
            // System.out.println("MainWindow: Show NewJobDialog...");
            dialog.setVisible(true);
    }//GEN-LAST:event_menuItemAboutActionPerformed

    private void menuItemReconnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemReconnectActionPerformed
        if (!this.businessman.isOnline())
        {
            //System.out.println("MainWindow try reconnect...");
            this.businessman.reConnect(true);

            if (this.businessman.isOnline())
            {
                this.wmsxService = this.businessman.getWmsxService();
                setGUIOnlineMode();
                
            }else
                setGUIOfflineMode(false);
        }
    }//GEN-LAST:event_menuItemReconnectActionPerformed

    private void shortcutRefreshActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_shortcutRefreshActionPerformed
        this.updateBusinessManager(true);
    }// GEN-LAST:event_shortcutRefreshActionPerformed

    private void shortcutCleanupActionPerformed(
            final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_shortcutCleanupActionPerformed
        this.updateBusinessManager(false);
    }// GEN-LAST:event_shortcutCleanupActionPerformed

    // GEN-FIRST:event_btn_ping
    private void btnPing(final java.awt.event.ActionEvent evt) {
        if (this.businessman.isOnline()) {
            final boolean ping = this.wmsxService.ping(false);
            final boolean fullping = this.wmsxService.ping(true);

            if (ping && fullping) {
                JOptionPane.showMessageDialog(this, "Ping to provider was ok!",
                                              "WMSX GUI - Ping",
                                              JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Ping to provider failed!\nSwitch to offline mode.",
                                              "WMSX GUI - Ping",
                                              JOptionPane.ERROR_MESSAGE);

                setGUIOfflineMode(false);
            }
        } else
        {
                setGUIOfflineMode(true);
        }
    }

    // GEN-LAST:event_btn_ping

    // GEN-FIRST:event_btn_add
    private void btnAdd(final java.awt.event.ActionEvent evt) {
        if (this.businessman.isOnline()) {

            final NewJob newjob = new NewJob(this, rootPaneCheckingEnabled);
            System.out.println("Show NewJobDialog...");
            newjob.setVisible(true);
        }else
            setGUIOfflineMode(true);
    }

    // GEN-LAST:event_btn_add

    // GEN-FIRST:event_menu_item_optionsActionPerformed
    private void menuItemOptionsActionPerformed(
            final java.awt.event.ActionEvent evt) {
        if (this.businessman.isOnline()) {
            final Options optionen = new Options(this, rootPaneCheckingEnabled);
            optionen.setVisible(true);
        }else
            setGUIOfflineMode(true);
    }

    // GEN-LAST:event_menu_item_optionsActionPerformed

    // GEN-FIRST:event_menu_item_stopserverActionPerformed
    private void menuItemStopWorkersActionPerformed(
            final java.awt.event.ActionEvent evt) {
        if (this.businessman.isOnline()) {
            this.wmsxService.shutdownWorkers();
        }else
            setGUIOfflineMode(true);
    }

    // GEN-LAST:event_menu_item_stopserverActionPerformed

    // GEN-FIRST:event_menu_item_exitActionPerformed
    private void menuItemExitActionPerformed(
            final java.awt.event.ActionEvent evt) {
        System.exit(0);
    }

    // GEN-LAST:event_menu_item_exitActionPerformed

    // GEN-FIRST:event_menu_item_newjobActionPerformed
    private void menuItemNewjobActionPerformed(
            final java.awt.event.ActionEvent evt) {
        if (this.businessman.isOnline()) {
            final NewJob newjob = new NewJob(this, rootPaneCheckingEnabled);
            // System.out.println("MainWindow: Show NewJobDialog...");
            newjob.setVisible(true);
        }else
            setGUIOfflineMode(true);
    }

    // GEN-LAST:event_menu_item_newjobActionPerformed

    // GEN-FIRST:eventStopActionPerformed
    private void btnStopActionPerformed(final java.awt.event.ActionEvent evt) {
        // remove Job or Worker (soft)
        if (this.businessman.isOnline()) {
            if (this.currentJobData != null) {
                if (this.currentJobData.getJobinfo().isWorker()) {
                    this.businessman.getWmsxService()
                            .shutdownWorker(
                                            this.currentJobData.getJobinfo()
                                                    .getWorkerId());
                } else {
                    this.businessman.getWmsxService()
                            .cancelJob(this.currentJobData.getTransportJobUID());
                }

            }
        }else
            setGUIOfflineMode(true);
    }

    // GEN-LAST:eventStopActionPerformed

    // GEN-FIRST:event_btn_killActionPerformed
    private void btnKillActionPerformed(final java.awt.event.ActionEvent evt) {
        // remove Worker (hard)
        if (this.businessman.isOnline()) {
            if ((this.currentJobData != null)
                    && this.currentJobData.getJobinfo().isWorker()) {
                this.businessman.getWmsxService()
                        .cancelJob(this.currentJobData.getTransportJobUID());
            }
        }else
            setGUIOfflineMode(true);
    }

    // GEN-LAST:event_btn_killActionPerformed

    // GEN-FIRST:event_btn_refreshActionPerformed
    private void btnRefreshActionPerformed(final java.awt.event.ActionEvent evt) {

        this.updateBusinessManager(true);
    }

    // GEN-LAST:event_btn_refreshActionPerformed

    // GEN-FIRST:event_btn_cleanupActionPerformed
    private void btnCleanupActionPerformed(final java.awt.event.ActionEvent evt) {
        this.updateBusinessManager(false);
    }

    // GEN-LAST:event_btn_cleanupActionPerformed

    // GEN-FIRST:event_tree_jobsValueChanged
    private void treeJobsValueChanged(
            final javax.swing.event.TreeSelectionEvent evt) {
        final DefaultMutableTreeNode node = (DefaultMutableTreeNode) this.treeJobs
                .getLastSelectedPathComponent();

        if (node != null) {
            if (node.getUserObject().getClass().getSimpleName()
                    .equals("JobData")) {

                this.currentJobData = (JobData) node.getUserObject();
                this.setJobDetails();

                // change top panel
                this.panel_table.setVisible(false);
                this.panel_jobdetails.setVisible(true);

                // enable remove buttons
                if (this.currentJobData.getJobinfo().isWorker()) {
                    this.shortcutRemoveHard.setEnabled(true);
                    this.btnKill.setEnabled(true);
                    this.btnStop.setText("shutdown");
                } else {
                    this.shortcutRemoveHard.setEnabled(false);
                    this.btnKill.setEnabled(false);
                    this.btnStop.setText("cancel");
                }

                this.shortcutRemoveSoft.setEnabled(true);
                this.btnStop.setEnabled(true);

            } else {
                this.businessman.setCurrentBackend(node.getUserObject()
                        .toString());

                this.panel_table.setVisible(true);
                this.panel_jobdetails.setVisible(false);

                // disable remove buttons
                this.shortcutRemoveHard.setEnabled(false);
                this.shortcutRemoveSoft.setEnabled(false);
                this.btnKill.setEnabled(false);
                this.btnStop.setEnabled(false);
            }

            this.l_selectedjob.setText(node.getUserObject().toString());
        }
    }

    // GEN-LAST:event_tree_jobsValueChanged

    // GEN-FIRST:event_table_jobsMouseClicked
    private void table_jobsMouseClicked(final java.awt.event.MouseEvent evt) {

        final int row = this.table_jobs.getSelectedRow();
        final TransportJobUID jobUid = (TransportJobUID) this.table_jobs
                .getValueAt(row, 0);
        this.l_selectedjob.setText(jobUid.toString());

        this.currentJobData = this.businessman.getJobData(jobUid);

        // enable remove buttons
        if (this.currentJobData.getJobinfo().isWorker()) {
            this.shortcutRemoveHard.setEnabled(true);
            this.btnKill.setEnabled(true);
            this.btnStop.setText("shutdown");
        } else {
            this.shortcutRemoveHard.setEnabled(false);
            this.btnKill.setEnabled(false);
            this.btnStop.setText("cancel");
        }
        this.shortcutRemoveSoft.setEnabled(true);
        this.btnStop.setEnabled(true);

        if (evt.getClickCount() > 1) {
            // System.out.println("doubleclick");

            // select Item in the Tree
            final DefaultMutableTreeNode root = (DefaultMutableTreeNode) this.treeJobs
                    .getModel().getRoot();

            final Enumeration enumeration = root.children();
            while (enumeration.hasMoreElements()) {
                final DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumeration
                        .nextElement();
                final String backend = (String) node.getUserObject();
                if (backend.equals(jobUid.getBackend())) {
                    final Enumeration enumeration2 = node.children();
                    while (enumeration2.hasMoreElements()) {
                        final DefaultMutableTreeNode node2 = (DefaultMutableTreeNode) enumeration2
                                .nextElement();
                        final JobData jobData = (JobData) node2.getUserObject();

                        if (jobData.getTransportJobUID().equals(jobUid)) {
                            this.treeJobs.setSelectionPath(new TreePath(node2
                                    .getPath()));
                        }
                    }

                }

            }

        }
    }

    // GEN-LAST:event_table_jobsMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCleanup;
    private javax.swing.JButton btnKill;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnStop;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanelRight;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JLabel l_selectedjob;
    private javax.swing.JMenu menuFile;
    private javax.swing.JMenu menuHelp;
    private javax.swing.JMenuItem menuItemAbout;
    private javax.swing.JMenuItem menuItemExit;
    private javax.swing.JMenuItem menuItemNewjob;
    private javax.swing.JMenuItem menuItemOptions;
    private javax.swing.JMenuItem menuItemReconnect;
    private javax.swing.JMenuItem menuItemStopWorkers;
    private javax.swing.JPanel panel_buttons;
    private javax.swing.JPanel panel_description;
    private javax.swing.JPanel panel_jobdetails;
    private javax.swing.JScrollPane panel_table;
    private javax.swing.JPanel panel_top;
    private javax.swing.JButton shortcutAdd;
    private javax.swing.JButton shortcutCleanup;
    private javax.swing.JButton shortcutPing;
    private javax.swing.JButton shortcutRefresh;
    private javax.swing.JButton shortcutRemoveHard;
    private javax.swing.JButton shortcutRemoveSoft;
    private javax.swing.JScrollPane sp_tree;
    private javax.swing.JTable table_jobs;
    private javax.swing.JTextField tb_jobdetails_creationtime;
    private javax.swing.JTextField tb_jobdetails_description;
    private javax.swing.JTextField tb_jobdetails_donetime;
    private javax.swing.JTextField tb_jobdetails_executable;
    private javax.swing.JTextField tb_jobdetails_jobuid;
    private javax.swing.JTextField tb_jobdetails_output;
    private javax.swing.JTextField tb_jobdetails_siteid;
    private javax.swing.JTextField tb_jobdetails_startedtime;
    private javax.swing.JTextField tb_jobdetails_state;
    private javax.swing.JTextField tb_jobdetails_workerID;
    private javax.swing.JToolBar toolbar_main;
    private javax.swing.JTree treeJobs;
    // End of variables declaration//GEN-END:variables

    private void updateTreeModel() {
        System.out.println("MainWindow: updateTreeModel...");

        this.rootNode = new DefaultMutableTreeNode("Backends");

        if (this.businessman.isOnline()) {
            for (final String backend : this.businessman.getBackends()) {
                // System.out.println(backend);
                // add Backend
                final DefaultMutableTreeNode backendnode = new DefaultMutableTreeNode(
                        backend);
                // add jobs for backend
                for (final JobData jobdata : this.businessman.getJobs(backend)) {
                    backendnode.add(new DefaultMutableTreeNode(jobdata));
                }

                this.rootNode.add(backendnode);

            }

            this.treeModel = new DefaultTreeModel(this.rootNode);
            this.treeJobs.setModel(this.treeModel);

            // restore Expanded Nodes
            for (final int row : this.businessman.getExpansionStateRows()) {
                System.out.println("MainWindow: setExpandedRow: " + row);
                this.treeJobs.expandRow(row);
            }

        } else { // offline Demo Mode

            DefaultMutableTreeNode node1 = new DefaultMutableTreeNode("Fake");

            this.rootNode.add(node1);
            node1 = new DefaultMutableTreeNode("Worker");
            this.rootNode.add(node1);
            node1 = new DefaultMutableTreeNode("Local");
            this.rootNode.add(node1);
            node1 = new DefaultMutableTreeNode("Gat");
            this.rootNode.add(node1);

            this.treeModel = new DefaultTreeModel(this.rootNode);
            this.treeJobs.setModel(this.treeModel);

        }

        // tree_jobs.updateUI();
    }

    /**
     * update will be executed, when an observable sends this event, so the
     * treeModel has to be updated.
     * @param o Observable, which sends the event
     * @param obj Some object, which has changed
     */
    @Override
    public void update(final Observable o, final Object obj) {
        if ((obj == null) || (!obj.getClass().getSimpleName().equals("String"))) {
            System.out.println("MainWindow: updateObserver...");
            this.businessman.saveExpansionState(this.treeJobs);
            this.updateTreeModel();

            if ((obj != null)
                    && (obj.getClass().getSimpleName().equals("JobData"))) {
                final JobData job = (JobData) obj;
                if ((this.panel_jobdetails.isVisible())
                        && (this.tb_jobdetails_jobuid.getText().equals(job
                                .getTransportJobUID().toString()))) {
                    this.currentJobData = job;
                    this.setJobDetails();
                }
            }
        }
    }

    /**
     * updateBusinessManager tries to update the BusinessManager.
     * @param keepOldData if true keep old data and refresh, otherwise do cleanup
     */
    public void updateBusinessManager(final boolean keepOldData) {
        if (this.businessman.isOnline())
        {
            if (keepOldData) {
                this.businessman.refreshBusinessData();
            } else {
                this.businessman.cleanupBusinessData();
            }
        } else {
            this.setGUIOfflineMode(true);
        }
    }

    private void setJobDetails() {
        if (this.currentJobData != null) {
            System.out.println("MainWindow: setJobDetails.. job: "
                    + this.currentJobData);

            final DateFormat dateFormat = new SimpleDateFormat(
                    this.DATEFORMATSTR);

            this.tb_jobdetails_jobuid.setText(this.currentJobData
                    .getTransportJobUID().toString());
            this.tb_jobdetails_state.setText(this.currentJobData.getJobinfo()
                    .getStatus().toString());
            this.tb_jobdetails_siteid.setText(this.currentJobData.getJobinfo()
                    .getSiteId());

            this.tb_jobdetails_creationtime.setText((this.currentJobData
                    .getJobinfo().getCreationTime() != null) ? dateFormat
                    .format(this.currentJobData.getJobinfo().getCreationTime())
                    : "");
            this.tb_jobdetails_startedtime.setText((this.currentJobData
                    .getJobinfo().getStartRunningTime() != null) ? dateFormat
                    .format(this.currentJobData.getJobinfo()
                            .getStartRunningTime()) : "");
            this.tb_jobdetails_donetime.setText((this.currentJobData
                    .getJobinfo().getDoneRunningTime() != null) ? dateFormat
                    .format(this.currentJobData.getJobinfo()
                            .getDoneRunningTime()) : "");

            this.tb_jobdetails_executable.setText(this.currentJobData
                    .getJobinfo().getExecutable());
            this.tb_jobdetails_output.setText(this.currentJobData.getJobinfo()
                    .getOutput());
            this.tb_jobdetails_description.setText(this.currentJobData
                    .getJobinfo().getDescription());

            final boolean isworker = this.currentJobData.getJobinfo()
                    .isWorker();
            final Uuid workerID = this.currentJobData.getJobinfo()
                    .getWorkerId();

            final String jobtxt = "I am a job executed without a worker";
            final String workertxt = "I am a worker my workerID is: ";
            final String workerjob = "I am job executed by workerID: ";

            String workerinfo = new String();
            if (!isworker && (workerID == null)) { // normaler job
                workerinfo = jobtxt;
            }
            if (!isworker && (workerID != null)) { // job mit worker
                workerinfo = workerjob + workerID;
            }
            if (isworker && (workerID != null)) { // worker
                workerinfo = workertxt + workerID;
            }
            this.tb_jobdetails_workerID.setText(workerinfo);

        }
    }
}
