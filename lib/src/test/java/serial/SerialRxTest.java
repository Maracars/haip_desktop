package serial;

import org.junit.Test;

import java.util.Scanner;

public class SerialRxTest {

    Serial serialRx;
    Scanner scanner;

    @Test
    public void testComm() {
        serialRx = new Serial();
        scanner = new Scanner(System.in);

        try {
            serialRx.startConnection();
            System.out.println("Rx is connected: " + serialRx.isConnected());
            scanner.nextLine();

            serialRx.closeConnection();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public static void main(String[] args) {
        new SerialRxTest().testComm();
    }
}