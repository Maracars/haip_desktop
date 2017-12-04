package protocol;

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

}
