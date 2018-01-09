package ui.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import protocol.ProtocolProperties.ActionType;
import protocol.ProtocolProperties.StatusType;

public class DecisionListRenderer implements ListCellRenderer<String>{

	StatusListRenderer statusRenderer;

	public DecisionListRenderer(StatusListRenderer statusRenderer) {
		this.statusRenderer = statusRenderer;
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends String> list, String value, int index,
			boolean isSelected, boolean cellHasFocus) {
		JLabel label = new JLabel();
		label.setText(value);
		label.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
		label.setHorizontalAlignment(JLabel.CENTER);
		checkAvailableOptions(label, value);
		if(isSelected && label.isEnabled()) {
			label.setOpaque(true);
			label.setBackground(Color.LIGHT_GRAY);
		}
		return label;
	}

	private void checkAvailableOptions(JLabel label, String text) {

		if(statusRenderer.getStatusType().equals(StatusType.TRANSIT.name())){
			if(text.equals(ActionType.IDLE.name())) {
				label.setEnabled(false);
			}else {
				label.setEnabled(true);
			}		
		}
		if(statusRenderer.getStatusType().equals(StatusType.PARKING.name())) {
			if(text.equals(ActionType.ENTER.name())) {
				label.setEnabled(false);
			}else {
				label.setEnabled(true);
			}
		}
		if(statusRenderer.getStatusType().equals(StatusType.SEA.name())) {
			if(text.equals(ActionType.LEAVE.name())) {
				label.setEnabled(false);
			}else {
				label.setEnabled(true);
			}
		}

	}

}
