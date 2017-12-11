package ui.tables;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class TableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	
	List<TableData> tableData;
	ColumnModel columnas;
	
	public TableModel(ColumnModel columnas, List<TableData> tableData) {
		this.tableData = tableData;
		this.columnas = columnas;
		this.fireTableDataChanged();
	}
	
	public void remove(int indice) {
		tableData.remove(indice);
		this.fireTableDataChanged();
	}
	
	@Override
	public int getRowCount() {
		return tableData.size();
	}

	@Override
	public int getColumnCount() {
		return columnas.getColumnCount();
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