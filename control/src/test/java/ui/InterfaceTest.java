package ui;

import helpers.CRC8;
import models.*;
import org.junit.Test;
import protocol.ControllerLogic;
import protocol.FrameCreator;
import protocol.ProtocolProperties;
import serial.Serial;
import sun.applet.Main;
import ui.panels.MainPanel;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.TimeUnit;

import static protocol.ProtocolProperties.DESTINATION_ID;
import static protocol.ProtocolProperties.HEADER;
import static protocol.ProtocolProperties.ORIGIN_ID;

public class InterfaceTest extends Observable {
	private static String PACKET_1 = "0011000000000001000000001010001000110000";
	private static String PACKET_2 = "0011000000000010000000001000011010001110";
	private static String PACKET_3 = "0011000000000001000000001001001000011110";
	private static String PACKET_4 = "0011000000000010000000001001011000111000";
	private static String PACKET_5 = "0011000000000011000000001010101001001011";
	private static String PACKET_6 = "0011000000000001000000001000101011101101";
	private static String PACKET_7 = "0011000000000010000000001010101011010000";

	ArrayList<Mooring> moorings;
	Dock dock;
	Port port;

	Serial serial;
	ControllerLogic controllerLogic;

	MainPanel mainPanel;

	public InterfaceTest() {
		this.moorings = new ArrayList<>();
		this.initMoorings(moorings);

		this.dock = new Dock("Albert Dock", moorings);
		this.port = new Port(this.dock);

		this.serial = new Serial();
		this.controllerLogic = new ControllerLogic(this.serial, this.port);

		this.mainPanel = new MainPanel(this.serial, this.controllerLogic);

		Thread thread = new Thread(this.controllerLogic);
		this.addObserver(this.controllerLogic);
		thread.start();
	}

	@Test
	public void initMoorings(ArrayList<Mooring> moorings) {
		for (Integer i = 0; i < 10; i++) {
			Ship ship = null;
			if (i == 2 ) {
				ship = new Ship("00000010");
			}
			moorings.add(new Mooring(i.toString(), ship));
		}
		Status status = new Status(ProtocolProperties.StatusType.PARKING.toString(), ProtocolProperties.ActionType.IDLE.toString(), ProtocolProperties.PermissionType.ASK.toString());
	}

	@Test
	public void receiveFrames() {
		sendToParserAndWait(PACKET_1);
		/*sendToParserAndWait(PACKET_2);
		sendToParserAndWait(PACKET_3);
		sendToParserAndWait(PACKET_4);
		sendToParserAndWait(PACKET_5);
		sendToParserAndWait(PACKET_6);
		sendToParserAndWait(PACKET_7);*/
	}

	@Test
	public void sendToParserAndWait(String packet) {
		try {
			TimeUnit.SECONDS.sleep(1);
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
		new InterfaceTest().receiveFrames();
		/*Integer length = (Integer.parseInt(PACKET_1.substring(0, 3)) * 8);
		System.out.println(CRC8.toCRC8(PACKET_1.toString().substring(0, HEADER + ORIGIN_ID + DESTINATION_ID + length)));*/
	}
}
