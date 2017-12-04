package serial;

import java.io.InputStream;
import java.io.OutputStream;

import dialogs.COMPortChooser;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;

public class Serial implements SerialPortEventListener {

	private static final int BAUD_RATE = 9600;
	private static final int BYTE_LENGTH = 8;

	SerialPort serialPort;
	OutputStream outputStream;
	InputStream inputStream;
	//FrameParser frameParser;

	// Starts serial connection
	public void startConnection() throws SerialPortException, Exception {
		serialPort = null;
		outputStream = null;
		inputStream = null;
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
				}
				else {
					serialPort = new SerialPort(portNames[portChooser.getSelectedIndex()]);
				}
			}
			//Open the port
			serialPort.openPort();
			serialPort.addEventListener(this);
			serialPort.setParams(BAUD_RATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
		}
	}

	// Closes serial connection if open
	public void closeConnection() throws SerialPortException {
		if (serialPort != null) serialPort.closePort();
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
			String binaryString = fillWithZeros(Integer.toBinaryString(readDecimal));

			// Send data to parser
			//frameParser.parse(binaryString)

			System.out.println(binaryString);
		}
		catch (SerialPortException e) {
			e.printStackTrace();
		}
	}

	public void writeByte() throws SerialPortException {
		String binaryString = "101";

		// Fill with zeros to get a full byte
		binaryString = fillWithZeros(binaryString);

		// Data is converted to decimal
		int decimalToSend = Integer.parseInt(binaryString, 2);

		// Data sent through serial port
		serialPort.writeInt(decimalToSend);
	}

	public String fillWithZeros(String binaryString) {
		if (binaryString.length() < BYTE_LENGTH) {
			for (int i = binaryString.length(); i < BYTE_LENGTH; i++) {
				binaryString = "0" + binaryString;
			}
		}
		return binaryString;
	}
}
