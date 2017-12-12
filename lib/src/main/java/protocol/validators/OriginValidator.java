package protocol.validators;

import static protocol.ProtocolProperties.MASTER_ID;

import models.Frame;
import protocol.ProtocolProperties.PacketType;

public class OriginValidator implements Validator {

	@Override
	public boolean validate(Frame frame) {
		//Check if the packet is a discovery packet and is sent by master
		if (frame.getHeader().getPacketType().equals(PacketType.DISCOVERY) &&
				!frame.getOriginId().equals(MASTER_ID))
			return false;
		//Check if the packet is a token packet and is sent by master
		if(frame.getHeader().getPacketType().equals(PacketType.TOKEN) && 
				!frame.getOriginId().equals(MASTER_ID))
			return false;
		return true;
	}

}
