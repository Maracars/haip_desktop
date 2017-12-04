package helpers;

import org.junit.Test;


import static org.junit.Assert.assertEquals;

public class CRC8Test {

	@Test
	public void checkChecksum() {
		CRC8 crc = new CRC8();
		crc.reset();
		crc.update("test".getBytes());
		assertEquals(181, crc.getValue());
		crc.reset();
		crc.update("hello world".getBytes());
		assertEquals(59, crc.getValue());

	}

}
