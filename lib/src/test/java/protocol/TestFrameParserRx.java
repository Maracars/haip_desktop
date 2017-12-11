package protocol;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import models.Frame;
import serial.Serial;

public class TestFrameParserRx {
	
	private final static String PACKET = "0100000000000000111111110000000011111111";
	private final static String PACKET_WITH_DATA_1_BYTE = "010000000000000011111111000000010010101011111111";
	private final static String PACKET_WITH_DATA_2_BYTE = "01000000000000001111111100000010001010100010101011111111";
	private final static String HEADER = "01000000";
	private final static String ORIGIN_ID = "00000000";
	private final static String DESTINATION_ID = "11111111";
	private final static String LENGTH = "00000000";
	private final static String CHECKSUM = "11111111";

	// TODO All these should be different, we receive byte by byte or whatever, but we should parse everything in one
	@Test
	public void checkParseRxOk() {
		Serial serialTx = new Serial();
		FrameParser frameParser = new FrameParser(serialTx);
		frameParser.parseRx(HEADER);
		frameParser.parseRx(ORIGIN_ID);
		frameParser.parseRx(DESTINATION_ID);
		frameParser.parseRx(LENGTH);
		frameParser.parseRx(CHECKSUM);
		Frame frame = frameParser.getFrame();
		assertEquals("Parser error", PACKET, frame.toString());
	}
	
	@Test
	public void checkParseRxBadOrigin() {
		Serial serialTx = new Serial();
		FrameParser frameParser = new FrameParser(serialTx);
		frameParser.parseRx(HEADER);
		frameParser.parseRx("00000001");
		Frame frame = frameParser.getFrame();
		assertEquals("Parser error", null, frame.getDestinationId());
	}
	
	@Test
	public void checkParseRxOringAndDestinationEqual() {
		Serial serialTx = new Serial();
		FrameParser frameParser = new FrameParser(serialTx);
		frameParser.parseRx(HEADER);
		frameParser.parseRx("00000000");
		frameParser.parseRx("00000000");
		Frame frame = frameParser.getFrame();
		assertEquals("Parser error", null, frame.getLength());
	}
	
	@Test
	public void checkParseRxPacketFilterNotExist() {
		Serial serialTx = new Serial();
		FrameParser frameParser = new FrameParser(serialTx);
		frameParser.parseRx("01011000");
		Frame frame = frameParser.getFrame();
		assertEquals("Parser error", null, frame.getOriginId());
	}
	
	@Test
	public void checkParseRxPacketDataParser1Byte() {
		Serial serialTx = new Serial();
		FrameParser frameParser = new FrameParser(serialTx);
		frameParser.parseRx(HEADER);
		frameParser.parseRx(ORIGIN_ID);
		frameParser.parseRx(DESTINATION_ID);
		frameParser.parseRx("00000001");
		frameParser.parseRx("00101010");
		frameParser.parseRx(CHECKSUM);
		Frame frame = frameParser.getFrame();
		assertEquals("Parser error", PACKET_WITH_DATA_1_BYTE, frame.toString());
	}
	
	@Test
	public void checkParseRxPacketDataParser2Byte() {
		Serial serialTx = new Serial();
		FrameParser frameParser = new FrameParser(serialTx);
		frameParser.parseRx(HEADER);
		frameParser.parseRx(ORIGIN_ID);
		frameParser.parseRx(DESTINATION_ID);
		frameParser.parseRx("00000010");
		frameParser.parseRx("00101010");
		frameParser.parseRx("00101010");
		frameParser.parseRx(CHECKSUM);
		Frame frame = frameParser.getFrame();
		assertEquals("Parser error", PACKET_WITH_DATA_2_BYTE, frame.toString());
	}
}
