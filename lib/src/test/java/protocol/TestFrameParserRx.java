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
		boolean parsed = FrameParser.parseRx(PACKET);
		if (parsed) {
			System.out.println("Correct");
		}
		else {
			System.out.println("Error");
		}
	}

	public static void main(String[] args) {
		new TestFrameParserRx().checkParserTx();
	}

}
