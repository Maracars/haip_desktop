package main;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import models.Ship;
import models.Status;
import protocol.ProtocolProperties.ActionType;
import protocol.ProtocolProperties.PermissionType;
import protocol.ProtocolProperties.StatusType;
import protocol.ShipLogic;
import protocol.SimulationShipLogic;
import serial.Serial;
import ui.panels.MainPanel;

public class Main {
	
	public static void main(String[] args) throws Exception {
		Status status = new Status(StatusType.SEA.toString(), ActionType.ENTER.toString(), PermissionType.ASK.toString());
		Ship ship = new Ship("00000110", status);
		Status newStatus = new Status(StatusType.PARKING.toString(), ActionType.ENTER.toString());
		ship.addAction(newStatus);
		Serial serial = new Serial();
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		ShipLogic shipLogic = new ShipLogic(serial, ship);
		SimulationShipLogic simulationShipLogic = new SimulationShipLogic(shipLogic);
		MainPanel panel = new MainPanel(serial, ship, shipLogic, simulationShipLogic);
		shipLogic.addObserver(panel);
	}

}
