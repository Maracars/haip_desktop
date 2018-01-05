package ui.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import helpers.Helpers;
import models.Ship;
import models.Status;
import protocol.DecisionMaker;
import protocol.ProtocolProperties.PermissionType;
import protocol.ProtocolProperties.StatusType;
import protocol.SimulationShipLogic;
import ui.panels.TextFieldPanel;

public class SimulationDialog extends JDialog implements ActionListener{

	private static final long serialVersionUID = 1L;

	JFrame window;
	JButton bStart;
	JButton bStop;
	SimulationShipLogic simShipLogic;
	TextFieldPanel boatNumbers;
	int numBoats;

	public SimulationDialog(JFrame window, SimulationShipLogic simShipLogic) {
		super(window, "Haip Simulation", true);
		this.window = window;
		this.simShipLogic = simShipLogic;
		numBoats = 0;
		simShipLogic.getShipLogic().getSerial().deleteObserver(simShipLogic.getShipLogic());
		simShipLogic.getShipLogic().setSimulationStarted();
		simShipLogic.getShipLogic().getSerial().addObserver(simShipLogic);

		this.setSize((int) Math.round(java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth() - 500),
				(int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight()/4);


		this.setLocation((int) Math.round(java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 5),
				(int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 6);

		this.setContentPane(createWindowPanel());

		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setVisible(true);
	}

	private Container createWindowPanel() {
		JPanel panel = new JPanel(new BorderLayout(10,10));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		panel.add(createText(), BorderLayout.NORTH);
		panel.add(createNotePanel(), BorderLayout.CENTER);
		panel.add(createButtonPanel(), BorderLayout.SOUTH);

		return panel;
	}

	private Component createButtonPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

		bStart = new JButton("Start Simulation");
		bStart.addActionListener(this);

		bStop = new JButton("Stop Simulation");
		bStop.addActionListener(this);
		bStop.setEnabled(false);

		panel.add(bStart);
		panel.add(bStop);
		return panel;
	}

	private Component createNotePanel() {
		JPanel panel = new JPanel();
		String text = "Note: Make sure of start the simulation without boats in the port. "
				+ "All the boats simulated would start from SEA state";
		JLabel noteLabel = new JLabel(text);
		panel.add(noteLabel);
		return panel;
	}

	private Component createText() {
		boatNumbers = new TextFieldPanel("Choose a number of boats:", "");
		return boatNumbers;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(bStart)) {
			try {
				if(numBoats != Integer.parseInt(boatNumbers.getText()) || numBoats == 0) {
					numBoats = Integer.parseInt(boatNumbers.getText());
					initializeBoats(numBoats);
				}
				bStop.setEnabled(true);
				bStart.setEnabled(false);
				simShipLogic.getShipLogic().getSerial().deleteObserver(simShipLogic.getShipLogic());
				simShipLogic.getShipLogic().setSimulationStarted();
				simShipLogic.getShipLogic().getSerial().addObserver(simShipLogic);
			}catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(window,
						"Remember to insert a valid number, please",
						"Number of boats Error",
						JOptionPane.ERROR_MESSAGE);
				boatNumbers.setText("");
			}
		}else if(e.getSource().equals(bStop)){
			bStart.setEnabled(true);
			bStop.setEnabled(false);

			simShipLogic.getShipLogic().setSimulationStopped();
			simShipLogic.getShipLogic().getSerial().deleteObserver(simShipLogic);
			simShipLogic.getShipLogic().getSerial().addObserver(simShipLogic.getShipLogic());
		}

	}

	private void initializeBoats(int numBoats) {
		System.out.println("Num boats "+numBoats);
		for(int i = 0; i < numBoats; i++) {
			Status newStatus = DecisionMaker.getRandomAction(StatusType.SEA);
			Status status = new Status(StatusType.SEA.toString(), newStatus.getAction(), PermissionType.ASK.toString());
			Ship ship = new Ship(Helpers.toByteBinString(String.valueOf(i+1), 8), status);
			ship.addAction(newStatus);
			simShipLogic.addShipToSimulation(ship);
		}

	}

}
