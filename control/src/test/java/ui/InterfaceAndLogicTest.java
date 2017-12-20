package ui;

import models.*;
import org.junit.Test;
import protocol.ControllerLogic;
import protocol.FrameCreator;
import serial.Serial;
import ui.panels.MainPanel;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Scanner;

import static protocol.ProtocolProperties.*;

public class InterfaceAndLogicTest {
	ArrayList<Mooring> moorings;
	Dock dock;
	Port port;

	Serial serial;
	ControllerLogic controllerLogic;

	MainPanel mainPanel;

	Scanner scanner;

	public InterfaceAndLogicTest() {
		this.moorings = new ArrayList<>();
		this.initMoorings(moorings);

		this.dock = new Dock("Albert Dock", moorings);
		this.port = new Port(this.dock);

		this.serial = new Serial();
		this.controllerLogic = new ControllerLogic(this.serial, this.port);

		this.mainPanel = new MainPanel(this.serial, this.controllerLogic);

		this.scanner = new Scanner(System.in);
	}

	@Test
	public void receiveFrames() {
		Status seaEnter = new Status(StatusType.SEA.toString(), ActionType.ENTER.toString(), PermissionType.ASK.toString());
		Status transitEnter = new Status(StatusType.TRANSIT.toString(), ActionType.ENTER.toString(), PermissionType.ASK.toString());
		Status dockIdle = new Status(StatusType.PARKING.toString(), ActionType.IDLE.toString(), PermissionType.ASK.toString());
		Status parkingLeave = new Status(StatusType.PARKING.toString(), ActionType.LEAVE.toString(), PermissionType.ASK.toString());
		Status transitLeave = new Status(StatusType.TRANSIT.toString(), ActionType.LEAVE.toString(), PermissionType.ASK.toString());

		scanner.nextLine();
		serial.sendToParser(FrameCreator.createAck("00000001", MASTER_ID).toString());
		waitAndSendToParser(FrameCreator.createAck("00000010", MASTER_ID).toString());

		scanner.nextLine();
		serial.sendToParser(FrameCreator.createRequest("00000001", MASTER_ID, seaEnter).toString());
		scanner.nextLine();
		serial.sendToParser(FrameCreator.createRequest("00000010", MASTER_ID, seaEnter).toString());
		scanner.nextLine();
		serial.sendToParser(FrameCreator.createRequest("00000001", MASTER_ID, transitEnter).toString());
		scanner.nextLine();
		serial.sendToParser(FrameCreator.createRequest("00000010", MASTER_ID, seaEnter).toString());
		scanner.nextLine();
		serial.sendToParser(FrameCreator.createRequest("00000001", MASTER_ID, parkingLeave).toString());
		scanner.nextLine();
		serial.sendToParser(FrameCreator.createRequest("00000010", MASTER_ID, transitEnter).toString());
		scanner.nextLine();

		serial.sendToParser(FrameCreator.createRequest("00000001", MASTER_ID, parkingLeave).toString());
		scanner.nextLine();

		serial.sendToParser(FrameCreator.createRequest("00000010", MASTER_ID, parkingLeave).toString());
		scanner.nextLine();
		serial.sendToParser(FrameCreator.createRequest("00000001", MASTER_ID, transitLeave).toString());
		scanner.nextLine();
		serial.sendToParser(FrameCreator.createRequest("00000010", MASTER_ID, parkingLeave).toString());
		scanner.nextLine();
		serial.sendToParser(FrameCreator.createRequest("00000001", MASTER_ID, dockIdle).toString());
		scanner.nextLine();
		serial.sendToParser(FrameCreator.createRequest("00000010", MASTER_ID, transitLeave).toString());

	}

	@Test
	public void waitAndSendToParser(String packet) {
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
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
		new InterfaceAndLogicTest().receiveFrames();
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
