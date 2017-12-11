package protocol;

import static protocol.ProtocolProperties.MASTER_ID;

import java.util.List;

import models.Frame;
import protocol.ProtocolProperties.PacketType;
import static protocol.ProtocolProperties.HEADER;
import static protocol.ProtocolProperties.ORIGIN_ID;

public class OriginValidator implements Validator {

	@Override
	public boolean validate(Frame frame) {
		//Check if the packet is a discovery packet and is sent by master
		if (frame.getHeader().getPacketType().equals(PacketType.DISCOVERY) &&
				!frame.getOriginId().equals(MASTER_ID))
			return false;
		return true;
	}

}
