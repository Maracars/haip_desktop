package protocol;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Test;

import models.Frame;

public class TestFrameParserRx {

	private final static String PACKET = "0100000000000000111111110000000011111111";
	private final static String PACKET_WITH_DATA_1_BYTE = "010000000000000011111111000000010010101011111111";
	private final static String PACKET_WITH_DATA_2_BYTE = "01000000000000001111111100000010001010100010101011111111";
	private final static String HEADER = "01000000";
	private final static String ORIGIN_ID = "00000000";
	private final static String DESTINATION_ID = "11111111";
	private final static String LENGTH = "00000000";
	private final static String CHECKSUM = "11111111";

	@After
	public void resetCommunication() {
		FrameParser.resetCommunication();
	}

	// TODO All these should be different, we receive byte by byte or whatever, but we should parse everything in one
	@Test
	public void checkParseRxOk() {
		FrameParser.parseRx(PACKET);
		assertEquals("Parser error", PACKET, FrameParser.frame.toString());
	}

	@Test
	public void checkParseRxBadOrigin() {
		FrameParser.parseRx(HEADER);
		FrameParser.parseRx("00000001");
		assertEquals("Parser error", null, FrameParser.frame.getDestinationId());
	}

	@Test
	public void checkParseRxOringAndDestinationEqual() {
		FrameParser.parseRx(HEADER);
		FrameParser.parseRx("00000000");
		FrameParser.parseRx("00000000");
		assertEquals("Parser error", null, FrameParser.frame.getLength());
	}

	@Test
	public void checkParseRxPacketFilterNotExist() {
		FrameParser.parseRx("01011000");
		assertEquals("Parser error", null, FrameParser.frame.getOriginId());
	}

	@Test
	public void checkParseRxPacketDataParser1Byte() {
		FrameParser.parseRx(PACKET_WITH_DATA_1_BYTE);
		assertEquals("Parser error", PACKET_WITH_DATA_1_BYTE, FrameParser.frame.toString());
	}

	@Test
	public void checkParseRxPacketDataParser2Byte() {
		FrameParser.parseRx(PACKET_WITH_DATA_2_BYTE);
		assertEquals("Parser error", PACKET_WITH_DATA_2_BYTE, FrameParser.frame.toString());
	}
}
