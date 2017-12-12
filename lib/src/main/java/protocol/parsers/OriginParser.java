package protocol.parsers;

import static protocol.ProtocolProperties.HEADER;
import static protocol.ProtocolProperties.ORIGIN_ID;

import java.util.List;

import models.Frame;

public class OriginParser implements Parser{

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
	public List<String> parseTx(Frame frame, List<String> byteList) {
		byteList.add(frame.getOriginId());
		return byteList;
	}

}
