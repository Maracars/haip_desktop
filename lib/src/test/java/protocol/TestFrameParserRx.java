package protocol;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Test;

import helpers.CRC8;

public class TestFrameParserRx {

	private static String PACKET = "10100000000000001111111100000000";
	private static String PACKET_WITH_CHECKSUM = PACKET;
	private static String PACKET_WITH_DATA_NOT_FULL = "10100000000000001111111100000001111";
	private static String PACKET_WITH_DATA_FULL_1_BYTE = "1010000000000000111111110000000100000000";
	private static String PACKET_WITH_CHECKSUM_1_BYTE = PACKET_WITH_DATA_FULL_1_BYTE;
	private static String PACKET_WITH_DATA_FULL_2_BYTE = "101100000000000011111111000000101010101100000000";
	private static String PACKET_WITH_CHECKSUM_2_BYTE = PACKET_WITH_DATA_FULL_2_BYTE;
	
	@After
	public void resetCommunication() {
		FrameParser.resetCommunication();
	}

	@Test
	public void checkParseRxOk() {
		String checksum = CRC8.toCRC8(PACKET);
		PACKET_WITH_CHECKSUM += checksum;
		int result = FrameParser.parseRx(PACKET_WITH_CHECKSUM);
		assertEquals("Parser error", FrameParser.FIN_PACKET, result);
	}

	@Test
	public void checkParseRxNotFullPacketWithoutData() {
		int result = FrameParser.parseRx(PACKET);
		assertEquals("Parser error", FrameParser.UNFIN_PACKET, result);
	}

	@Test
	public void checkParseRxNotFullPacketWithData() {
		int result = FrameParser.parseRx(PACKET_WITH_DATA_NOT_FULL);
		assertEquals("Parser error", FrameParser.UNFIN_PACKET, result);
	}
	
	@Test
	public void checkParseRxOkData1Byte() {
		String checksum = CRC8.toCRC8(PACKET_WITH_DATA_FULL_1_BYTE);
		PACKET_WITH_CHECKSUM_1_BYTE += checksum;
		int result = FrameParser.parseRx(PACKET_WITH_CHECKSUM_1_BYTE);
		assertEquals("Parser error", FrameParser.FIN_PACKET, result);
	}
	
	@Test
	public void checkParseRxOkData2ByteWithParking() {
		String checksum = CRC8.toCRC8(PACKET_WITH_DATA_FULL_2_BYTE);
		PACKET_WITH_CHECKSUM_2_BYTE += checksum;
		int result = FrameParser.parseRx(PACKET_WITH_CHECKSUM_2_BYTE);
		assertEquals("Parser error", FrameParser.FIN_PACKET, result);
	}
}
