package ui.tables;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class TableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	
	List<TableData> tableDataList;
	ColumnModel columnModel;
	
	public TableModel(ColumnModel columnModel) {
		this.tableDataList = new ArrayList<>();
		this.columnModel = columnModel;
		this.fireTableDataChanged();
	}

	public void add(TableData tableData) {
		int index = -1;

		for (TableData t : tableDataList) {
			if (t.getShipID() == tableData.getShipID()) {
				index = this.tableDataList.indexOf(t);
			}
		}
		if (index == -1) {
			this.tableDataList.add(tableData);
		}
		else {
			this.tableDataList.set(index, tableData);
		}
		this.fireTableDataChanged();
	}
	
	public void remove(int index) {
		this.tableDataList.remove(index);
		this.fireTableDataChanged();
	}
	
	@Override
	public int getRowCount() {
		return tableDataList.size();
	}

	@Override
	public int getColumnCount() {
		return columnModel.getColumnCount();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		TableData data = tableDataList.get(rowIndex);
		return data.getFieldAt(columnIndex);
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return getValueAt(0, columnIndex).getClass();
	}
}