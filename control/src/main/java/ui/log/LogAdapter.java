package ui.log;

import javax.swing.*;
import java.awt.*;

public class LogAdapter implements ListCellRenderer<String> {
	
	@Override
	public Component getListCellRendererComponent(JList<? extends String> list, String text, int index,
			boolean selected, boolean focus) {
		
		JLabel label = new JLabel();
		label.setText(text);
		
		if (text.substring(0, 5).equals("ERROR")) {
			label.setForeground(Color.red);
		}
		return label;
	}
}