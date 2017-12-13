package protocol;

import jssc.SerialPortException;
import serial.Serial;

import java.util.Observable;
import java.util.Scanner;


public class ProbaObserver extends Observable {

	public static void main(String[] args) throws SerialPortException {
		Serial serial = new Serial();
		try {
			serial.openConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		ControllerLogic watcher = new ControllerLogic(serial);
		Thread th = new Thread(watcher);
		serial.addObserver(watcher);
		th.start();
		Scanner scanner = new Scanner(System.in);
		while (true) {
			String line = scanner.nextLine();
			serial.writeString(line);
		}

	}


}