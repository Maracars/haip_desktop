package helpers;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CRC8Test {

	private static final String BINARY_TEST = "10110101";

	private static final String BINARY_HELLO_WORLD = "00111011";

	@Test
	public void checkChecksumTest() {
		assertEquals(BINARY_TEST, CRC8.toCRC8("test"));
	}

	@Test
	public void checkChecksumHelloWorld() {
		assertEquals(BINARY_HELLO_WORLD, CRC8.toCRC8("hello world"));
	}

}
