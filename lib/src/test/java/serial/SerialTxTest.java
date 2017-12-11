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
            serialTx.startConnection();
            System.out.println("Tx is connected: " + serialTx.isConnected());
            scanner.nextLine();

            serialTx.writeByte("10101011");
            scanner.nextLine();

            serialTx.closeConnection();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public static void main(String[] args) {
        new SerialTxTest().testComm();
    }
}