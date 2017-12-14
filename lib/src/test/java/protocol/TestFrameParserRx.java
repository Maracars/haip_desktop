package protocol;

import org.junit.After;
import org.junit.Test;

public class TestFrameParserRx {

	private static String PACKET = "101100000000001000000000000010001010101000110001";
	
	@After
	public void resetCommunication() {
		FrameParser.resetCommunication();
	}

	@Test
	public void checkParserTx() {
		int result = FrameParser.parseRx(PACKET);

		if (result == FrameParser.BAD_PACKET) {
			System.out.println("Error");
		}
		else if (result == FrameParser.FIN_PACKET) {
			System.out.println("Correct");
		}
		else if (result == FrameParser.UNFIN_PACKET) {
			System.out.println("Unfin");
		}
	}

	public static void main(String[] args) {
		new TestFrameParserRx().checkParserTx();
	}

}
