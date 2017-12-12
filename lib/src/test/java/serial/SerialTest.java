package serial;


import java.util.Scanner;

public class SerialTest {

	private Serial serial;
	Scanner scanner = new Scanner(System.in);

	public void testComm() {
		serial = new Serial();
		while (true) {

			try {
				serial.openConnection();
				System.out.println("Write whatever: ");
				String string = scanner.nextLine();
				serial.writeString(string);
				serial.closeConnection();
			}
			catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	public static void main(String[] args) {
		SerialTest serialTest = new SerialTest();
		serialTest.testComm();
	}

}