package serial;

import java.util.List;
import java.util.Observable;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;
import protocol.FrameParser;
import ui.dialogs.COMPortChooser;

public class Serial extends Observable implements SerialPortEventListener {

	private static final int BAUD_RATE = 9600;

	private static SerialPort serialPort;
	private String packet;
	private boolean isConnected;

	public Serial() {
		packet = "";
		serialPort = null;
		isConnected = false;
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
			// Get int array from serial port
			String readString = serialPort.readString();
			packet += readString;
			int result = FrameParser.parseRx(readString);
			if (result == FrameParser.BAD_PACKET) {
				packet = "";
			} else if (result == FrameParser.FIN_PACKET) {
				// TODO Parse packet to frame
				notifyPacket(packet);
			}
		} catch (SerialPortException e) {
			e.printStackTrace();
		}
	}

	public static void writeStrings(List<String> stringList) throws SerialPortException {
		for (String strByte : stringList) {
			writeString(strByte);
		}

	}

	public static void writeString(String string) throws SerialPortException {
		serialPort.writeString(string);
	}

	private void notifyPacket(String value) {
		setChanged();
		notifyObservers(value);
		packet = "";

	}

}
