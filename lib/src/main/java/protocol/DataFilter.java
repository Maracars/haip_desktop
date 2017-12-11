package protocol;

import static protocol.ProtocolProperties.DESTINATION_ID;
import static protocol.ProtocolProperties.HEADER;
import static protocol.ProtocolProperties.LENGTH;
import static protocol.ProtocolProperties.ORIGIN_ID;

import java.util.List;

import models.Data;
import models.Frame;

public class DataFilter implements FrameFilter{

	Data data;

	public DataFilter() {
		data = new Data();
	}

	@Override
	public Frame parseRx(Frame frame, String byteString) {
		try {	
			Data data = new Data(byteString.substring(HEADER + ORIGIN_ID + DESTINATION_ID + LENGTH,  HEADER + ORIGIN_ID + DESTINATION_ID + LENGTH + Integer.parseInt(frame.getLength(), 2)*8));
			frame.setData(data);
		} catch (StringIndexOutOfBoundsException | NumberFormatException e) {
			frame.setData(null);
		}
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
