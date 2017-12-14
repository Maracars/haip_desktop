package ui;

import org.junit.Test;
import serial.Serial;
import ui.panels.MainPanel;

import javax.swing.*;
import java.util.concurrent.TimeUnit;

public class InterfaceTest {
	private static String PACKET_1 = "101100000000000100000000000010001010001001010110";
	private static String PACKET_2 = "101100000000001000000000000010001000011001101111";
	private static String PACKET_3 = "101100000000000100000000000010001001001001111000";
	private static String PACKET_4 = "101100000000001000000000000010001001011011011001";
	private static String PACKET_5 = "101100000000001100000000000010001010101010101100";
	private static String PACKET_6 = "101100000000000100000000000010001000101010001011";
	private static String PACKET_7 = "101100000000001000000000000010001010101000110001";

	Serial serial;

	@Test
	public void openUI() {
		this.serial = new Serial();
		new MainPanel(serial);

		sendToParserAndWait(PACKET_1);
		sendToParserAndWait(PACKET_2);
		sendToParserAndWait(PACKET_3);
		sendToParserAndWait(PACKET_4);
		sendToParserAndWait(PACKET_5);
		sendToParserAndWait(PACKET_6);
		sendToParserAndWait(PACKET_7);
	}

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
		new InterfaceTest().openUI();
	}
}
