package helpers;

import org.junit.Test;


import static org.junit.Assert.assertEquals;

public class CRC8Test {

	@Test
	public void checkChecksum() {

		assertEquals("181", CRC8.toCRC8("test"));

		assertEquals("59", CRC8.toCRC8("hello world"));

	}

}
