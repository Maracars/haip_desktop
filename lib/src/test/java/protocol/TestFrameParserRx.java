package protocol;

import helpers.CRC8;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestFrameParserRx {

	private static String PACKET_OK_NO_CHECK_NO_DATA = "000000000000000011111111";
	private static String PACKET_OK_WITH_CHECK_NO_DATA = PACKET_OK_NO_CHECK_NO_DATA;
	private static String PACKET_OK_NO_CHECK_1_BYTE_DATA = "00110000000000001111111110000111";
	private static String PACKET_OK_WITH_CHECK_1_BYTE_DATA = PACKET_OK_NO_CHECK_1_BYTE_DATA;
	private static String PACKET_OK_NO_CHECK_2_BYTE_DATA = "0101000000000000111111111010001110101010";
	private static String PACKET_OK_WITH_CHECK_2_BYTE_DATA = PACKET_OK_NO_CHECK_2_BYTE_DATA;

	private static String PACKET_NO_CHECK_ORIGIN_AND_DEST_EQUAL = "000000000000000000000000";
	private static String PACKET_WITH_CHECK_ORIGIN_AND_DEST_EQUAL = PACKET_NO_CHECK_ORIGIN_AND_DEST_EQUAL;
	private static String PACKET_NO_CHECK_DATA_TYPE_FALSE = "00110000000000001111111111000111";
	private static String PACKET_WITH_CHECK_DATA_TYPE_FALSE = PACKET_NO_CHECK_DATA_TYPE_FALSE;
	private static String PACKET_NO_CHECK_STATUS_TYPE_FALSE = "00110000000000001111111110110111";
	private static String PACKET_WITH_CHECK_STATUS_TYPE_FALSE = PACKET_NO_CHECK_STATUS_TYPE_FALSE;
	private static String PACKET_NO_CHECK_ACTION_TYPE_FALSE = "00110000000000001111111110001111";
	private static String PACKET_WITH_CHECK_ACTION_TYPE_FALSE = PACKET_NO_CHECK_STATUS_TYPE_FALSE;

	private static String PACKET_WITH_CHECKSUM_FALSE = "00000000000000001111111110101010";

	@After
	public void resetCommunication() {
		FrameParser.resetCommunication();
	}

	@Test
	public void checkParserRxOkNoData() {
		String checksum = CRC8.toCRC8(PACKET_OK_NO_CHECK_NO_DATA);
		PACKET_OK_WITH_CHECK_NO_DATA += checksum;
		boolean parsed = FrameParser.parseRx(PACKET_OK_WITH_CHECK_NO_DATA);
		assertTrue("Parser error", parsed);
	}

	@Test
	public void checkParserRxOk1ByteData() {
		String checksum = CRC8.toCRC8(PACKET_OK_NO_CHECK_1_BYTE_DATA);
		PACKET_OK_WITH_CHECK_1_BYTE_DATA += checksum;
		boolean parsed = FrameParser.parseRx(PACKET_OK_WITH_CHECK_1_BYTE_DATA);
		assertTrue("Parser error", parsed);
	}

	@Test
	public void checkParserRxOk2ByteData() {
		String checksum = CRC8.toCRC8(PACKET_OK_NO_CHECK_2_BYTE_DATA);
		PACKET_OK_WITH_CHECK_2_BYTE_DATA += checksum;
		boolean parsed = FrameParser.parseRx(PACKET_OK_WITH_CHECK_2_BYTE_DATA);
		assertTrue("Parser error", parsed);
	}

	@Test
	public void checkParserRxIdentificationFilter() {
		String checksum = CRC8.toCRC8(PACKET_NO_CHECK_ORIGIN_AND_DEST_EQUAL);
		PACKET_WITH_CHECK_ORIGIN_AND_DEST_EQUAL += checksum;
		boolean parsed = FrameParser.parseRx(PACKET_WITH_CHECK_ORIGIN_AND_DEST_EQUAL);
		assertFalse("Parser error", parsed);
	}

	@Test
	public void checkParserRxDataTypeFilter() {
		String checksum = CRC8.toCRC8(PACKET_NO_CHECK_DATA_TYPE_FALSE);
		PACKET_WITH_CHECK_DATA_TYPE_FALSE += checksum;
		boolean parsed = FrameParser.parseRx(PACKET_WITH_CHECK_DATA_TYPE_FALSE);
		assertFalse("Parser error", parsed);
	}

	@Test
	public void checkParserRxStatusTypeFilter() {
		String checksum = CRC8.toCRC8(PACKET_NO_CHECK_STATUS_TYPE_FALSE);
		PACKET_WITH_CHECK_STATUS_TYPE_FALSE += checksum;
		boolean parsed = FrameParser.parseRx(PACKET_WITH_CHECK_STATUS_TYPE_FALSE);
		assertFalse("Parser error", parsed);
	}

	@Test
	public void checkParserRxActionTypeFilter() {
		String checksum = CRC8.toCRC8(PACKET_NO_CHECK_ACTION_TYPE_FALSE);
		PACKET_WITH_CHECK_ACTION_TYPE_FALSE += checksum;
		boolean parsed = FrameParser.parseRx(PACKET_WITH_CHECK_ACTION_TYPE_FALSE);
		assertFalse("Parser error", parsed);
	}

	@Test
	public void checkParserRxChecksumFilter() {
		boolean parsed = FrameParser.parseRx(PACKET_WITH_CHECKSUM_FALSE);
		assertFalse("Parser error", parsed);
	}

}
