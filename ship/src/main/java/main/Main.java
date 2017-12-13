package main;

import java.awt.FontFormatException;
import java.io.IOException;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import jiconfont.icons.FontAwesome;
import jiconfont.swing.IconFontSwing;
import models.Ship;
import models.Status;
import protocol.ProtocolProperties.ActionType;
import protocol.ProtocolProperties.PermissionType;
import protocol.ProtocolProperties.StatusType;
import protocol.ShipLogic;
import serial.Serial;
import ui.panels.MainPanel;

public class Main {
	
	public static void main(String[] args) throws FontFormatException, IOException {
		Status status = new Status(StatusType.SEA.toString(), ActionType.ENTER.toString(), PermissionType.ALLOW.toString());
		Ship ship = new Ship("00000001", status);
		Serial serial = new Serial();
		IconFontSwing.register(FontAwesome.getIconFont());

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
