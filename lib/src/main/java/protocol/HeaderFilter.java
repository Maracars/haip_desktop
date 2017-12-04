package protocol;

import java.util.ArrayList;
import java.util.List;

import models.Frame;
import models.Header;

import static protocol.ProtocolProperties.COUNTER;
import static protocol.ProtocolProperties.PACKET_TYPE;
import static protocol.ProtocolProperties.START_FRAME;

public class HeaderFilter implements FrameFilter{
	
	List<String> packetTypeValues;
	
	public HeaderFilter() {
		packetTypeValues = new ArrayList<String>();
		initializePacketTypes();
	}
	

	private void initializePacketTypes() {
		for (int i = 0; i < PACKET_TYPE; i++) {
			String packetType = Integer.toBinaryString(i);
			packetTypeValues.add(packetType);
		}
	}


	@Override
	public boolean filter(Frame frame) {
		if(!packetTypeValues.contains(frame.getHeader().getPacketType()))
			return false;
		return true;
	}


	@Override
	public Frame parseRx(Frame frame, String byteString) {
		Header header = new Header();
		header.setStartFrame(byteString.substring(0, START_FRAME - 1));
		header.setPacketType(byteString.substring(START_FRAME, PACKET_TYPE - 1));
		header.setCounter(byteString.substring(PACKET_TYPE, COUNTER - 1));
		frame.setHeader(header);
		return frame;
	}

}
