package main;

import models.Ship;
import protocol.ShipLogic;
import serial.Serial;
import ui.panels.MainPanel;

public class Main {
	
	public static void main(String[] args) {
		Ship ship = new Ship();
		Serial serial = new Serial();
		MainPanel panel = new MainPanel(serial, ship);
		try {
			serial.openConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		ShipLogic watcher = new ShipLogic(serial);
		serial.addObserver(watcher);
	}

}
