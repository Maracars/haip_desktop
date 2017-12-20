package ui.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import helpers.Helpers;
import models.Ship;
import models.Status;
import protocol.ProtocolProperties.ActionType;
import protocol.ProtocolProperties.PermissionType;
import protocol.ProtocolProperties.StatusType;
import protocol.SimulationShipLogic;

public class SimulationDialog extends JDialog implements ActionListener{
	private static final int MAX_BOATS_SIMULATION = 6;

	private static final long serialVersionUID = 1L;

	JFrame window;
	JComboBox<String> boatNumbers;
	List<JPanel> boatStates;
	JButton bStart;
	JButton bStop;
	SimulationShipLogic shipLogic;

	public SimulationDialog(JFrame window, SimulationShipLogic shipLogic) {
		super(window, "Haip Simulation", true);
		this.window = window;
		this.shipLogic = shipLogic;
		boatStates = new ArrayList<JPanel>();

		this.setSize((int) Math.round(java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth() - 500),
				(int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 150);


		this.setLocation((int) Math.round(java.awt.Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 5),
				(int) java.awt.Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 15);

		this.setContentPane(createWindowPanel());

		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setVisible(true);
	}

	private Container createWindowPanel() {
		JPanel panel = new JPanel(new BorderLayout(10,10));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		panel.add(createText(), BorderLayout.NORTH);
		panel.add(createComboBox(), BorderLayout.CENTER);
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

	private Component createComboBox() {
		JPanel panel = new JPanel(new GridLayout(3, 2));
		for(int i = 0; i < MAX_BOATS_SIMULATION; i++) {
			panel.add(createBoatStatePanel(i));
		}
		repaintPanels();
		return panel;
	}

	private Component createBoatStatePanel(int boatNumber) {
		JPanel panel = new JPanel();
		JLabel label = new JLabel();
		label.setText("Select boat number "+ (boatNumber + 1)+ " initial state");
		label.setEnabled(false);
		JComboBox<String> boatState = new JComboBox<String>(Helpers.getNames(StatusType.class));
		boatState.setEnabled(false);
		panel.add(label);
		panel.add(boatState);
		boatStates.add(panel);
		return panel;

	}

	private Component createText() {
		JPanel panel = new JPanel();
		JLabel label = new JLabel();
		label.setText("Choose a number of boats:");
		createBoatsList();
		panel.add(label);
		panel.add(this.boatNumbers);
		return panel;
	}

	private void createBoatsList() {
		String[] boatNumbers = {"1","2","3","4","5", "6"};
		this.boatNumbers = new JComboBox<String>(boatNumbers);
		this.boatNumbers.addActionListener(this);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(bStart)) {
			initializeBoats();
			bStop.setEnabled(true);
			bStart.setEnabled(false);
		}else if(e.getSource().equals(bStop)){
			bStart.setEnabled(true);
			bStop.setEnabled(false);
		}else{
			repaintPanels();
		}
		
	}

	private void initializeBoats() {
		for(int i = 0; i <= boatNumbers.getSelectedIndex(); i++) {
			JComboBox<String> boatState = (JComboBox<String>) boatStates.get(i).getComponent(1);
			Ship ship = checkShipStatus(boatState, i+1);
			shipLogic.addShipToSimulation(ship);
		}

	}

	private Ship checkShipStatus(JComboBox<String> boatState, int boatId) {
		StatusType statusType = StatusType.valueOf((String) boatState.getSelectedItem());
		Status status = null;
		switch(statusType) {
		case PARKING:
			status = new Status(statusType.toString(), ActionType.LEAVE.toString(), PermissionType.ASK.toString());
			break;
		case TRANSIT:
			//Hemen ez dakit zer jarri
			break;
		case SEA:
			status = new Status(statusType.toString(), ActionType.ENTER.toString(), PermissionType.ASK.toString());
			break;
		default:
			break;
		}

		Ship ship = new Ship(Integer.toBinaryString(boatId), status);
		return ship;
	}

	public void repaintPanels() {
		for(int i = 0; i < boatStates.size(); i++) {
			for(Component component: boatStates.get(i).getComponents()) {
				if(i <= boatNumbers.getSelectedIndex()) {
					component.setEnabled(true);
				}else {
					component.setEnabled(false);
				}
			}
		}
	}





}
