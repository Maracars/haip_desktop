package ui.panels;

import javax.swing.*;
import java.awt.*;

public class TextFieldPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JTextField textField;

	public TextFieldPanel(String title, String text) {
		this.setLayout(new BorderLayout(10, 10));

		this.textField = new JTextField(text);
		this.textField.setBackground(Color.WHITE);

		this.add(textField);
		this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), title));

	}

	public String getText() {
		return this.textField.getText();
	}

	public void setText(String text) {
		this.textField.setText(text);
	}

	public void setEditable(boolean editable) {
		this.textField.setEditable(editable);
	}
}