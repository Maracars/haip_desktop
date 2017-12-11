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
		switch (column) {
		case 0:
			this.setHorizontalAlignment(LEFT);
			this.setFont(new Font("Arial", Font.PLAIN, 16));
			break;
			
		case 1:
			
			label.setText("" + value);
			
			label.setFont(new Font("Arial", Font.BOLD, 16));
			label.setForeground(Color.red);
			label.setHorizontalAlignment(LEFT);
			
		return label;
	  }
		this.setOpaque(true);
		return this;
	}
}