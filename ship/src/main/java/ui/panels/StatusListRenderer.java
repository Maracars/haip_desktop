package ui.panels;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class StatusListRenderer implements ListCellRenderer<String>{
	
	String statusType;
	
	public StatusListRenderer(String statusType) {
		this.statusType = statusType;
	}
	

	@Override
	public Component getListCellRendererComponent(JList<? extends String> list, String value, int index,
			boolean isSelected, boolean cellHasFocus) {
		JLabel label = new JLabel();
		label.setText(value);
		label.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
		label.setHorizontalAlignment(JLabel.CENTER);
		if(statusType.equals(value)) {
			list.setSelectedValue(value, true);
			label.setEnabled(true);
			isSelected = true;
		}else {
			label.setEnabled(false);
			isSelected = false;
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
