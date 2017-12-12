package helpers;

import java.util.Arrays;
import java.util.List;

import models.Frame;
import protocol.FrameParser;
import serial.Serial;

public class Helpers {


	public static String toByteBinString(String str) {
		if (str == null || str.isEmpty()) return "";

		return String.format("%8s", Integer.toBinaryString((byte) Integer.parseInt(str) & 0xFF)).replace(' ', '0');
	}
	
	public static void sendParsedFrame(Frame frame, Serial serial) {
		List<String> listBytes = FrameParser.parseTx(frame);
		try {

			serial.writeStrings(listBytes);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	public static <E extends Enum<E>> boolean isInEnums(Class<E> e, String eq) {
		return Arrays.stream(e.getEnumConstants()).map(Enum::toString).anyMatch(s -> s.equals(eq));

	}
}
