package main;

import serial.Serial;
import java.util.Scanner;

public class main {

    Serial serialRx, serialTx;
    Scanner scanner = new Scanner(System.in);

    public void testComm() {
        serialRx = new Serial();
        serialTx = new Serial();

        try {
            serialRx.startConnection();
            serialTx.startConnection();
            scanner.nextLine();

            serialTx.writeByte();
            scanner.nextLine();

            serialRx.closeConnection();
            serialTx.closeConnection();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new main().testComm();
    }
}