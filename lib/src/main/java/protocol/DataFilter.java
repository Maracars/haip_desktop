package protocol;

import models.Data;
import models.Frame;

import java.util.List;

public class DataFilter implements FrameFilter{
	
	Data data;
	
	public DataFilter() {
		data = new Data();
	}

	@Override
	public Frame parseRx(Frame frame, String byteString) {
		data.getData().add(byteString);
		frame.setData(data);
		return frame;
	}

	@Override
	public boolean filter(Frame frame) {
		return true;
	}

	@Override
	public List<String> parseTx(Frame frame, List<String> byteList) {
		byteList.add(frame.getData().toString());
		return byteList;
	}
	
}
