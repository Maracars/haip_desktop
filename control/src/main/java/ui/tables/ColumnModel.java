package ui.tables;

import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

public class ColumnModel extends DefaultTableColumnModel {
	private static final long serialVersionUID = 1L;

	private CellRenderer cellRenderer;

	public ColumnModel(CellRenderer cellRenderer) {
		this.cellRenderer = cellRenderer;
		this.addColumn(createColumn("Ship ID", 0, 100));
		this.addColumn(createColumn("Position", 1, 100));
		this.addColumn(createColumn("Desired Action", 2, 100));
		this.addColumn(createColumn("Action allowed", 3, 100));
	}

	private TableColumn createColumn(String text, int index, int width) {
		TableColumn column = new TableColumn(index, width);

		column.setHeaderValue(text);
		column.setPreferredWidth(width);
		column.setCellRenderer(cellRenderer);

		return column;
	}
}