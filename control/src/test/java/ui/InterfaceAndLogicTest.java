package ui;

import helpers.Helpers;
import settings.Settings;
import models.*;
import org.junit.Test;
import protocol.FrameCreator;
import serial.Serial;
import ui.panels.MainPanel;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static protocol.ProtocolProperties.*;

public class InterfaceAndLogicTest {
	ArrayList<Mooring> moorings;
	Dock dock;
	Port port;

	Serial serial;

	MainPanel mainPanel;

	Scanner scanner;

	@Test
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		new InterfaceAndLogicTest();

		/*try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		interfaceAndLogicTest.receiveFrames();*/
		//new InterfaceAndLogicTest();
	}

	public InterfaceAndLogicTest() {
		this.moorings = new ArrayList<>();

		this.dock = new Dock("Albert Dock", moorings);
		this.port = new Port(this.dock);
		this.serial = new Serial();
		this.mainPanel = new MainPanel(this.serial, this.port);

		this.scanner = new Scanner(System.in);
	}

	@Test
	public void receiveFrames() {
		mainPanel.startLogic();

		Status seaEnter = new Status(StatusType.SEA.toString(), ActionType.ENTER.toString(), PermissionType.ASK.toString());
		Status transitEnter = new Status(StatusType.TRANSIT.toString(), ActionType.ENTER.toString(), PermissionType.ASK.toString());
		Status dockIdle = new Status(StatusType.PARKING.toString(), ActionType.IDLE.toString(), PermissionType.ASK.toString());
		Status parkingLeave = new Status(StatusType.PARKING.toString(), ActionType.LEAVE.toString(), PermissionType.ASK.toString());
		Status transitLeave = new Status(StatusType.TRANSIT.toString(), ActionType.LEAVE.toString(), PermissionType.ASK.toString());

		//scanner.nextLine();
		waitAndSendToParser(FrameCreator.createAck("00000001", MASTER_ID).toString());
		waitAndSendToParser(FrameCreator.createAck("00000010", MASTER_ID).toString());

		//scanner.nextLine();
		waitAndSendToParser(FrameCreator.createRequest("00000001", MASTER_ID, seaEnter).toString());
		//scanner.nextLine();
		waitAndSendToParser(FrameCreator.createRequest("00000010", MASTER_ID, seaEnter).toString());
		//scanner.nextLine();
		waitAndSendToParser(FrameCreator.createRequest("00000001", MASTER_ID, transitEnter).toString());
		//scanner.nextLine();
		waitAndSendToParser(FrameCreator.createRequest("00000010", MASTER_ID, seaEnter).toString());
		//scanner.nextLine();
		waitAndSendToParser(FrameCreator.createRequest("00000001", MASTER_ID, parkingLeave).toString());
		//scanner.nextLine();
		waitAndSendToParser(FrameCreator.createRequest("00000010", MASTER_ID, transitEnter).toString());
		//scanner.nextLine();

		waitAndSendToParser(FrameCreator.createRequest("00000001", MASTER_ID, parkingLeave).toString());
		//scanner.nextLine();

		waitAndSendToParser(FrameCreator.createRequest("00000010", MASTER_ID, parkingLeave).toString());
		//scanner.nextLine();
		waitAndSendToParser(FrameCreator.createRequest("00000001", MASTER_ID, transitLeave).toString());
		//scanner.nextLine();
		waitAndSendToParser(FrameCreator.createRequest("00000010", MASTER_ID, parkingLeave).toString());
		//scanner.nextLine();
		waitAndSendToParser(FrameCreator.createRequest("00000001", MASTER_ID, dockIdle).toString());
		//scanner.nextLine();
		waitAndSendToParser(FrameCreator.createRequest("00000010", MASTER_ID, transitLeave).toString());

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
}
