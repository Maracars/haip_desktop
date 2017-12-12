package protocol;

import static protocol.ProtocolProperties.DESTINATION_ID;
import static protocol.ProtocolProperties.HEADER;
import static protocol.ProtocolProperties.LENGTH;
import static protocol.ProtocolProperties.ORIGIN_ID;
import static protocol.ProtocolProperties.STATUS;
import static protocol.ProtocolProperties.TYPE;
import static protocol.ProtocolProperties.ACTION;
import static protocol.ProtocolProperties.PERMISSION;

import java.util.List;

import models.Data;
import models.Frame;
import models.Status;

public class DataParser implements Parser{

	@Override
	public Frame parseRx(Frame frame, String byteString) {
		try {
			Status status = new Status(byteString.substring(HEADER + ORIGIN_ID + DESTINATION_ID + LENGTH + TYPE, HEADER + ORIGIN_ID + DESTINATION_ID + LENGTH + STATUS), 
					byteString.substring(HEADER + ORIGIN_ID + DESTINATION_ID + LENGTH + TYPE + STATUS, HEADER + ORIGIN_ID + DESTINATION_ID + LENGTH + STATUS + ACTION), 
					byteString.substring(HEADER + ORIGIN_ID + DESTINATION_ID + LENGTH + TYPE + STATUS + ACTION, HEADER + ORIGIN_ID + DESTINATION_ID + LENGTH + STATUS + ACTION + PERMISSION));
			Data data = new Data(byteString.substring(HEADER + ORIGIN_ID + DESTINATION_ID + LENGTH, HEADER + ORIGIN_ID + DESTINATION_ID + LENGTH + TYPE), status);
			frame.setData(data);
		} catch (StringIndexOutOfBoundsException | NumberFormatException e) {
			frame.setData(null);
		}
		return frame;
	}

	@Override
	public List<String> parseTx(Frame frame, List<String> byteList) {
		byteList.add(frame.getData().toString());
		return byteList;
	}

}
