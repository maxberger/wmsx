package at.ac.uibk.dps.wmsxgui.presentation.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.JInternalFrame;

import javax.swing.Icon;
import javax.swing.table.AbstractTableModel;

public class NonSortableTableModel extends AbstractTableModel 
{
	// constants
	private static final int COLUMN_ID 			= 0;
	private static final int COLUMN_FIRST_NAME 	= 1;
	private static final int COLUMN_LAST_NAME 	= 2;
	private static final int COLUMN_POSTAL_CODE = 3;
	private static final int COLUMN_CITY 		= 4;
	private static final int COLUMN_STREET 		= 5;
	

	public NonSortableTableModel() {
		this(false);
	}
	
	// data holds a vector of Record objects where
	// one Record represents one row of data
	protected Vector data;
	private Record template;
	private Vector columnNames;
	private boolean sortable;
	
	
	protected NonSortableTableModel(boolean sortable) {
		super();
		
		this.sortable = sortable;
		createData();
	}

	private void createData() {
		columnNames = new Vector();
		
		columnNames.add( "ClientID" );
		columnNames.add( "First name" );
		columnNames.add( "Last name" );
		columnNames.add( "Postal Code" );
		columnNames.add( "City" );
		columnNames.add( "Street" );
		
		TableColorIcon.reset();
		template = null;
		data = new Vector();
		
		// here we create our row record "information"
		// this must be replaced with a database call
		for(int i = 0; i < 50; i++) {
			Record rec = new Record(i + 1);
			
			data.add(rec);
			
			if(template == null) {
				template = rec;
			}
		}
	}

// AbstractTableModel implementation
	
	public int getColumnCount() {
		if(template == null) return 0;
		
		return template.getColumnCount();
	}

	public int getRowCount() {
		return data.size();
	}

	public Object getValueAt(int row, int column) {
		Record rec = (Record)data.get(row);
		
		return rec.getValueAt(column);
	}
	
	public Class getColumnClass(int column) {
		if(template == null) return Object.class;
		
		return template.getColumnClass(column);
	}
	
	public String getColumnName(int column) {
		return (String)columnNames.get(column);
	}
	
// Some methods for testing
	
	public void addColumn(Class cl, int column) {
		columnNames.add(column, "N" + (column + 1));
		
		Iterator ii = data.iterator();
		while(ii.hasNext()) {
			Record rec = (Record)ii.next();
			
			rec.addColumn(cl, column);
		}
		
		fireTableStructureChanged();
	}
	
	public void removeColumn(int column) {
		columnNames.remove(column);
		
		Iterator ii = data.iterator();
		while(ii.hasNext()) {
			Record rec = (Record)ii.next();
			
			rec.removeColumn(column);
		}
		
		fireTableStructureChanged();
	}
	
	public void removeAllRows() {
		data.clear();
		fireTableDataChanged();
	}
	
	public void createNewData() {
		createData();
		
		// might be that number of columns changed
		fireTableStructureChanged();
	}

	/**
	 * A Record represents one row of data.
	 * By default a Record consists of 4 values but
	 * columns can be added and can be removed.
	 * 
	 * @author Hans Bickel
	 *
	 */
	protected class Record {

		private Vector values;
		private int oldRow, newRow;
		
		protected Record(int index) 
		{
			// here we fill our data with real information
			// this must be replaced with a DB call
			values = new Vector();
			Object v[] = new Object[COLUMN_STREET+1];
			v[COLUMN_ID] 			= ""+index;
			v[COLUMN_FIRST_NAME] 	= "fname" + index;
			v[COLUMN_LAST_NAME] 	= "lname" + index;
			v[COLUMN_POSTAL_CODE] 	= ""+(index+6000);
			v[COLUMN_CITY] 			= "city" + index;
			v[COLUMN_STREET] 		= "street" + index; //new TableColorIcon();
			
			for( int i = 0; i<=COLUMN_STREET; i++ )
				values.add(v[i]);
		}
		
		public Object getValueAt(int column) {
			if(column < 0 || column >= values.size()) return null;
			
			return values.get(column);
		}
		
		public Class getColumnClass(int column) {
			if(column < 0 || column >= values.size()) return Object.class;
			
			return values.get(column).getClass();
		}
		
		public void addColumn(Class cl, int column) {
			if(Double.class.equals(cl)) {
				values.add(column, 0.00);
			}
			else if(Icon.class.equals(cl)) {
				values.add(column, 0.00);
			}
			else if(Integer.class.equals(cl)) {
				values.add(column, 0.00);
			}
			else if(String.class.equals(cl)) {
				values.add(column, 0.00);
			}
		}
		
		public void removeColumn(int column) {
			values.remove(column);
		}
		
		public int getColumnCount() {
			return values.size();
		}

		
		public int getNewRow() {
			return newRow;
		}

		
		public void setNewRow(int newRow) {
			this.newRow = newRow;
		}

		
		public int getOldRow() {
			return oldRow;
		}

		
		public void setOldRow(int oldRow) {
			this.oldRow = oldRow;
		}
	}
	
	/**
	 * A 24x12 sized icon of random color.
	 */
	public static final class TableColorIcon implements Icon {
		
		private Color color;
		private static int hue = 0;
		
		TableColorIcon() {
			color = Color.getHSBColor((float)(hue / 360.0), 0.5f, 0.9f);
			hue += 360 / 20;
		}
		
		static void reset() {
			hue = 0;
		}

		public void paintIcon(Component c, Graphics g, int x, int y) {
			g.setColor(Color.DARK_GRAY);
			g.drawRect(x, y, getIconWidth() - 1, getIconHeight() - 1);
			
			g.setColor(color);
			g.fillRect(x + 1, y + 1, getIconWidth() - 2, getIconHeight() - 2);
		}

		public int getIconWidth() {
			return 24;
		}

		public int getIconHeight() {
			return 12;
		}

		public int compareTo(Object o) {
			if(o == null) return 1;
			if(!(o instanceof TableColorIcon)) return 1;
			
			Color c2 = ((TableColorIcon)o).color;
			
			if(color.getRGB() == c2.getRGB()) {
				return 0;
			}
			else if(color.getRGB() > c2.getRGB()) {
				return 1;
			}
			
			return -1;
		}
	}
}
