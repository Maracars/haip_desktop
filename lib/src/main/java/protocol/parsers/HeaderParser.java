package protocol.parsers;

import static protocol.ProtocolProperties.COUNTER;
import static protocol.ProtocolProperties.PACKET_TYPE;
import static protocol.ProtocolProperties.START_FRAME;

import java.util.List;

import models.Frame;
import models.Header;

public class HeaderParser implements Parser{

	@Override
	public Frame parseRx(Frame frame, String byteString) {
		Header header = new Header();
		// TODO Here instead of substrings we should create our own funct. e.g. getFramePart(byteString, START_FRAME)
		try {
			header.setStartFrame(byteString.substring(0, START_FRAME));
			header.setPacketType(byteString.substring(START_FRAME, START_FRAME + PACKET_TYPE));
			header.setCounter(byteString.substring(START_FRAME + PACKET_TYPE, START_FRAME + PACKET_TYPE + COUNTER));
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
