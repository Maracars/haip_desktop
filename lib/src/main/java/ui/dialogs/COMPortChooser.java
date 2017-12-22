package ui.dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class COMPortChooser extends JDialog implements ActionListener {
	private static final long serialVersionUID = 1L;

	String[] portStrings;
	JComboBox<String> comboBox;
	JButton bOk;
	JFrame window;

	public COMPortChooser(JFrame window, String[] portStrings) {
		super(window, "Choose a serial port", true);
		this.window = window;
		this.portStrings = portStrings;

		this.setSize((int) Math.round(java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 6),
				(int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 5);

		this.setLocation((int) Math.round(java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 15),
				(int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 2 * this.getHeight());

		this.setContentPane(createWindowPanel());

		// bOk is clicked when enter
		this.getRootPane().setDefaultButton(this.bOk);
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.setVisible(true);
	}

	private JPanel createWindowPanel() {
		JPanel panel = new JPanel(new BorderLayout(10, 10));

		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panel.add(createText(), BorderLayout.NORTH);
		panel.add(createComboBox(), BorderLayout.CENTER);
		panel.add(createButtonPanel(), BorderLayout.SOUTH);

		return panel;
	}

	private Component createButtonPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
		panel.setAlignmentX(0.f);

		bOk = new JButton("Ok");
		bOk.addActionListener(this);
		bOk.setPreferredSize(new Dimension(this.getWidth() / 3, this.getHeight() / 6));

		panel.add(bOk);
		return panel;
	}

	private Component createComboBox() {
		JPanel panel = new JPanel();
		this.comboBox = new JComboBox<>(portStrings);
		panel.add(comboBox);
		return panel;
	}

	private JLabel createText() {
		JLabel text = new JLabel("Choose a port:");
		text.setFont(new Font("Arial", Font.BOLD, 16));
		return text;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == bOk) {
			this.dispose();
		}
	}

	public int getSelectedIndex() {
		return comboBox.getSelectedIndex();
	}
}