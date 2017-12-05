package protocol;

import java.util.List;

import models.Frame;

public class LengthFilter implements FrameFilter{

	@Override
	public Frame parseRx(Frame frame, String byteString) {
		frame.setLength(byteString);
		return frame;
	}

	@Override
	public boolean filter(Frame frame) {
		return true;
	}

	@Override
	public List<String> parseTx(Frame frame, List<String> byteList) {
		byteList.add(frame.getLength().toString());
		return byteList;
	}

}
