package protocol.parsers;

import static protocol.ProtocolProperties.CHECKSUM;
import static protocol.ProtocolProperties.DESTINATION_ID;
import static protocol.ProtocolProperties.HEADER;
import static protocol.ProtocolProperties.ORIGIN_ID;

import java.util.List;

import helpers.Helpers;
import models.Frame;

public class ChecksumParser implements Parser {

	@Override
	public Frame parseRx(Frame frame, String byteString) {
		try {
			Integer length = (Integer.parseInt(frame.getHeader().getLength(), 2) * 8);
			String checksum = byteString.substring(HEADER + ORIGIN_ID + DESTINATION_ID + length,
					HEADER + ORIGIN_ID + DESTINATION_ID + length + CHECKSUM);

			frame.setChecksum(checksum);
		} catch (StringIndexOutOfBoundsException | NumberFormatException e) {
			frame.setChecksum(null);
		}
		return frame;
	}

	@Override
	public List<Byte> parseTx(Frame frame, List<Byte> byteList) {
		byteList.add(Helpers.getUnsignedByte(frame.getChecksum()));
		return byteList;
	}

}
