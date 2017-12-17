package protocol.parsers;

import static protocol.ProtocolProperties.DESTINATION_ID;
import static protocol.ProtocolProperties.HEADER;
import static protocol.ProtocolProperties.LENGTH;
import static protocol.ProtocolProperties.ORIGIN_ID;
import static protocol.ProtocolProperties.STATUS;
import static protocol.ProtocolProperties.TYPE;
import static protocol.ProtocolProperties.ACTION;
import static protocol.ProtocolProperties.PERMISSION;
import static protocol.ProtocolProperties.PARKING;

import java.util.List;

import models.Data;
import models.Frame;
import models.Status;
import protocol.ProtocolProperties.DataType;
import protocol.ProtocolProperties.PacketType;
import protocol.ProtocolProperties.PermissionType;
import protocol.ProtocolProperties.StatusType;

public class DataParser implements Parser{

	// Type, Status, Action, Permission (2 bits each)
	@Override
	public Frame parseRx(Frame frame, String byteString) {
		if (Integer.parseInt(frame.getHeader().getLength(), 2) > 0) {
			frame = parseDataFirstByte(frame, byteString);
			frame = parseDataSecondByte(frame, byteString);

		} else {
			frame.setData(null);
		}
		return frame;
	}


	private Frame parseDataSecondByte(Frame frame, String byteString) {
		if (frame.getHeader().getPacketType().equals(PacketType.DATA.toString()) &&
				frame.getData().getType().equals(DataType.RESPONSE.toString()) &&
				frame.getData().getStatus().getStatus().equals(StatusType.SEA.toString()) &&
				frame.getData().getStatus().getPermission().equals(PermissionType.ALLOW.toString())) {
			try {
				frame.getData().setParking(byteString.substring(HEADER + ORIGIN_ID + DESTINATION_ID + TYPE + STATUS + ACTION + PERMISSION,
						HEADER + ORIGIN_ID + DESTINATION_ID + TYPE + STATUS + ACTION + PERMISSION + PARKING));
			} catch (StringIndexOutOfBoundsException e) {
				frame.getData().setParking(null);
			}
		} else {
			frame.getData().setParking(null);
		}
		return frame;
	}


	public Frame parseDataFirstByte(Frame frame, String byteString) {
		try {
			String type = byteString.substring(HEADER + ORIGIN_ID + DESTINATION_ID,
					HEADER + ORIGIN_ID + DESTINATION_ID + TYPE);
			Status status = new Status(
					byteString.substring(HEADER + ORIGIN_ID + DESTINATION_ID + TYPE,
							HEADER + ORIGIN_ID + DESTINATION_ID + TYPE + STATUS),
					byteString.substring(HEADER + ORIGIN_ID + DESTINATION_ID + TYPE + STATUS,
							HEADER + ORIGIN_ID + DESTINATION_ID + TYPE + STATUS + ACTION),
					byteString.substring(HEADER + ORIGIN_ID + DESTINATION_ID + TYPE + STATUS + ACTION,
							HEADER + ORIGIN_ID + DESTINATION_ID + TYPE + STATUS + ACTION + PERMISSION));

			Data data = new Data(type, status);
			frame.setData(data);
		}
		catch (StringIndexOutOfBoundsException | NumberFormatException e) {
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
