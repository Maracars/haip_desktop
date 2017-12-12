package helpers;


public class CRC8 {
	private static final int poly = 0x0D5;
	private static int crc = 0;

	public static void update(final byte[] input, final int offset, final int len) {
		for (int i = 0; i < len; i++) {
			update(input[offset + i]);
		}
	}

	public static void update(final byte[] input) {
		update(input, 0, input.length);
	}

	private static void update(final byte b) {
		crc ^= b;
		for (int j = 0; j < 8; j++) {
			if ((crc & 0x80) != 0) {
				crc = ((crc << 1) ^ poly);
			} else {
				crc <<= 1;
			}
		}
		crc &= 0xFF;
	}

	public static void update(final int b) {
		update((byte) b);
	}

	public static int getValue() {
		return (crc & 0xFF);
	}

	public static void reset() {
		crc = 0;
	}

	public static String toCRC8(String string) {
		reset();
		update(string.getBytes());
		return fillWithZeros(Integer.toBinaryString(getValue()));
	}

	public static String fillWithZeros(String binaryString) {
		if (binaryString.length() < 8) {
			for (int i = binaryString.length(); i < 8; i++) {
				binaryString = "0" + binaryString;
			}
		}
		return binaryString;
	}
}