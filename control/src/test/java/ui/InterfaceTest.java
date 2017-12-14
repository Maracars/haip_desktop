package ui;

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

public class InterfaceTest extends Observable {
	private static String PACKET_1 = "101100000000000100000000000010001010001001010110";
	private static String PACKET_2 = "101100000000001000000000000010001000011001101111";
	private static String PACKET_3 = "101100000000000100000000000010001001001001111000";
	private static String PACKET_4 = "101100000000001000000000000010001001011011011001";
	private static String PACKET_5 = "101100000000001100000000000010001010101010101100";
	private static String PACKET_6 = "101100000000000100000000000010001000101010001011";
	private static String PACKET_7 = "101100000000001000000000000010001010101000110001";

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
		sendToParserAndWait(PACKET_2);
		sendToParserAndWait(PACKET_3);
		sendToParserAndWait(PACKET_4);
		sendToParserAndWait(PACKET_5);
		sendToParserAndWait(PACKET_6);
		sendToParserAndWait(PACKET_7);
	}

	@Test
	public void sendToParserAndWait(String packet) {
		try {
			TimeUnit.SECONDS.sleep(5);
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
	}
}
