package protocol.parsers;

import models.Frame;

import java.util.List;

import static protocol.ProtocolProperties.HEADER;
import static protocol.ProtocolProperties.ORIGIN_ID;

public class OriginParser implements Parser {

	@Override
	public Frame parseRx(Frame frame, String byteString) {
		try {
			frame.setOriginId(byteString.substring(HEADER, HEADER + ORIGIN_ID));
		} catch (StringIndexOutOfBoundsException e) {
			frame.setOriginId(null);
		}
		return frame;
	}

	@Override
	public List<Byte> parseTx(Frame frame, List<Byte> byteList) {
		byteList.add(Byte.parseByte(frame.getOriginId(), 2));
		return byteList;
	}

}
