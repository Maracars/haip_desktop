package protocol;

import static protocol.ProtocolProperties.HEADER;
import static protocol.ProtocolProperties.ORIGIN_ID;
import static protocol.ProtocolProperties.DESTINATION_ID;

import java.util.List;

import models.Frame;

public class DestinationFilter implements FrameFilter{

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
	public boolean filter(Frame frame) {
		//Check if both origin and destination are not equal
		if (frame.getOriginId().equals(frame.getDestinationId()))
			return false;
		return true;
	}

	@Override
	public List<String> parseTx(Frame frame, List<String> byteList) {
		byteList.add(frame.getDestinationId());
		return byteList;
	}

}
