package serial;

import java.io.InputStream;
import java.io.OutputStream;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;
import protocol.FrameParser;

public class Serial implements SerialPortEventListener{

	private static final int BAUD_RATE = 9600;
	SerialPort serialPort;
	OutputStream outputStream;
	InputStream inputStream;
	//FrameParser frameParser;

	public void startConnection() {
		serialPort = null;
		outputStream = null;
		inputStream = null;
		String[] portNames = SerialPortList.getPortNames();

		//modeloLog.add(textosLog[0]);

		//Si no hay cable conectado
		if (portNames.length == 0) {
			//modeloLog.add(textosLog[1]);
		}
		else {
			//Si hay uno
			if (portNames.length == 1) {
				serialPort = new SerialPort(portNames[0]);
			}
			//Si hay m·s de uno
			else {
				/*diagPuertos = new DialogoPuertos(null, portNames);
        		if (diagPuertos.getSelectedIndex() == -1) {
        			modeloLog.add(textosLog[2]);
        			return;
        		}
        		else {
                	serialPort = new SerialPort(portNames[diagPuertos.getSelectedIndex()]);
        		}*/
			}
			//Abre el puerto
			try {
				serialPort.openPort();
				serialPort.addEventListener(this);
				serialPort.setParams(BAUD_RATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

				//conexionEstablecida = true;
				//modeloLog.add(textosLog[3]);
			}
			catch (SerialPortException e) {
				//modeloLog.add(textosLog[11]);
			}
		}
	}

	public void serialEvent(SerialPortEvent arg0) {
		try {
			int[] a = serialPort.readIntArray();
			int zbk = 0;
			for (int f : a) {
				zbk = f;
			}
			String binario = rellenarDatoRecibir(Integer.toBinaryString(zbk));
			//frameParser.parse(binario)
			System.out.println(binario);

		} catch (SerialPortException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String rellenarDatoRecibir(String dato) {
		if (dato.length() < 8) {
			for (int i = dato.length(); i < 8; i++) {
				dato = "0" + dato;
			}
		}
		return dato;
	}

}
