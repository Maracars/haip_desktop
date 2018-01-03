package main;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import models.Ship;
import models.Status;
import protocol.ProtocolProperties.ActionType;
import protocol.ProtocolProperties.PermissionType;
import protocol.ProtocolProperties.StatusType;
import protocol.ShipLogic;
import serial.Serial;

public class Main {
	
	public static void main(String[] args) throws Exception {
		Status status = new Status(StatusType.SEA.toString(), ActionType.ENTER.toString(), PermissionType.ASK.toString());
		Ship ship = new Ship("00000001", status);
		ship.addAction(ActionType.ENTER.toString());
		Serial serial = new Serial();
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		//MainPanel panel = new MainPanel(serial, ship, nodeLogic);

		ShipLogic watcher = new ShipLogic(serial, ship);
		serial.addObserver(watcher);
		serial.openConnection();
		/*SimulationShipLogic simulationShipLogic = new SimulationShipLogic(watcher);
		MainPanel panel = new MainPanel(serial, ship, watcher, simulationShipLogic);
		watcher.addObserver(panel);*/
	}

}
