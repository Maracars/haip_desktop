package protocol;

import models.Frame;

public class ChecksumFilter implements FrameFilter{

	@Override
	public Frame parseRx(Frame frame, String byteString) {
		frame.setChecksum(byteString);
		return frame;
	}

	@Override
	public boolean filter(Frame frame) {
		// TODO Auto-generated method stub
		return false;
	}

}
