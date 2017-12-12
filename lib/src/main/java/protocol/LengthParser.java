package protocol;

import static protocol.ProtocolProperties.DESTINATION_ID;
import static protocol.ProtocolProperties.HEADER;
import static protocol.ProtocolProperties.LENGTH;
import static protocol.ProtocolProperties.ORIGIN_ID;

import java.util.List;

import models.Frame;

public class LengthParser implements Parser {

	@Override
	public Frame parseRx(Frame frame, String byteString) {
		try {
			frame.setLength(byteString.substring(HEADER + ORIGIN_ID + DESTINATION_ID, HEADER + ORIGIN_ID + DESTINATION_ID + LENGTH));
		} catch (StringIndexOutOfBoundsException e) {
			frame.setLength(null);
		}
		return frame;
	}

	@Override
	public List<String> parseTx(Frame frame, List<String> byteList) {
		byteList.add(frame.getLength());
		return byteList;
	}

}
