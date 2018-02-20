package helpers;

import java.util.Arrays;
import java.util.List;

import models.Frame;
import protocol.FrameParser;
import protocol.ProtocolProperties.PacketType;
import serial.Serial;
import serial.SocketClient;
import serial.SocketServer;

public class Helpers {


	public static String toNbitBinaryString(String str, Integer num_bits) {
		if (str == null || str.isEmpty()) return "";

		return String.format("%" + num_bits + "s", Integer.toBinaryString((byte) Integer.parseInt(str) & 0xFF)).replace(' ', '0');
	}

	public static synchronized void sendParsedFrame(Frame frame, Serial serial, SocketServer socketServer, SocketClient socketClient) {
		List<Byte> listBytes = FrameParser.parseTx(frame);
		try {
			if(frame.getHeader().getPacketType().equals(PacketType.ACK.toString()))
				System.out.println("ACK Send by "+Integer.parseInt(frame.getOriginId(),2));
			byte[] byteArray = Helpers.byteListToArray(listBytes);
			if(socketServer != null) {
				System.out.println("enviando algo");
				socketServer.send(byteArray);
			}else {
				socketClient.send(byteArray);
			}
				
			//serial.writeBytes(listBytes);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static byte[] byteListToArray(List<Byte> listBytes) {
		byte[] byteArray = new byte[listBytes.size()];
		for(int i = 0; i < byteArray.length; i++) {
			byteArray[i] = listBytes.get(i);
		}
		return byteArray;
	}

	public static <E extends Enum<E>> boolean isInEnums(Class<E> e, String eq) {
		return Arrays.stream(e.getEnumConstants()).map(Enum::toString).anyMatch(s -> s.equals(eq));

	}

	public static String[] getNames(Class<? extends Enum<?>> e) {
		return Arrays.stream(e.getEnumConstants()).map(Enum::name).toArray(String[]::new);
	}
	
	public static byte getUnsignedByte(String binaryString) {
		return (byte) (Integer.parseInt(binaryString, 2) & 0xFF);
	}
}
