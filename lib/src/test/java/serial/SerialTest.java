package serial;

import org.junit.Test;

import java.util.Scanner;

public class SerialTest {

<<<<<<< HEAD
	Serial serialRx, serialTx;
	Scanner scanner = new Scanner(System.in);

	@Test
	public void testComm() {
		serialRx = new Serial();
		serialTx = new Serial();

		try {
			serialRx.startConnection();
			serialTx.startConnection();
			scanner.nextLine();


			//serialTx.writeByte();
			scanner.nextLine();

			serialRx.closeConnection();
			serialTx.closeConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public static void main(String[] args) {
		new SerialTest().testComm();
	}
=======
    Serial serialRx, serialTx;
    Scanner scanner = new Scanner(System.in);

    @Test
    public void testComm() {
        serialRx = new Serial();
        serialTx = new Serial();

        try {
            serialRx.startConnection();
            serialTx.startConnection();
            scanner.nextLine();

            serialTx.writeByte("101");
            scanner.nextLine();

            serialRx.closeConnection();
            serialTx.closeConnection();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public static void main(String[] args) {
        new SerialTest().testComm();
    }
>>>>>>> 779e7297065ce194dcb6015a318a2fdb5ee55882
}