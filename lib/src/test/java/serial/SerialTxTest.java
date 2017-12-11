package serial;

import org.junit.Test;

import java.util.Scanner;

public class SerialTxTest {

	Serial serialTx;
	Scanner scanner;

	@Test
	public void testComm() {
		serialTx = new Serial();
		scanner = new Scanner(System.in);

		try {
			serialTx.openConnection();
			System.out.println("Tx is connected: " + serialTx.isConnected());
			scanner.nextLine();

			serialTx.writeString("10101011");
			scanner.nextLine();

			serialTx.closeConnection();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}


}