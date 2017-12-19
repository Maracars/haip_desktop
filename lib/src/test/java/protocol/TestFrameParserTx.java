package protocol;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Test;

import helpers.CRC8;
import models.Data;
import models.Frame;
import models.Header;
import models.Status;

public class TestFrameParserTx {
	
	private static String PACKET_OK_NO_CHECK_NO_DATA = "000000000000000011111111";
	private static String PACKET_OK_NO_CHECK_1_BYTE_DATA = "00110000000000001111111110000111";
	private static String PACKET_OK_NO_CHECK_2_BYTE_DATA = "0101000000000000111111111010001110101010";

	@After
	public void resetCommunication() {
		FrameParser.resetCommunication();
	}
	
	@Test
	public void checkParserTxOkNoData() {
		Header header = new Header("000", "00", "000");
		Frame frame = new Frame(header, "00000000", "11111111", null);
		String checksum = CRC8.toCRC8(PACKET_OK_NO_CHECK_NO_DATA);
		frame.setChecksum(checksum);
		List<String> parserByteList = FrameParser.parseTx(frame);
		List<String> myByteList = createMyByteList(frame);
		assertThat(parserByteList, is(myByteList));
	}
	
	@Test
	public void checkParserTxOk1ByteData() {
		Header header = new Header("001", "10", "000");
		Status status = new Status("00", "01", "11");
		Data data = new Data("10", status);
		Frame frame = new Frame(header, "00000000", "11111111", data);
		String checksum = CRC8.toCRC8(PACKET_OK_NO_CHECK_1_BYTE_DATA);
		frame.setChecksum(checksum);
		List<String> parserByteList = FrameParser.parseTx(frame);
		List<String> myByteList = createMyByteList(frame);
		assertThat(parserByteList, is(myByteList));
	}
	
	@Test
	public void checkParserTxOk2ByteData() {
		Header header = new Header("001", "10", "000");
		Status status = new Status("10", "00", "11");
		Data data = new Data("10", status, "10101010");
		Frame frame = new Frame(header, "00000000", "11111111", data);
		String checksum = CRC8.toCRC8(PACKET_OK_NO_CHECK_2_BYTE_DATA);
		frame.setChecksum(checksum);
		List<String> parserByteList = FrameParser.parseTx(frame);
		List<String> myByteList = createMyByteList(frame);
		assertThat(parserByteList, is(myByteList));
	}
	
	
	private List<String> createMyByteList(Frame frame) {
		List<String> myByteList = new ArrayList<String>();
		myByteList.add(frame.getHeader().toString());
		myByteList.add(frame.getOriginId());
		myByteList.add(frame.getDestinationId());
		if(frame.getData() != null)
			myByteList.add(frame.getData().toString());
		myByteList.add(frame.getChecksum());
		return myByteList;
	}
}
