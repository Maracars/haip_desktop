package protocol;

import  models.Frame;

public interface FrameFilter {
	
	public Frame parseRx(Frame frame, String byteString);
	
	public boolean filter(Frame frame);

}
