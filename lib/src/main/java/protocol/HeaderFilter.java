package protocol;

import models.Frame;
import models.Header;

import java.util.ArrayList;
import java.util.List;

import static protocol.ProtocolProperties.*;

public class HeaderFilter implements FrameFilter {

	List<String> packetTypeValues;

	public HeaderFilter() {
		packetTypeValues = new ArrayList<>();
		initializePacketTypes();
	}

	private void initializePacketTypes() {
		packetTypeValues.add("00");
		packetTypeValues.add("01");
		packetTypeValues.add("10");
	}

	@Override
	public Frame parseRx(Frame frame, String byteString) {
		Header header = new Header();
		// TODO Here instead of substrings we should create our own funct. e.g. getFramePart(byteString, START_FRAME)
		try {
			header.setStartFrame(byteString.substring(0, START_FRAME));
			header.setPacketType(byteString.substring(START_FRAME, START_FRAME + PACKET_TYPE));
			header.setCounter(byteString.substring(START_FRAME + PACKET_TYPE, START_FRAME + PACKET_TYPE + COUNTER));
			frame.setHeader(header);
		} catch (StringIndexOutOfBoundsException e) {
			frame.setHeader(null);
		}
		return frame;
	}

	@Override
	public boolean filter(Frame frame) {
		if (!packetTypeValues.contains(frame.getHeader().getPacketType()))
			return false;
		return true;
	}

	@Override
	public List<String> parseTx(Frame frame, List<String> byteList) {
		byteList.add(frame.getHeader().toString());
		return byteList;
	}

}
