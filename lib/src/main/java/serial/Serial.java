package serial;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

import ui.dialogs.COMPortChooser;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;

public class Serial implements SerialPortEventListener {

	private static final int BAUD_RATE = 9600;

	private static SerialPort serialPort;


	private ArrayList<Observer> observers = new ArrayList<>();

	//FrameParser frameParser;

	boolean isConnected;

	public Serial() {
		serialPort = null;
<<<<<<< HEAD
		OutputStream outputStream = null;
		InputStream inputStream = null;

=======
		outputStream = null;
		inputStream = null;
		isConnected = false;
	}

	// Starts serial connection
	public void startConnection() throws SerialPortException, Exception {
>>>>>>> 779e7297065ce194dcb6015a318a2fdb5ee55882
		String[] portNames = SerialPortList.getPortNames();
		COMPortChooser portChooser;

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
				portChooser = new COMPortChooser(null, portNames);
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
			// Get int array from serial port
			int[] readIntArray = serialPort.readIntArray();

			// Get decimal int
			int readDecimal = 0;
			for (int n : readIntArray) {
				readDecimal = n;
			}

			// Fill with zeros to get a full byte
			String binaryString = Integer.toBinaryString(readDecimal);

			// Send data to parser
			//frameParser.parse(binaryString)

			System.out.println(binaryString);
		} catch (SerialPortException e) {
			e.printStackTrace();
		}
	}

	public static void writeBytes(List<String> byteList) throws SerialPortException {
		for (String strByte : byteList) {
			writeByte(strByte);
		}

	}

	public static void writeByte(String binaryString) throws SerialPortException {
		// Data is converted to decimal
		int decimalToSend = Integer.parseInt(binaryString, 2);

		// Data sent through serial port
		serialPort.writeInt(decimalToSend);
	}


	public ArrayList<Observer> getObservers() {
		return observers;
	}

	public void addObserver(Observer observer) {
		this.observers.add(observer);

	}
}
