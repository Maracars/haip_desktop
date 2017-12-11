package serial;

import org.junit.Test;

import java.util.Scanner;

public class SerialRxTest {

	Serial serial;
	Scanner scanner;

	@Test
	public void testComm() {
		serial = new Serial();
		scanner = new Scanner(System.in);

		try {
			serial.openConnection();

			System.out.println("Rx is connected: " + serial.isConnected());
			scanner.nextLine();

			serial.closeConnection();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}