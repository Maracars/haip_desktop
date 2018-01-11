package serial;

import org.junit.Test;

public class SerialTxTest {

	private Serial serialTx;

	public static void main(String[] args) {
		new SerialTxTest().testComm();
	}

	@Test
	public void testComm() {
		serialTx = new Serial();

		try {
			serialTx.openConnection();
			serialTx.writeString("10101000" + "10101010" + "00000000" + "00000001" + "01100111" + "01001100");
			serialTx.closeConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}