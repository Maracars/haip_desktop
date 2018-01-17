package serial;

import jssc.*;
import models.Frame;
import protocol.FrameParser;
import ui.dialogs.COMPortChooser;

import java.util.List;
import java.util.Observable;

import helpers.Helpers;

import static serial.SerialExceptionMessages.*;

public class Serial extends Observable implements SerialPortEventListener {
	private SerialPort serialPort;

	public Serial() {
		serialPort = null;
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
				if (serialPort.openPort()) {
					serialPort.addEventListener(this);
					serialPort.setParams(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
				}
			} catch (SerialPortException e) {
				throw new Exception(SERIAL_PORT_NOT_WORKING);
			}
		}
	}

	// Closes serial connection if open
	public void closeConnection() throws Exception {
		if (serialPort != null && serialPort.isOpened()) {
			if (serialPort.closePort()) {
				serialPort = null;
			} else throw new Exception("Port could not be closed");
		}
	}

	public boolean isConnected() {
		return (serialPort != null && serialPort.isOpened());
	}

	@Override
	public void serialEvent(SerialPortEvent event) {
		if (event.isRXCHAR() && event.getEventValue() > 0) { // If data is available && there are more than 0 bytes
			try {
				int byteCount = event.getEventValue();
				byte[] byteStr = serialPort.readBytes(byteCount);
				if (byteStr != null) {
					for (byte b : byteStr) {
						sendToParser(Helpers.toNbitBinaryString(String.valueOf(b & 0xFF), 8));
					}
				}
			} catch (SerialPortException e) {
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
		serialPort.writeString(string);
	}

	private void notifyFrame(Frame value) {
		setChanged();
		notifyObservers(value);
		FrameParser.resetCommunication();
	}
	
	public void writeBytes(List<Byte> byteList) throws SerialPortException {
		for(Byte b: byteList) {
			serialPort.writeByte(b);
		}
	}
}
