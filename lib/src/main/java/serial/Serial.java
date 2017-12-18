package serial;

import java.util.List;
import java.util.Observable;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;
import models.Frame;
import protocol.FrameParser;
import ui.dialogs.COMPortChooser;

import static serial.SerialExceptionMessages.NO_SERIAL_PORT_CONNECTED;
import static serial.SerialExceptionMessages.NO_SERIAL_PORT_SELECTED;
import static serial.SerialExceptionMessages.SERIAL_PORT_NOT_WORKING;

public class Serial extends Observable implements SerialPortEventListener {

	private SerialPort serialPort;
	private boolean isConnected;

	public Serial() {
		serialPort = null;
		isConnected = false;
	}

	// Opens serial connection
	public void openConnection() throws Exception {
		String[] portNames = SerialPortList.getPortNames();

		// No port connected
		if (portNames.length == 0) {
			throw new Exception(NO_SERIAL_PORT_CONNECTED);
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
					throw new Exception(NO_SERIAL_PORT_SELECTED);
				} else {
					serialPort = new SerialPort(portNames[portChooser.getSelectedIndex()]);
				}
			}
			//Open the port
			try {
				serialPort.openPort();
				serialPort.addEventListener(this);
				serialPort.setParams(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

				isConnected = true;
			}
			catch (SerialPortException e) {
				throw new Exception(SERIAL_PORT_NOT_WORKING);
			}
		}
	}

	// Closes serial connection if open
	public void closeConnection() throws SerialPortException {
		if (serialPort != null) {
			serialPort.closePort();
			serialPort = null;
			isConnected = false;
		}
	}

	public boolean isConnected() {
		return isConnected;
	}

	public void serialEvent(SerialPortEvent arg0) {
		try {
			sendToParser(serialPort.readString());
		}
		catch (SerialPortException e) {
			e.printStackTrace();
		}
	}

	public void sendToParser(String string) {
        boolean validFrame = FrameParser.parseRx(string);
        if (validFrame) {
            notifyFrame(FrameParser.getFrame());
        }
	}

	public void writeStrings(List<String> stringList) throws SerialPortException {
		for (String strByte : stringList) {
			writeString(strByte);
		}
	}

	public void writeString(String string) throws SerialPortException {
		serialPort.writeString(string);
	}

	private void notifyFrame(Frame value) {
		setChanged();
		notifyObservers(value);
	}

}
