package protocol;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import models.Frame;
import serial.Serial;

public class TestFrameParserRx {
	
	private final static String PACKET = "0100000000000000111111110000000011111111";
	private final static String HEADER = "01000000";
	private final static String ORIGIN_ID = "00000000";
	private final static String DESTINATION_ID = "11111111";
	private final static String LENGTH = "00000000";
	private final static String CHECKSUM = "11111111";

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
}
