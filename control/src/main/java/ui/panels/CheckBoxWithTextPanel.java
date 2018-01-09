package ui.panels;

import javax.swing.*;
import java.awt.*;

public class CheckBoxWithTextPanel extends JPanel {
	private JCheckBox checkBox;
	private JLabel text;

	public CheckBoxWithTextPanel() {
		this.setLayout(new BorderLayout(10, 10));

		this.checkBox = new JCheckBox();
		this.checkBox.setSelected(true);
		this.text = new JLabel("Enable auto-scroll");

		this.add(this.text, BorderLayout.CENTER);
		this.add(this.checkBox, BorderLayout.EAST);
	}

	public boolean isSelected() {
		return this.checkBox.isSelected();
	}
}
