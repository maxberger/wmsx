/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.ac.uibk.dps.wmsxgui.business;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.table.AbstractTableModel;

/**
 * 
 * @author WmsxGUI Team
 * @version 1.0
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
    private static final String DATEFORMATSTR = "HH:mm:ss dd.MM.yyyy";

    private static final int COLUMN0 = 0;
    private static final int COLUMN1 = 1;
    private static final int COLUMN2 = 2;
    private static final int COLUMN3 = 3;
    private static final int COLUMN4 = 4;
    private static final int COLUMN5 = 5;
    private static final int COLUMN6 = 6;
    private static final int COLUMN7 = 7;
    private static final int COLUMN8 = 8;

    /**
     * Constructor for a JobTableModel object.
     */
    public JobTableModel() {
        this.businessman = BusinessManager.getInstance();
        this.businessman.addObserver(this);
        this.updateData();
    }

    /**
     * update will be executed, when an observable sends this event, so the
     * tableModel has to be updated.
     * @param o Observable, which sends the event
     * @param obj Some object, which has changed
     */
    @Override
    public void update(final Observable o, final Object obj) {
        System.out.println("JobTableModel: updateObserver...");
        this.updateData();
    }

    private void updateData() {
        this.data = this.businessman.getJobsTable();
        // printDebugData();
        this.fireTableDataChanged();
    }

    /**
     * getColumnCount returns the current column count.
     * @return column count of the current table
     */
    @Override
    public int getColumnCount() {
        return this.columnNames.length;
    }

    /**
     * getRowCount returns the current row count.
     * @return row count of the current table
     */
    @Override
    public int getRowCount() {
        return this.data.size();
    }

    /**
     * getColumnName returns the column name for the given column.
     * @param col column index, for the column name
     * @return column name for the given column index
     */
    @Override
    public String getColumnName(final int col) {
        return this.columnNames[col];
    }

    /**
     * getValueAt returns the specific value at the given position in the table.
     * @param row row index for the value
     * @param col column index for the value
     * @return value at the given position (row,col)
     */
    @Override
    public Object getValueAt(final int row, final int col) {
        if ((this.data.size() > 0) && (this.data.get(row) != null)) {
            final DateFormat dateFormat = new SimpleDateFormat(
                    this.DATEFORMATSTR);

            switch (col) {
                case COLUMN0:
                    return this.data.get(row).getTransportJobUID();
                case COLUMN1:
                    return this.data.get(row).getJobinfo().getExecutable();
                case COLUMN2:
                    return this.data.get(row).getJobinfo().getSiteId();
                case COLUMN3:
                    return (this.data.get(row).getJobinfo().getCreationTime() != null) ? dateFormat
                            .format(this.data.get(row).getJobinfo()
                                    .getCreationTime())
                            : "";
                case COLUMN4:
                    return (this.data.get(row).getJobinfo()
                            .getStartRunningTime() != null) ? dateFormat
                            .format(this.data.get(row).getJobinfo()
                                    .getStartRunningTime()) : "";
                case COLUMN5:
                    return (this.data.get(row).getJobinfo()
                            .getDoneRunningTime() != null) ? dateFormat
                            .format(this.data.get(row).getJobinfo()
                                    .getDoneRunningTime()) : "";
                case COLUMN6:
                    return this.data.get(row).getJobinfo().getStatus()
                            .toString();
                case COLUMN7:
                    return (this.data.get(row).getJobinfo().isWorker() == true) ? "Worker"
                            : "Job";
                case COLUMN8:
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
    /**
     * getColumnClass returns the class type of the given column.
     * @param c column index
     * @return class type for the given column index
     */
    @Override
    public Class getColumnClass(final int c) {
        final Object val = this.getValueAt(0, c);
        // System.out.println("JobTableModel: getColumnClass for Value at Col="+c+": "+val+" Class: "+((val!=null)?val.getClass().toString():"null"));
        if (val != null) {
            return val.getClass();
        } else {
            return String.class;
        }
    }

    /*
     * Don't need to implement this method unless your table's editable.
     */
    /**
     * isCellEditable returns true or false, if the cell is editable.
     * @param row row index
     * @param col column index
     * @return true, if cell is editable, otherwise false
     */
    @Override
    public boolean isCellEditable(final int row, final int col) {
        // Note that the data/cell address is constant,
        // no matter where the cell appears onscreen.
        return false;
    }
}
