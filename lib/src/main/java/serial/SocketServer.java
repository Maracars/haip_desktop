package serial;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;

import helpers.Helpers;
import models.Frame;
import protocol.FrameParser;

public class SocketServer extends Observable implements Runnable{

	private static final String MULTICAST_ADDRESS = "239.0.0.1";
	private static final int MULTICAST_PORT = 8000;
	private static final int UDP_PORT = 6000;

	MulticastSocket serverSocket;
	Map<String, Integer> connectedList; 
	DatagramSocket udpSocket;
	Thread thread;

	public SocketServer() {
		try {
			InetAddress inetAddress = InetAddress.getByName(MULTICAST_ADDRESS);
			serverSocket = new MulticastSocket(MULTICAST_PORT);
			serverSocket.joinGroup(inetAddress);
			connectedList = new HashMap<>();
			udpSocket = new DatagramSocket(UDP_PORT);
			thread = new Thread(this);
			thread.start();
			WaitCommunication waitComm = new WaitCommunication(udpSocket);
			waitComm.start();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void waitConnections() {
		byte[] bufferIn = new byte[256];
		DatagramPacket pIn;
		String port = "";
		pIn = new DatagramPacket(bufferIn, bufferIn.length);
		try {
			System.out.println("receive");
			serverSocket.receive(pIn);
			port = new String(pIn.getData(), 0, pIn.getLength());
			System.out.println(port);
			connectedList.put(pIn.getAddress().getHostAddress(), Integer.parseInt(port));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void send(byte[] listBytes) {
		Collection<String> addresses = connectedList.keySet();
		Iterator<String> iterator = addresses.iterator();
		while(iterator.hasNext()) {
			String address = iterator.next();
			int port = connectedList.get(address);
			DatagramPacket pOut;
			try {
				pOut = new DatagramPacket(listBytes,listBytes.length, InetAddress.getByName(address) , port);
				serverSocket.send(pOut);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	@Override
	public void run() {
		while(true) {
			this.waitConnections();
		}
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
						System.out.println(b);
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
			}
		}
		
		private void notifyFrame(Frame value) {
			setChanged();
			notifyObservers(value);
			FrameParser.resetCommunication();
		}

	}





}
