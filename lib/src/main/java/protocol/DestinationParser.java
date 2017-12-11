package protocol;

import static protocol.ProtocolProperties.DESTINATION_ID;
import static protocol.ProtocolProperties.HEADER;
import static protocol.ProtocolProperties.ORIGIN_ID;

import java.util.List;

import models.Frame;

public class DestinationParser implements Parser{

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
