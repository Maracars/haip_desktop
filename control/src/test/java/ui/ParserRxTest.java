package ui;

import org.junit.Test;
import serial.Serial;
import ui.panels.MainPanel;

import javax.swing.*;
import java.util.Observable;
import java.util.concurrent.TimeUnit;

public class ParserRxTest extends Observable {
	private static String PACKET_1 = "11010000" + "00110000000000010000000010100010" + "00110000";

	private static String PACKET_2_A = "001100000000001";
	private static String PACKET_2_B = "00000000010000110" + "10001110";

	private static String PACKET_3_A = "11101101" + "001100000000000";
	private static String PACKET_3_B = "10000000010010010" + "00011110" + "01001011";

	private static String PACKET_4_A = "001100000000001" + "001100000000001";    // 1ª parte basura
	private static String PACKET_4_B = "00000000010010110" + "00111000" + "0011";    // 3ª parte inicio de P5
	private static String PACKET_5_A = "00000000001100000000";
	private static String PACKET_5_B = "10101010" + "01001011" + "001100";    // 3ª parte inicio de P6
	private static String PACKET_6_A = "0011000000000001000000001000";
	private static String PACKET_6_B = "1010" + "11101101";

	private static String PACKET_7 = "00110000000000100000000010101010" + "11010000";

	private Serial serial;

	private ParserRxTest() {
		serial = new Serial();
		new MainPanel();
	}

	@Test
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		new ParserRxTest().receiveFrames();
	}

	private void receiveFrames() {
		sendToParserAndWait(PACKET_4_A);
		sendToParserAndWait(PACKET_4_B);
		sendToParserAndWait(PACKET_5_A);
		sendToParserAndWait(PACKET_5_B);
		sendToParserAndWait(PACKET_6_A);
		sendToParserAndWait(PACKET_6_B);
	}

	private void sendToParserAndWait(String packet) {
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		serial.sendToParser(packet);
	}
}
