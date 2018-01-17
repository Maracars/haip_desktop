package helpers;

import models.Frame;
import protocol.FrameParser;
import serial.Serial;

import java.util.Arrays;
import java.util.List;

public class Helpers {


	public static String toNbitBinaryString(String str, Integer num_bits) {
		if (str == null || str.isEmpty()) return "";

		return String.format("%" + num_bits + "s", Integer.toBinaryString((byte) Integer.parseInt(str) & 0xFF)).replace(' ', '0');
	}

	public static synchronized void sendParsedFrame(Frame frame, Serial serial) {
		List<Byte> listBytes = FrameParser.parseTx(frame);
		try {

			serial.writeBytes(listBytes);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
