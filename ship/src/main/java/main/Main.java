package main;

import models.Ship;
import models.Status;
import protocol.ProtocolProperties.ActionType;
import protocol.ProtocolProperties.PermissionType;
import protocol.ProtocolProperties.StatusType;
import protocol.ShipLogic;
import protocol.SimulationShipLogic;
import serial.Serial;
import ui.panels.MainPanel;

import javax.swing.*;

public class Main {

	private static final String SHIP_ID = "00000001";

	public static void main(String[] args) {
		Status status = new Status(StatusType.SEA.toString(), ActionType.ENTER.toString(), PermissionType.ASK.toString());
		Ship ship = new Ship(SHIP_ID, status);
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
