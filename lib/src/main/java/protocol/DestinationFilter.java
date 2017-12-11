package protocol;

import java.util.List;

import models.Frame;

public class DestinationFilter implements FrameFilter{
	
	@Override
	public Frame parseRx(Frame frame, String byteString) {
		frame.setDestinationId(byteString);
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
