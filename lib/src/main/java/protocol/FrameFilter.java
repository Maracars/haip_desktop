package protocol;

import models.Frame;

import java.util.List;

public interface FrameFilter {
	
	public Frame parseRx(Frame frame, String byteString);
	
	public boolean filter(Frame frame);
	
	public List<String> parseTx(Frame frame, List<String> byteList);

}
