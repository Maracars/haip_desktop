package serial;

import jssc.*;
import models.Frame;
import protocol.FrameParser;
import ui.dialogs.COMPortChooser;

import java.util.List;
import java.util.Observable;

import static serial.SerialExceptionMessages.*;

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
	public void closeConnection() throws Exception {
		if (serialPort != null) {
			boolean portClosed = serialPort.closePort();
			if (portClosed) {
				serialPort = null;
				isConnected = false;
			}
			else throw new Exception("Port could not be closed");
		}
	}

	public boolean isConnected() {
		return isConnected;
	}

	@Override
	public void serialEvent(SerialPortEvent event) {
		if (event.isRXCHAR() && event.getEventValue() > 0) { // If data is available && there are more than 0 bytes
			try {
				int bytesCount = event.getEventValue();
				String bits = serialPort.readString(bytesCount);
				if (bits != null) sendToParser(bits);
			}
			catch (SerialPortException e) {
				e.printStackTrace();
			}
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
		System.out.println(string);
		serialPort.writeString(string);
	}

	private void notifyFrame(Frame value) {
		setChanged();
		notifyObservers(value);
		FrameParser.resetCommunication();
	}

}
