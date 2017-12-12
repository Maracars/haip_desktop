package main;

import protocol.ShipLogic;
import serial.Serial;

public class Main {
	
	public static void main(String[] args) {
		Serial serial = new Serial();
		try {
			serial.openConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		ShipLogic watcher = new ShipLogic(serial);
		serial.addObserver(watcher);
	}

}
