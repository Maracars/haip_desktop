package ui.panels;

import javax.swing.*;
import java.awt.*;

public class StatusListRenderer implements ListCellRenderer<String> {

	private String statusType;

	StatusListRenderer(String statusType) {
		this.statusType = statusType;
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends String> list, String value, int index,
												  boolean isSelected, boolean cellHasFocus) {

		JLabel label = new JLabel();
		label.setText(value);
		label.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
		label.setHorizontalAlignment(JLabel.CENTER);
		if (statusType.equals(value)) {
			label.setEnabled(true);
		} else {
			label.setEnabled(false);
		}
		return label;
	}

	public String getStatusType() {
		return statusType;
	}

	public void setStatusType(String statusType) {
		this.statusType = statusType;
	}

}
