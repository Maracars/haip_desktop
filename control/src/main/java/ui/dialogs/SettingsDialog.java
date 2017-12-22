package ui.dialogs;

import settings.Settings;
import ui.panels.TextFieldPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import static settings.Settings.NUM_OF_SETTINGS;
import static settings.Settings.PROPERTY_FIELD_TITLES;

public class SettingsDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = 1L;

	private List<TextFieldPanel> fieldList;

	public SettingsDialog() {
		super(new JFrame(), "Settings Menu", true);

		this.setSize((int) Math.round(java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 6),
				(int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 4);

		this.setLocation((int) Math.round(java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 12),
				(int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 8);

		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		this.setContentPane(createMainPanel());
		this.setVisible(true);
	}

	private JPanel createMainPanel() {
		JPanel panel = new JPanel(new BorderLayout(10, 10));

		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panel.add(createDataPanel(), BorderLayout.CENTER);
		panel.add(createButtonsPanel(), BorderLayout.SOUTH);

		return panel;
	}

	private Component createDataPanel() {
		JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
		this.fieldList = new ArrayList<>();

		for (int i = 0; i < NUM_OF_SETTINGS; i++) {
			TextFieldPanel textFieldPanel = createTextFieldPanel(PROPERTY_FIELD_TITLES[i],
					Settings.getProperties().get(i));
			this.fieldList.add(textFieldPanel);
			panel.add(textFieldPanel);
		}
		return panel;
	}

	private TextFieldPanel createTextFieldPanel(String settingName, int settingValue) {
		return new TextFieldPanel(settingName, String.valueOf(settingValue));
	}

	private Component createButtonsPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
		panel.setAlignmentX(0.f);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("Cancel");
		cancelButton.addActionListener(this);
		cancelButton.setPreferredSize(new Dimension(this.getWidth() / 3, this.getHeight() / 6));

		JButton acceptButton = new JButton("Accept");
		acceptButton.setActionCommand("Accept");
		acceptButton.addActionListener(this);
		acceptButton.setPreferredSize(new Dimension(this.getWidth() / 3, this.getHeight() / 6));

		panel.add(cancelButton);
		panel.add(acceptButton);

		return panel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Accept")) {
			if (checkFields()) {
				this.dispose();
			}
		} else if (e.getActionCommand().equals("Cancel")) {
			this.dispose();
		}
	}

	public List<String> getSettings() {
		List<String> settings = new ArrayList<>();
		for (TextFieldPanel textFieldPanel : fieldList) {
			settings.add(textFieldPanel.getText());
		}
		return settings;
	}

	private boolean checkFields() {
		for (int i = 0; i < fieldList.size(); i++) {
			if (fieldList.get(i).getText().trim().isEmpty()) {
				JOptionPane.showMessageDialog(this, "Some of the fields are empty.",
						"Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
			if (isInteger(fieldList.get(i).getText()) == false) {
				JOptionPane.showMessageDialog(this, "Some of the fields are not a number.",
						"Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
		return true;
	}

	private boolean isInteger(String str) {
		try {
			Integer.parseInt(str);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
}