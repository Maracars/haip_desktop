package protocol.parsers;

import models.Frame;

import java.util.List;

import static protocol.ProtocolProperties.*;

public class DestinationParser implements Parser {

	@Override
	public Frame parseRx(Frame frame, String byteString) {
		try {
			frame.setDestinationId(byteString.substring(HEADER + ORIGIN_ID, HEADER + ORIGIN_ID + DESTINATION_ID));
		} catch (StringIndexOutOfBoundsException e) {
			frame.setDestinationId(null);
		}
		return frame;
	}

	@Override
	public List<String> parseTx(Frame frame, List<String> byteList) {
		byteList.add(frame.getDestinationId());
		return byteList;
	}

}
