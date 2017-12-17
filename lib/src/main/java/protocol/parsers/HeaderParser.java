package protocol.parsers;

import java.util.List;

import models.Frame;
import models.Header;

import static protocol.ProtocolProperties.*;

public class HeaderParser implements Parser{

	@Override
	public Frame parseRx(Frame frame, String byteString) {
		Header header = new Header();
		// TODO Here instead of substrings we should create our own funct. e.g. getFramePart(byteString, START_FRAME)
		try {
			header.setLength(byteString.substring(0, LENGTH));
			header.setPacketType(byteString.substring(LENGTH, LENGTH + PACKET_TYPE));
			header.setCounter(byteString.substring(LENGTH + PACKET_TYPE, LENGTH + PACKET_TYPE + COUNTER));
			frame.setHeader(header);
		} catch (StringIndexOutOfBoundsException e) {
			frame.setHeader(null);
		}
		return frame;
	}

	@Override
	public List<String> parseTx(Frame frame, List<String> byteList) {
		byteList.add(frame.getHeader().toString());
		return byteList;
	}

}
