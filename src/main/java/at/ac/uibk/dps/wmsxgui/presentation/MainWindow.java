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

import at.ac.uibk.dps.wmsxgui.presentation.util.MyTreeCellRenderer;
import at.ac.uibk.dps.wmsxgui.business.BusinessManager;
import at.ac.uibk.dps.wmsxgui.business.JobData;
import hu.kfki.grid.wmsx.Wmsx;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.Observable;
import java.util.Observer;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author bafu
 */
public class MainWindow extends javax.swing.JFrame implements Observer {

    private BusinessManager businessman;
    private Wmsx wmsx_service;
    private JFrame optionen;
    private DefaultMutableTreeNode rootNode;
    private DefaultTreeModel treeModel;


    /** Creates new form MainWindow */
    public MainWindow() {
        this.businessman = BusinessManager.getInstance();
        wmsx_service = businessman.getWmsxService();

        businessman.addObserver(this);
        
        initComponents();

        updateTreeModel();
        
        if (!businessman.isOnline())
        {
            setTitle("WMSX GUI - Offline Demo Mode");

            menu_item_newjob.setEnabled(false);
            menu_item_options.setEnabled(false);
            menu_item_stopserver.setEnabled(false);

            btn_add.setEnabled(false);
            btn_remove.setEnabled(false);
            btn_ping.setEnabled(false);

            btn_kill.setEnabled(false);
            btn_refresh.setEnabled(false);
        }

        //verstecke tabelle
        //panel_table.setVisible(false);
        panel_jobdetails.setVisible(false);

        //disable remove buttons
        btn_remove.setEnabled(false);
        btn_kill.setEnabled(false);

        tree_jobs.setCellRenderer(new MyTreeCellRenderer());

        // Set icon
        Image icon = (new ImageIcon(getClass().getResource("/icon.png"))).getImage();
        this.setIconImage(icon);

        centerScreen();
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

        jSplitPane1 = new javax.swing.JSplitPane();
        jPanelRight = new javax.swing.JPanel();
        panel_top = new javax.swing.JPanel();
        panel_table = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        panel_jobdetails = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jTextField6 = new javax.swing.JTextField();
        jTextField7 = new javax.swing.JTextField();
        panel_buttons = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        btn_kill = new javax.swing.JButton();
        btn_refresh = new javax.swing.JButton();
        sp_tree = new javax.swing.JScrollPane();
        tree_jobs = new javax.swing.JTree();
        toolbar_main = new javax.swing.JToolBar();
        btn_add = new javax.swing.JButton();
        btn_remove = new javax.swing.JButton();
        btn_ping = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        menu_file = new javax.swing.JMenu();
        menu_item_newjob = new javax.swing.JMenuItem();
        menu_item_stopserver = new javax.swing.JMenuItem();
        menu_item_options = new javax.swing.JMenuItem();
        menu_item_exit = new javax.swing.JMenuItem();
        menu_edit = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("WMSX GUI");

        jSplitPane1.setDividerSize(5);

        jPanelRight.setPreferredSize(new java.awt.Dimension(200, 200));

        panel_top.setPreferredSize(new java.awt.Dimension(596, 250));

        panel_table.setAutoscrolls(true);

        jTable1.setAutoCreateRowSorter(true);
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Name", "Path", "Backend", "Output", "Time", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        panel_table.setViewportView(jTable1);

        panel_jobdetails.setBorder(javax.swing.BorderFactory.createTitledBorder("Job Details"));

        jLabel1.setText("Name:");

        jLabel2.setText("Description");

        jLabel3.setText("Output");

        jLabel4.setText("Start");

        jLabel5.setText("Running");

        jLabel6.setText("Done");

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Host");

        javax.swing.GroupLayout panel_jobdetailsLayout = new javax.swing.GroupLayout(panel_jobdetails);
        panel_jobdetails.setLayout(panel_jobdetailsLayout);
        panel_jobdetailsLayout.setHorizontalGroup(
            panel_jobdetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_jobdetailsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_jobdetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel7))
                .addGroup(panel_jobdetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel_jobdetailsLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE))
                    .addGroup(panel_jobdetailsLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField2, javax.swing.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE))
                    .addGroup(panel_jobdetailsLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(panel_jobdetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField7, javax.swing.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE)
                            .addComponent(jTextField3, javax.swing.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_jobdetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_jobdetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField4, javax.swing.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE)
                    .addComponent(jTextField5, javax.swing.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE)
                    .addComponent(jTextField6, javax.swing.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE))
                .addContainerGap())
        );
        panel_jobdetailsLayout.setVerticalGroup(
            panel_jobdetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_jobdetailsLayout.createSequentialGroup()
                .addGroup(panel_jobdetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_jobdetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel5)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(panel_jobdetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_jobdetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(150, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panel_topLayout = new javax.swing.GroupLayout(panel_top);
        panel_top.setLayout(panel_topLayout);
        panel_topLayout.setHorizontalGroup(
            panel_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel_table, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 629, Short.MAX_VALUE)
            .addGroup(panel_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_topLayout.createSequentialGroup()
                    .addContainerGap(101, Short.MAX_VALUE)
                    .addComponent(panel_jobdetails, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        panel_topLayout.setVerticalGroup(
            panel_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel_table, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE)
            .addGroup(panel_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(panel_jobdetails, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panel_buttons.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel8.setText("job/worker so und so");

        btn_kill.setText("kill");
        btn_kill.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_killActionPerformed(evt);
            }
        });

        btn_refresh.setText("refresh");
        btn_refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_refreshActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panel_buttonsLayout = new javax.swing.GroupLayout(panel_buttons);
        panel_buttons.setLayout(panel_buttonsLayout);
        panel_buttonsLayout.setHorizontalGroup(
            panel_buttonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_buttonsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel_buttonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addGroup(panel_buttonsLayout.createSequentialGroup()
                        .addComponent(btn_kill, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_refresh)))
                .addContainerGap(468, Short.MAX_VALUE))
        );
        panel_buttonsLayout.setVerticalGroup(
            panel_buttonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_buttonsLayout.createSequentialGroup()
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel_buttonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_kill)
                    .addComponent(btn_refresh))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanelRightLayout = new javax.swing.GroupLayout(jPanelRight);
        jPanelRight.setLayout(jPanelRightLayout);
        jPanelRightLayout.setHorizontalGroup(
            jPanelRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel_top, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 629, Short.MAX_VALUE)
            .addComponent(panel_buttons, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanelRightLayout.setVerticalGroup(
            jPanelRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelRightLayout.createSequentialGroup()
                .addComponent(panel_top, javax.swing.GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel_buttons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jSplitPane1.setRightComponent(jPanelRight);

        sp_tree.setMinimumSize(new java.awt.Dimension(50, 80));
        sp_tree.setPreferredSize(new java.awt.Dimension(120, 363));

        tree_jobs.setModel(treeModel);
        tree_jobs.setMaximumSize(null);
        tree_jobs.setMinimumSize(new java.awt.Dimension(100, 100));
        tree_jobs.setPreferredSize(new java.awt.Dimension(100, 100));
        tree_jobs.setRequestFocusEnabled(false);
        tree_jobs.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                tree_jobsValueChanged(evt);
            }
        });
        sp_tree.setViewportView(tree_jobs);

        jSplitPane1.setLeftComponent(sp_tree);

        toolbar_main.setFloatable(false);
        toolbar_main.setRollover(true);

        btn_add.setIcon(new javax.swing.ImageIcon(getClass().getResource("/add.png"))); // NOI18N
        btn_add.setFocusable(false);
        btn_add.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn_add.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btn_add.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_add(evt);
            }
        });
        toolbar_main.add(btn_add);

        btn_remove.setIcon(new javax.swing.ImageIcon(getClass().getResource("/stop.png"))); // NOI18N
        btn_remove.setFocusable(false);
        btn_remove.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn_remove.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btn_remove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_remove(evt);
            }
        });
        toolbar_main.add(btn_remove);

        btn_ping.setText("Ping");
        btn_ping.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_ping(evt);
            }
        });
        toolbar_main.add(btn_ping);

        menu_file.setText("File");

        menu_item_newjob.setText("New Job");
        menu_item_newjob.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu_item_newjobActionPerformed(evt);
            }
        });
        menu_file.add(menu_item_newjob);

        menu_item_stopserver.setText("Stop Server");
        menu_item_stopserver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu_item_stopserverActionPerformed(evt);
            }
        });
        menu_file.add(menu_item_stopserver);

        menu_item_options.setText("Options");
        menu_item_options.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu_item_optionsActionPerformed(evt);
            }
        });
        menu_file.add(menu_item_options);

        menu_item_exit.setText("Exit");
        menu_item_exit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu_item_exitActionPerformed(evt);
            }
        });
        menu_file.add(menu_item_exit);

        jMenuBar1.add(menu_file);

        menu_edit.setText("Edit");
        jMenuBar1.add(menu_edit);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 718, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(toolbar_main, javax.swing.GroupLayout.DEFAULT_SIZE, 742, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(toolbar_main, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 338, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_ping(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_ping
       if (businessman.isOnline())
       {
           boolean ping = wmsx_service.ping(false);
           boolean fullping =  wmsx_service.ping(true);

           if (ping && fullping)
               JOptionPane.showMessageDialog(this, "Ping to provider was ok!", "WMSX GUI - Ping", JOptionPane.INFORMATION_MESSAGE);
           else
               JOptionPane.showMessageDialog(this, "Ping to provider failed!", "WMSX GUI - Ping", JOptionPane.ERROR_MESSAGE);
       }
}//GEN-LAST:event_btn_ping

    private void btn_add(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_add
       if (businessman.isOnline())
       {
            //System.out.println("btn_add...");

            NewJob newjob = new NewJob(this);
            System.out.println("Show NewJobDialog...");
            newjob.setVisible(true);
       }
}//GEN-LAST:event_btn_add

       private void btn_remove(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_remove
       if (businessman.isOnline())
       {
           System.out.println("btn_remove...");
       }
}//GEN-LAST:event_btn_remove

       private void menu_item_optionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu_item_optionsActionPerformed
       if (businessman.isOnline())
       {
            if(optionen==null)
                optionen=new Options();
            //System.out.println("Show OptionsDialog...");
            optionen.setVisible(true);
       }
}//GEN-LAST:event_menu_item_optionsActionPerformed

       private void menu_item_stopserverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu_item_stopserverActionPerformed
           if (businessman.isOnline())
           {
               wmsx_service.shutdownWorkers();
           }
       }//GEN-LAST:event_menu_item_stopserverActionPerformed

       private void menu_item_exitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu_item_exitActionPerformed
           System.exit(0);
       }//GEN-LAST:event_menu_item_exitActionPerformed

       private void menu_item_newjobActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu_item_newjobActionPerformed
           if (businessman.isOnline())
           {
                NewJob newjob = new NewJob(this);
                //System.out.println("MainWindow: Show NewJobDialog...");
                newjob.setVisible(true);
           }
       }//GEN-LAST:event_menu_item_newjobActionPerformed

       private void btn_killActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_killActionPerformed
           // TODO add your handling code here:
       }//GEN-LAST:event_btn_killActionPerformed

       private void btn_refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_refreshActionPerformed

           updateBusinessManager();
       }//GEN-LAST:event_btn_refreshActionPerformed

       private void tree_jobsValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_tree_jobsValueChanged
            DefaultMutableTreeNode node =
                (DefaultMutableTreeNode) tree_jobs.getLastSelectedPathComponent();

            if (node == null) return;

            String classname = node.getUserObject().getClass().getSimpleName();
            System.out.println(classname);

            if (classname.equals("JobData")){
                panel_table.setVisible(false);
                panel_jobdetails.setVisible(true);

                if (businessman.isOnline())
                {
                    //enable remove buttons
                    btn_remove.setEnabled(true);
                    btn_kill.setEnabled(true);
                }
            }else{
                panel_table.setVisible(true);
                panel_jobdetails.setVisible(false);

                //disable remove buttons
                btn_remove.setEnabled(false);
                btn_kill.setEnabled(false);
            }

            String titel = node.getUserObject().toString();
            jLabel8.setText(titel);

            //this.repaint();
       }//GEN-LAST:event_tree_jobsValueChanged

    Action exitAction = new AbstractAction( "Quit" ) {
      @Override public void actionPerformed( ActionEvent e ) {
        System.exit(0);
      }
    };

    public void updateBusinessManager()
    {
           businessman.saveExpansionState(tree_jobs);
           businessman.refreshData();
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_add;
    private javax.swing.JButton btn_kill;
    private javax.swing.JButton btn_ping;
    private javax.swing.JButton btn_refresh;
    private javax.swing.JButton btn_remove;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanelRight;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JMenu menu_edit;
    private javax.swing.JMenu menu_file;
    private javax.swing.JMenuItem menu_item_exit;
    private javax.swing.JMenuItem menu_item_newjob;
    private javax.swing.JMenuItem menu_item_options;
    private javax.swing.JMenuItem menu_item_stopserver;
    private javax.swing.JPanel panel_buttons;
    private javax.swing.JPanel panel_jobdetails;
    private javax.swing.JScrollPane panel_table;
    private javax.swing.JPanel panel_top;
    private javax.swing.JScrollPane sp_tree;
    private javax.swing.JToolBar toolbar_main;
    private javax.swing.JTree tree_jobs;
    // End of variables declaration//GEN-END:variables

    private void updateTreeModel() {
        System.out.println("MainWindow: updateTreeModel...");
        
        rootNode = new DefaultMutableTreeNode("Backends");

        if (businessman.isOnline())
        {
            for (String backend : businessman.getBackends()){
                 //System.out.println(backend);
                 //add Backend
                 DefaultMutableTreeNode backendnode = new DefaultMutableTreeNode(backend);
                 //add jobs for backend
                 for (JobData jobdata : businessman.getJobs(backend))
                    backendnode.add(new DefaultMutableTreeNode(jobdata));
                    
                 rootNode.add(backendnode);
                 
            }

            treeModel = new DefaultTreeModel(rootNode);
            tree_jobs.setModel(treeModel);

            //restore Expanded Nodes
            for (int row : businessman.getExpansionStateRows())
            {                
                System.out.println("MainWindow: setExpandedRow: "+row);
                tree_jobs.expandRow(row);
            }


        }else { //offline Demo Mode

            DefaultMutableTreeNode node1 = new DefaultMutableTreeNode("Fake");
            
            rootNode.add(node1);
            node1 = new DefaultMutableTreeNode("Worker");
            rootNode.add(node1);
            node1 = new DefaultMutableTreeNode("Local");
            rootNode.add(node1);
            node1 = new DefaultMutableTreeNode("Gat");
            rootNode.add(node1);

            treeModel = new DefaultTreeModel(rootNode);
            tree_jobs.setModel(treeModel);

        }

        //tree_jobs.updateUI();
    }


    public void update(final Observable o, final Object obj) {
        System.out.println("MainWindow: updateObserver...");
		updateTreeModel();
	}
}
