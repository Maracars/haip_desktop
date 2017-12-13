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

public class Serial extends Observable implements SerialPortEventListener {

    private SerialPort serialPort;
    private String packet;
    private boolean isConnected;

    public Serial() {
        serialPort = null;
        isConnected = false;
        packet = "";
    }

    // Opens serial connection
    public void openConnection() throws Exception {
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
            serialPort.setParams(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

            isConnected = true;
        }
    }

    // Closes serial connection if open
    public void closeConnection() throws SerialPortException {
        if (serialPort != null) {
            serialPort.closePort();
            serialPort = null;
            isConnected = false;
            packet = "";
        }
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void serialEvent(SerialPortEvent arg0) {
        try {
            String readString = serialPort.readString();
            //System.out.println(readString);
            packet += readString;
            //System.out.println(packet);

            int result = FrameParser.parseRx(packet);
            if (result == FrameParser.BAD_PACKET) {
                packet = "";
            } else if (result == FrameParser.FIN_PACKET) {
                notifyFrame(FrameParser.getFrame());
            }
        } catch (SerialPortException e) {
            e.printStackTrace();
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
        packet = "";
    }

}
