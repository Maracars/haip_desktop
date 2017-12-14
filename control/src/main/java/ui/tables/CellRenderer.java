package ui.tables;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class CellRenderer extends DefaultTableCellRenderer{
	private static final long serialVersionUID = 1L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		JLabel label = new JLabel();

		/*switch (column) {}*/

		if (value.getClass().equals(Boolean.class)) {
			label.setFont(new Font("Comic Sans MS", Font.PLAIN, 18));
			label.setText("" + value);

			if ((Boolean) value == false || label.getText().substring(0, 5).equals("ERROR")) {
				label.setForeground(Color.RED);
			}
			return label;
		}
		else {
			this.setHorizontalAlignment(LEFT);
			this.setFont(new Font("Comic Sans MS", Font.PLAIN, 18));
		}

		this.setOpaque(true);
		return this;
	}
}