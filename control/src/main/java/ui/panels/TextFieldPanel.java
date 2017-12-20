package ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class TextFieldPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	JTextField textField;

	public TextFieldPanel(String text) {
		this.setLayout(new BorderLayout(10, 10));
		
		textField = new JTextField("");

		textField.setEditable(true);
		textField.setBackground(Color.WHITE);

		this.add(textField);
		this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), text));

	}
	
	public void setText(String text) {
		this.textField.setText(text);
	}
	
	public String getText() {
		return this.textField.getText();
	}
}