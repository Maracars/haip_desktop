package protocol.parsers;

import models.Frame;

import java.util.List;

import static protocol.ProtocolProperties.*;

public class ChecksumParser implements Parser {

	@Override
	public Frame parseRx(Frame frame, String byteString) {
		try {
			Integer len = Integer.parseInt(frame.getLength(), 2);
			String checksum = byteString.substring(HEADER + ORIGIN_ID + DESTINATION_ID + LENGTH + len,
					HEADER + ORIGIN_ID + DESTINATION_ID + LENGTH + len + CHECKSUM);

			frame.setChecksum(checksum);
		} catch (StringIndexOutOfBoundsException | NumberFormatException e) {
			frame.setChecksum(null);
		}
		return frame;
	}

	@Override
	public List<String> parseTx(Frame frame, List<String> byteList) {
		byteList.add(frame.getChecksum());
		return byteList;
	}

}
