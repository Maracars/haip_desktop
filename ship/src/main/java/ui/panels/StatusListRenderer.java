package ui.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import protocol.ProtocolProperties.ActionType;
import protocol.ProtocolProperties.StatusType;

public class StatusListRenderer implements ListCellRenderer<String>{

	@Override
	public Component getListCellRendererComponent(JList<? extends String> list, String value, int index,
			boolean isSelected, boolean cellHasFocus) {
		JLabel label = new JLabel();
		label.setText(value);
		label.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
		label.setHorizontalAlignment(JLabel.CENTER);
		if(isSelected) {
			label.setOpaque(true);
			label.setBackground(Color.LIGHT_GRAY);
		}	
		return label;
	}


}
