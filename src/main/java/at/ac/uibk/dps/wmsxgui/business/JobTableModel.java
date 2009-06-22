/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.ac.uibk.dps.wmsxgui.business;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author bafu
 */
public class JobTableModel extends AbstractTableModel implements Observer
{
    private boolean DEBUG = false;
    private BusinessManager businessman;

    private String[] columnNames = {"JobUID", "Executable", 
        "SiteID", "Created", "Started", "Finished", "State","Type","WorkerID"};

    private List<JobData> data;

    public JobTableModel()
    {
        this.businessman = BusinessManager.getInstance();
        businessman.addObserver(this);
        updateData();
    }

    public void update(final Observable o, final Object obj) {
        System.out.println("JobTableModel: updateObserver...");
        updateData();
	}

    private void updateData()
    {
        data = businessman.getJobsTable();
        //printDebugData();
        fireTableDataChanged();
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return data.size();
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col)
    {
        if ( (data.size()>0) && (data.get(row)!=null) )
        {
            switch(col)
            {
                case 0: return data.get(row).getTransportJobUID().toString();
                case 1: return data.get(row).getJobinfo().getExecutable();
                case 2: return data.get(row).getJobinfo().getSiteId();
                case 3: return (data.get(row).getJobinfo().getCreationTime()!=null)?data.get(row).getJobinfo().getCreationTime().toString():"";
                case 4: return (data.get(row).getJobinfo().getStartRunningTime()!=null)?data.get(row).getJobinfo().getStartRunningTime().toString():"";
                case 5: return (data.get(row).getJobinfo().getDoneRunningTime()!=null)?data.get(row).getJobinfo().getDoneRunningTime().toString():"";
                case 6: return data.get(row).getJobinfo().getStatus().toString();
                case 7: return (data.get(row).getJobinfo().isWorker()==true)?"Worker":"Job";
                case 8: return data.get(row).getJobinfo().getWorkerId();
                default: return data.get(row).getTransportJobUID().toString();
            }
        }else
            return null;
    }

    /*
     * JTable uses this method to determine the default renderer/
     * editor for each cell.  If we didn't implement this method,
     * then the last column would contain text ("true"/"false"),
     * rather than a check box.
     */
    public Class getColumnClass(int c) {
        Object val = getValueAt(0, c);
        //System.out.println("JobTableModel: getColumnClass for Value at Col="+c+": "+val+" Class: "+((val!=null)?val.getClass().toString():"null"));
        if (val!=null)
            return val.getClass();
        else
            return new String().getClass();
    }

    /*
     * Don't need to implement this method unless your table's
     * editable.
     */
    public boolean isCellEditable(int row, int col) {
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.
        return false;
    }

    /*
     * Don't need to implement this method unless your table's
     * data can change.
     */
    /*
    public void setValueAt(Object value, int row, int col) {
        if (DEBUG) {
            System.out.println("Setting value at " + row + "," + col
                               + " to " + value
                               + " (an instance of "
                               + value.getClass() + ")");
        }

        data[row][col] = value;
        fireTableCellUpdated(row, col);

        if (DEBUG) {
            System.out.println("New value of data:");
            printDebugData();
        }
    }*/

    private void printDebugData() {
        System.out.println("JobTableModel: printDebugData... RowCount: "+getRowCount());
        for (int i=0; i < getRowCount(); i++) {
            System.out.println("JobTableModel: printDebugData:  row " + i + ": "+data.get(i));

        }
    }
}

