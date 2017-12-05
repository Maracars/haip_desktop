package protocol;

import java.util.List;

import  models.Frame;

public interface FrameFilter {
	
	public Frame parseRx(Frame frame, String byteString);
	
	public boolean filter(Frame frame);
	
	public List<String> parseTx(Frame frame, List<String> byteList);

}
