package serial;

import jssc.SerialPortException;
import org.junit.Test;

import java.util.Scanner;

public class SerialTest {

	private Serial serial;
	Scanner scanner = new Scanner(System.in);

	public void testComm() {
		serial = new Serial();
		while (true) {

			try {
				System.out.println("Write whatever: ");
				String string = scanner.nextLine();
				serial.writeString(string);

			} catch (Exception e) {
				try {
					serial.closeConnection();
				} catch (SerialPortException e1) {
					e1.printStackTrace();
				}
			}


		}
	}

	public static void main(String[] args) {
		SerialTest serialTest = new SerialTest();
		serialTest.testComm();
	}

}