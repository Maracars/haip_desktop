package protocol.parsers;

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

	// Type, Status, Action, Permission (2 bits each)
	@Override
	public Frame parseRx(Frame frame, String byteString) {
		try {
			String type = byteString.substring(HEADER + ORIGIN_ID + DESTINATION_ID + LENGTH,
					HEADER + ORIGIN_ID + DESTINATION_ID + LENGTH + TYPE);
			Status status = new Status(
					byteString.substring(HEADER + ORIGIN_ID + DESTINATION_ID + LENGTH + TYPE,
							HEADER + ORIGIN_ID + DESTINATION_ID + LENGTH + TYPE + STATUS),
					byteString.substring(HEADER + ORIGIN_ID + DESTINATION_ID + LENGTH + TYPE + STATUS,
							HEADER + ORIGIN_ID + DESTINATION_ID + LENGTH + TYPE + STATUS + ACTION),
					byteString.substring(HEADER + ORIGIN_ID + DESTINATION_ID + LENGTH + TYPE + STATUS + ACTION,
							HEADER + ORIGIN_ID + DESTINATION_ID + LENGTH + TYPE + STATUS + ACTION + PERMISSION));

			Data data = new Data(type, status);

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
