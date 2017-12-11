package serial;

import jssc.*;
import ui.dialogs.COMPortChooser;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

public class Serial implements SerialPortEventListener {

	private static final int BAUD_RATE = 9600;

	private static SerialPort serialPort;

	private List<Observer> observers;

	//FrameParser frameParser;

	boolean isConnected;

	public Serial() {
		serialPort = null;
		isConnected = false;
		observers = new ArrayList<>();
	}

	// Starts serial connection
	public void startConnection() throws SerialPortException, Exception {
		String[] portNames = SerialPortList.getPortNames();

		// No port connected
		if (portNames.length == 0) {
			throw new Exception("No port connected");
		}
		// At least one port connected
		else {
			// Only one port connected
			if (portNames.length == 1) {
				serialPort = new SerialPort(portNames[0]);
			}
			// More than one port connected
			else {
				COMPortChooser portChooser = new COMPortChooser(null, portNames);
				if (portChooser.getSelectedIndex() == -1) {
					throw new Exception("One port must be chosen");
				} else {
					serialPort = new SerialPort(portNames[portChooser.getSelectedIndex()]);
				}
			}
			//Open the port
			serialPort.openPort();
			serialPort.addEventListener(this);
			serialPort.setParams(BAUD_RATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

			isConnected = true;
		}
	}

	// Closes serial connection if open
	public void closeConnection() throws SerialPortException {
		if (serialPort != null) {
			serialPort.closePort();
			isConnected = false;
		}
	}

	public boolean isConnected() {
		return isConnected;
	}

	public void serialEvent(SerialPortEvent arg0) {
		try {
			// Get binary string from serial port
			String binaryString = serialPort.readString();
			System.out.println(binaryString);

			// Send data to parser
			//frameParser.parse(binaryString)
		}
		catch (SerialPortException e) {
			e.printStackTrace();
		}
	}

	public static void writeBytes(List<String> byteList) throws SerialPortException {
		for (String strByte : byteList) {
			writeString(strByte);
		}

	}

	public static void writeString(String string) throws SerialPortException {
		// Data sent through serial port
		serialPort.writeString(string);
	}

	public List<Observer> getObservers() {
		return observers;
	}

	public void addObserver(Observer observer) {
		this.observers.add(observer);

	}
}
