package protocol.parsers;

import static protocol.ProtocolProperties.CHECKSUM;
import static protocol.ProtocolProperties.DESTINATION_ID;
import static protocol.ProtocolProperties.HEADER;
import static protocol.ProtocolProperties.LENGTH;
import static protocol.ProtocolProperties.ORIGIN_ID;

import java.util.List;

import models.Frame;

public class ChecksumParser implements Parser{

	@Override
	public Frame parseRx(Frame frame, String byteString) {
		try {
			frame.setChecksum(byteString.substring(HEADER + ORIGIN_ID + DESTINATION_ID + LENGTH + Integer.parseInt(frame.getLength(),2)*8 , HEADER + ORIGIN_ID + DESTINATION_ID + LENGTH + Integer.parseInt(frame.getLength(),2)*8 + CHECKSUM));
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
