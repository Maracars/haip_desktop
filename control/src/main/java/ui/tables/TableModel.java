package ui.tables;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class TableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;

	private List<TableData> tableDataList;
	private ColumnModel columnModel;

	public TableModel(ColumnModel columnModel) {
		this.tableDataList = new ArrayList<>();
		this.columnModel = columnModel;
		this.fireTableDataChanged();
	}

	public void add(TableData tableData) {
		int index = tableDataList.indexOf(tableData);
		// Update old ship
		if (index != -1) {
			this.tableDataList.set(index, tableData);
			this.fireTableRowsUpdated(index, index);
		}
		// Add new ship
		else {
			this.tableDataList.add(tableData);
			this.fireTableRowsInserted(index, index);
		}
	}

	public void updatePermission(int shipID, boolean permission) {
		int index = -1;

		for (TableData t : tableDataList) {
			if (t.getShipID() == shipID) {
				index = this.tableDataList.indexOf(t);
				break;
			}
		}
		if (index != -1) {
			TableData tableData = tableDataList.get(index);
			tableData.setPermission(permission);
			this.tableDataList.set(index, tableData);
			this.fireTableRowsUpdated(index, index);
		}
	}

	public void remove(int index) {
		this.tableDataList.remove(index);
		this.fireTableRowsDeleted(index, index);
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