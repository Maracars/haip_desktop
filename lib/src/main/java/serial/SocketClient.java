package serial;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.Observable;

import helpers.Helpers;
import models.Frame;
import protocol.FrameParser;

public class SocketClient extends Observable{


	private static final String MULTICAST_ADDRESS = "239.0.0.1";
	private static final int MULTICAST_PORT = 8000;
	private static final int UDP_PORT = 6001;

	MulticastSocket clientSocket;
	DatagramSocket udpSocket;
	InetAddress inetAddress;

	public SocketClient() {
		try {
			inetAddress = InetAddress.getByName(MULTICAST_ADDRESS);
			clientSocket = new MulticastSocket(MULTICAST_PORT);
			clientSocket.setTimeToLive(2);
			clientSocket.joinGroup(inetAddress);
			udpSocket = new DatagramSocket(UDP_PORT);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void connect() {
		DatagramPacket pOut;
		byte[] bufferOut = "6001".getBytes();
		pOut = new DatagramPacket(bufferOut, bufferOut.length, inetAddress, MULTICAST_PORT);
		try {
			clientSocket.send(pOut);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send(byte[] listBytes) {
		try {
			DatagramPacket pOut = new DatagramPacket(listBytes,listBytes.length, InetAddress.getByName("localhost") , 6000);
			clientSocket.send(pOut);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void waitForCommunication() {
		WaitCommunication waitComm = new WaitCommunication(udpSocket);
		waitComm.start();
	}

	public class WaitCommunication extends Thread{

		DatagramSocket udpSocket;

		public WaitCommunication(DatagramSocket udpSocket) {
			this.udpSocket = udpSocket;
		}

		@Override
		public void run() {
			while(true) {
				this.waitCommunication();
			}
		}

		public void waitCommunication() {
			DatagramPacket pIn;
			byte[] bufferIn = new byte[256];
			pIn = new DatagramPacket(bufferIn, bufferIn.length);
			try {
				udpSocket.receive(pIn);
				byte[] byteStr = pIn.getData();
				if (byteStr != null) {
					for (byte b : byteStr) {
						sendToParser(Helpers.toNbitBinaryString(String.valueOf(b & 0xFF), 8));
					}
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		public void sendToParser(String string) {
			boolean validFrame = FrameParser.parseRx(string);
			if (validFrame) {
				notifyFrame(FrameParser.getFrame());
				System.out.println("frame received");
			}
		}

		private void notifyFrame(Frame value) {
			setChanged();
			notifyObservers(value);
			FrameParser.resetCommunication();
		}

	}

}
