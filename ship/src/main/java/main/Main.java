package main;

import java.awt.FontFormatException;
import java.io.IOException;

import models.Ship;
import protocol.ShipLogic;
import serial.Serial;
import ui.panels.MainPanel;

import javax.swing.*;

public class Main {
	
	public static void main(String[] args) throws FontFormatException, IOException {
		Ship ship = new Ship();
		Serial serial = new Serial();

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
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
