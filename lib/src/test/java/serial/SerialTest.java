package serial;


import java.util.Scanner;

public class SerialTest {

	private Scanner scanner = new Scanner(System.in);
	private Serial serial;

	public static void main(String[] args) {
		SerialTest serialTest = new SerialTest();
		serialTest.testComm();
	}

	private void testComm() {
		serial = new Serial();
		try {
			serial.openConnection();
			System.out.println("Write whatever: ");
			String string = scanner.nextLine();
			serial.writeString(string);
			serial.closeConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}