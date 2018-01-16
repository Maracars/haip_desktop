package protocol.parsers;

import models.Data;
import models.Frame;
import models.Status;

import java.util.List;

import helpers.Helpers;

import static protocol.ProtocolProperties.*;

public class DataParser implements Parser {

	// Type, Status, Action, Permission (2 bits each)
	@Override
	public Frame parseRx(Frame frame, String byteString) {
		if (Integer.parseInt(frame.getHeader().getLength(), 2) > 0) {
			if(frame.getHeader().getPacketType().equals(PacketType.DISCOVERY.toString())) {
				frame = parseDiscoveryData(frame, byteString);
			}else {
				frame = parseDataFirstByte(frame, byteString);
				frame = parseDataSecondByte(frame, byteString);
			}


		} else {
			frame.setData(null);
		}
		return frame;
	}


	private Frame parseDiscoveryData(Frame frame, String byteString) {
		try {
			String timeWindow =  byteString.substring(HEADER + ORIGIN_ID + DESTINATION_ID,
					HEADER + ORIGIN_ID + DESTINATION_ID + TIME_WINDOW);
			Data data = new Data(timeWindow);
			frame.setData(data);
		} catch (StringIndexOutOfBoundsException | NumberFormatException e) {
			frame.setData(null);
		}
		return frame;
	}


	private Frame parseDataSecondByte(Frame frame, String byteString) {
		if (Integer.parseInt(frame.getHeader().getLength(), 2) >= 2) {
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

	private Frame parseDataFirstByte(Frame frame, String byteString) {
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
		} catch (StringIndexOutOfBoundsException | NumberFormatException e) {
			frame.setData(null);
		}
		return frame;
	}

	@Override
	public List<Byte> parseTx(Frame frame, List<Byte> byteList) {
		if (frame.getData() != null & !frame.getData().getDataType().equals("")) {
			if(frame.getData().getTimeWindow() != null) {
				byteList.add(Helpers.getUnsignedByte(frame.getData().getTimeWindow()));
			}else{
				byteList.add(Helpers.getUnsignedByte(frame.getData().getDataType() + frame.getData().getStatus().toString()));
				if(frame.getData().getParking() != null) {
					byteList.add(Helpers.getUnsignedByte(frame.getData().getParking()));
				}
			}
		}

		return byteList;
	}

}
