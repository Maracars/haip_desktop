package ui.log;

import javax.swing.*;
import java.awt.*;

public class LogListCellRenderer implements ListCellRenderer<String> {

	@Override
	public Component getListCellRendererComponent(JList<? extends String> list, String text, int index,
												  boolean selected, boolean focus) {

		JLabel label = new JLabel();
		label.setText(text);

		if (text.length() >= "ERROR".length() && text.substring(0, "ERROR".length()).equals("ERROR")) {
			label.setForeground(Color.red);
		}
		return label;
	}
}