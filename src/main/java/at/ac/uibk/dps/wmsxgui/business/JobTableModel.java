/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.ac.uibk.dps.wmsxgui.business;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.table.AbstractTableModel;

/**
 * 
 * @author bafu
 */
public class JobTableModel extends AbstractTableModel implements Observer {
    /**
     * 
     */
    private static final long serialVersionUID = 8976244994494855744L;
    private final BusinessManager businessman;

    private final String[] columnNames = { "JobUID", "Executable", "SiteID",
            "Created", "Started", "Finished", "State", "Type", "WorkerID" };

    private List<JobData> data;

    public JobTableModel() {
        this.businessman = BusinessManager.getInstance();
        this.businessman.addObserver(this);
        this.updateData();
    }

    public void update(final Observable o, final Object obj) {
        System.out.println("JobTableModel: updateObserver...");
        this.updateData();
    }

    private void updateData() {
        this.data = this.businessman.getJobsTable();
        // printDebugData();
        this.fireTableDataChanged();
    }

    public int getColumnCount() {
        return this.columnNames.length;
    }

    public int getRowCount() {
        return this.data.size();
    }

    @Override
    public String getColumnName(final int col) {
        return this.columnNames[col];
    }

    public Object getValueAt(final int row, final int col) {
        if ((this.data.size() > 0) && (this.data.get(row) != null)) {
            switch (col) {
                case 0:
                    return this.data.get(row).getTransportJobUID();
                case 1:
                    return this.data.get(row).getJobinfo().getExecutable();
                case 2:
                    return this.data.get(row).getJobinfo().getSiteId();
                case 3:
                    return (this.data.get(row).getJobinfo().getCreationTime() != null) ? this.data
                            .get(row).getJobinfo().getCreationTime().toString()
                            : "";
                case 4:
                    return (this.data.get(row).getJobinfo()
                            .getStartRunningTime() != null) ? this.data
                            .get(row).getJobinfo().getStartRunningTime()
                            .toString() : "";
                case 5:
                    return (this.data.get(row).getJobinfo()
                            .getDoneRunningTime() != null) ? this.data.get(row)
                            .getJobinfo().getDoneRunningTime().toString() : "";
                case 6:
                    return this.data.get(row).getJobinfo().getStatus()
                            .toString();
                case 7:
                    return (this.data.get(row).getJobinfo().isWorker() == true) ? "Worker"
                            : "Job";
                case 8:
                    return this.data.get(row).getJobinfo().getWorkerId();
                default:
                    return this.data.get(row).getTransportJobUID().toString();
            }
        } else {
            return null;
        }
    }

    /*
     * JTable uses this method to determine the default renderer/ editor for
     * each cell. If we didn't implement this method, then the last column would
     * contain text ("true"/"false"), rather than a check box.
     */
    @Override
    public Class getColumnClass(final int c) {
        final Object val = this.getValueAt(0, c);
        // System.out.println("JobTableModel: getColumnClass for Value at Col="+c+": "+val+" Class: "+((val!=null)?val.getClass().toString():"null"));
        if (val != null) {
            return val.getClass();
        } else {
            return new String().getClass();
        }
    }

    /*
     * Don't need to implement this method unless your table's editable.
     */
    @Override
    public boolean isCellEditable(final int row, final int col) {
        // Note that the data/cell address is constant,
        // no matter where the cell appears onscreen.
        return false;
    }
}
