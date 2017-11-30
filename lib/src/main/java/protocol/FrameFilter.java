package protocol;

import models.Frame;

public interface FrameFilter {
	
	public boolean filter(Frame frame);

}
