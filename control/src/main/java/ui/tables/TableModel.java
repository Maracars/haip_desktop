package ui.tables;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class TableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	
	List<TableData> tableData;
	ColumnModel columnModel;
	
	public TableModel(ColumnModel columnModel, List<TableData> tableData) {
		this.tableData = tableData;
		this.columnModel = columnModel;
		this.fireTableDataChanged();
	}
	
	public void remove(int index) {
		tableData.remove(index);
		this.fireTableDataChanged();
	}
	
	@Override
	public int getRowCount() {
		return tableData.size();
	}

	@Override
	public int getColumnCount() {
		return columnModel.getColumnCount();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		TableData data = tableData.get(rowIndex);
		return data.getFieldAt(columnIndex);
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return getValueAt(0, columnIndex).getClass();
	}
}