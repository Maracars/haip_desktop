package protocol.parsers;

import models.Frame;

import java.util.List;

import static protocol.ProtocolProperties.*;

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
		byteList.add(Byte.parseByte(frame.getChecksum(), 2));
		return byteList;
	}

}
