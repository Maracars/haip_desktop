package ui.dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class COMPortChooser extends JDialog implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	String[] portStrings;
	JList<String> portList;
	JButton bOk;
	JFrame window;

	public COMPortChooser(JFrame window, String[] portStrings) {
		super(window, "Choose a serial port", true);
		this.window = window;
		this.portStrings = portStrings;
		this.setSize(600, 600);
		this.setLocation(340, 100);
		this.setContentPane(createWindowPanel());
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.setVisible(true);
	}

	private JPanel createWindowPanel() {
		JPanel panel = new JPanel(new BorderLayout(10, 10));
		
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panel.add(createText(), BorderLayout.NORTH);
		panel.add(createListPanel(), BorderLayout.CENTER);
		panel.add(createButtonPanel(), BorderLayout.SOUTH);
		
		return panel;
	}
	
	private Component createButtonPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
		panel.setAlignmentX(0.f);
		
		bOk = new JButton("Ok");
		bOk.addActionListener(this);
		bOk.setPreferredSize(new Dimension(100, 20));
		bOk.setMinimumSize(new Dimension(100, 20));
		bOk.setMaximumSize(new Dimension(100, 20));
		
		panel.add(bOk);
		return panel;
	}
	
	private Component createListPanel() {
		JPanel panel = new JPanel();
		portList = new JList<>(portStrings);
		portList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		panel.add(portList);
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
		return portList.getSelectedIndex();
	}
}