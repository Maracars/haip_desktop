package ui;

import helpers.Helpers;
import models.*;
import org.junit.Test;
import protocol.ProtocolProperties.ActionType;
import protocol.ProtocolProperties.PermissionType;
import protocol.ProtocolProperties.StatusType;
import serial.Serial;
import settings.Settings;
import ui.panels.MapPanel;

import javax.swing.*;
import java.awt.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static settings.Settings.*;
import static ui.panels.ActionMessages.ERROR_READING_SETTINGS;

public class MapTest {
	private Dock dock;
	private Port port;
	private Ship[] ships;

	private Serial serial;

	// Swing Elements
	private JFrame window;
	private MapPanel mapPanel;

	// Settings List
	private Properties properties;

	public MapTest() {
		List<Mooring> moorings = new ArrayList<>();
		this.dock = new Dock("Albert Dock", moorings);
		this.port = new Port(this.dock);
		this.serial = new Serial();
		this.createMainPanel(this.serial, this.port);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.addShipsToSea();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.moveThreeShipsToTransit();
	}

	@Test
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		new MapTest();
	}

	private void addShipsToSea() {
		this.ships = new Ship[5];
		for (int i = 0; i < 5; i++) {
			ships[i] = new Ship(Helpers.toNbitBinaryString(String.valueOf(i), 8),
					new Status(StatusType.SEA.toString(),
							ActionType.ENTER.toString(),
							PermissionType.ASK.toString()));
			this.mapPanel.addShipToTheList(ships[i]);
		}
		this.mapPanel.repaintAllElements();
	}

	private void moveThreeShipsToTransit() {
		for (int i = 0; i < 3; i++) {
			ships[i].setStatus(new Status(StatusType.TRANSIT.toString(),
					ActionType.ENTER.toString(),
					PermissionType.ASK.toString()));
			this.mapPanel.addShipToTheList(ships[i]);
			this.port.getTransitZone().add(ships[i]);
			this.mapPanel.repaintAllElements();
		}
	}

	private void createMainPanel(Serial serial, Port port) {
		this.createJFrame();
		this.initThings(serial, port);
		this.addContentToJFrame();
	}

	private void createJFrame() {
		this.window = new JFrame("Haip Ain't an Infor Project");
		this.window.setIconImage((new ImageIcon("control/src/main/resources/HAIP_squaredLogo.png").getImage()));
		this.window.setLocation(0, 0);
		this.window.setSize(new Dimension(java.awt.Toolkit.getDefaultToolkit().getScreenSize()));
		this.window.setExtendedState(this.window.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		this.window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

	private void initThings(Serial serial, Port port) {
		try {
			this.readSettings();
		} catch (IOException e) {
			System.out.println(ERROR_READING_SETTINGS);
		}
		this.initMoorings(port);

		this.serial = serial;
	}

	private void readSettings() throws IOException {
		this.properties = new Properties();
		this.properties.load(new FileReader(FILE_NAME));

		List<String> settings = new ArrayList<>();
		for (int i = 0; i < NUM_OF_SETTINGS; i++) {
			settings.add(properties.getProperty(PROPERTY_NAMES[i], String.valueOf(SHIP_LIMITS[i])));
		}
		Settings.setProperties(settings);
	}

	private void initMoorings(Port port) {
		ArrayList<Mooring> moorings = new ArrayList<>();
		for (Integer i = 0; i < Settings.getProperties().get(0); i++) {
			moorings.add(new Mooring(Helpers.toNbitBinaryString(i.toString(), 8), null));
		}
		port.getDock().setMoorings(moorings);
	}

	private void addContentToJFrame() {
		this.mapPanel = new MapPanel(port, null);
		this.window.getContentPane().add(mapPanel, BorderLayout.CENTER);
		this.window.setVisible(true);
	}
}
