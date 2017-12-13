package main;

import java.awt.FontFormatException;
import java.io.IOException;

import jiconfont.icons.FontAwesome;
import jiconfont.swing.IconFontSwing;
import models.Ship;
import protocol.ShipLogic;
import serial.Serial;
import ui.panels.MainPanel;

public class Main {
	
	public static void main(String[] args) throws FontFormatException, IOException {
		Ship ship = new Ship();
		Serial serial = new Serial();
		IconFontSwing.register(FontAwesome.getIconFont());
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
