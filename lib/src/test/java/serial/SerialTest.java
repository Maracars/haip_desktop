package serial;

import org.junit.Test;

import java.util.Scanner;

public class SerialTest {

	private Serial serial;


	@Test
	public void testComm() {
		serial = new Serial();
		try {
			serial.writeString("10000001111");
			System.out.println("Sent");
			serial.closeConnection();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}