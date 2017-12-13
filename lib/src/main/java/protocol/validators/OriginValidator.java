package protocol.validators;

import models.Frame;
import protocol.ProtocolProperties.PacketType;

import static protocol.ProtocolProperties.MASTER_ID;

public class OriginValidator implements Validator {

	@Override
	public boolean validate(Frame frame) {
		//Check if the packet is a discovery packet and is sent by master
		if (frame.getHeader().getPacketType().equals(PacketType.DISCOVERY.toString()) &&
				!frame.getOriginId().equals(MASTER_ID))
			return false;
		//Check if the packet is a token packet and is sent by master
		if (frame.getHeader().getPacketType().equals(PacketType.TOKEN.toString()) &&
				!frame.getOriginId().equals(MASTER_ID)) {
			return false;
		}
		return true;
	}

}
