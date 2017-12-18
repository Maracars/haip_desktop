package ui;

import helpers.CRC8;
import models.Dock;
import models.Mooring;
import models.Port;
import models.Ship;
import org.junit.Test;
import protocol.ControllerLogic;
import serial.Serial;
import ui.panels.MainPanel;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Scanner;

public class ControllerLogicTest extends Observable {
	private static String SHIP_1_ACK = "000010000000000100000000";
	private static String SHIP_2_ACK = "000010000000001000000000";

	private static String PACKET_1_OUT = "001100000000000100000000" + "01100010";
	private static String PACKET_1_TRANSIT = "001100000000000100000000" + "01010010";
	private static String PACKET_1_IN = "001100000000000100000000" + "01001010";

	private static String PACKET_2_OUT = "001100000000001000000000" + "01100010";
	private static String PACKET_2_TRANSIT = "001100000000001000000000" + "01010010";

	ArrayList<Mooring> moorings;
	Dock dock;
	Port port;

	Serial serial;
	ControllerLogic controllerLogic;

	MainPanel mainPanel;

	Scanner scanner;

	public ControllerLogicTest() {
		this.moorings = new ArrayList<>();
		this.initMoorings(moorings);

		this.dock = new Dock("Albert Dock", moorings);
		this.port = new Port(this.dock);

		this.serial = new Serial();
		this.controllerLogic = new ControllerLogic(this.serial, this.port);

		this.mainPanel = new MainPanel(this.serial, this.controllerLogic);

		this.scanner = new Scanner(System.in);

		Thread thread = new Thread(this.controllerLogic);
		this.addObserver(this.controllerLogic);
		thread.start();
	}

	@Test
	public void receiveFrames() {
		scanner.nextLine();
		serial.sendToParser(SHIP_1_ACK + CRC8.toCRC8(SHIP_1_ACK));
		waitAndSendToParser(SHIP_2_ACK + CRC8.toCRC8(SHIP_2_ACK));

		scanner.nextLine();
		serial.sendToParser(PACKET_1_OUT + CRC8.toCRC8(PACKET_1_OUT));
		scanner.nextLine();
		serial.sendToParser(PACKET_2_OUT + CRC8.toCRC8(PACKET_2_OUT));
		scanner.nextLine();
		serial.sendToParser(PACKET_1_TRANSIT + CRC8.toCRC8(PACKET_1_TRANSIT));
		scanner.nextLine();
		serial.sendToParser(PACKET_1_IN + CRC8.toCRC8(PACKET_1_IN));
		scanner.nextLine();
		serial.sendToParser(PACKET_2_OUT + CRC8.toCRC8(PACKET_2_OUT));
		scanner.nextLine();
		serial.sendToParser(PACKET_2_TRANSIT + CRC8.toCRC8(PACKET_2_TRANSIT));
	}

	@Test
	public void waitAndSendToParser(String packet) {
		try {
			Thread.sleep(10);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		serial.sendToParser(packet);
	}

	@Test
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		new ControllerLogicTest().receiveFrames();
	}

	@Test
	public void initMoorings(ArrayList<Mooring> moorings) {
		for (Integer i = 0; i < 10; i++) {
			Ship ship = null;
			/*if (i == 2 ) {
				ship = new Ship("00000010");
			}*/
			moorings.add(new Mooring(i.toString(), ship));
		}
	}
}
