package protocol;

import java.util.List;

import models.Frame;

public class ChecksumFilter implements FrameFilter{

	@Override
	public Frame parseRx(Frame frame, String byteString) {
		frame.setChecksum(byteString);
		return frame;
	}

	@Override
	public boolean filter(Frame frame) {
		return true;
	}

	@Override
	public List<String> parseTx(Frame frame, List<String> byteList) {
		byteList.add(frame.getChecksum().toString());
		return byteList;
	}

}
