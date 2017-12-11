package protocol;

import static protocol.ProtocolProperties.MASTER_ID;

import java.util.List;

import models.Frame;
import protocol.ProtocolProperties.PacketType;

public class OriginFilter implements FrameFilter {


	@Override
	public Frame parseRx(Frame frame, String byteString) {
		frame.setOriginId(byteString);
		return frame;
	}

	@Override
	public boolean filter(Frame frame) {
		//Check if the packet is a discovery packet and is sent by master
		if (frame.getHeader().getPacketType().equals(PacketType.DISCOVERY) &&
				!frame.getOriginId().equals(MASTER_ID))
			return false;
		return true;
	}

	@Override
	public List<String> parseTx(Frame frame, List<String> byteList) {
		byteList.add(frame.getOriginId().toString());
		return byteList;
	}

}
