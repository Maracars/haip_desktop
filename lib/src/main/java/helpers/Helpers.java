package helpers;

import models.Frame;
import protocol.FrameParser;
import serial.Serial;

import java.util.Arrays;
import java.util.List;

public class Helpers {


	public static String toByteBinString(String str, Integer num_bits) {
		if (str == null || str.isEmpty()) return "";

		return String.format("%" + num_bits + "s", Integer.toBinaryString((byte) Integer.parseInt(str) & 0xFF)).replace(' ', '0');
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

	public static String[] getNames(Class<? extends Enum<?>> e) {
		return Arrays.stream(e.getEnumConstants()).map(Enum::name).toArray(String[]::new);
	}
}
