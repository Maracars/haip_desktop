package helpers;

public class Helpers {


	public static String toByteBinString(String str) {
		if (str == null || str.isEmpty()) return "";

		return String.format("%8s", Integer.toBinaryString((byte) Integer.parseInt(str) & 0xFF)).replace(' ', '0');
	}
}
