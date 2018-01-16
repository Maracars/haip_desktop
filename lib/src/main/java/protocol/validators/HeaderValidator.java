package protocol.validators;

import helpers.Helpers;
import models.Frame;
import protocol.ProtocolProperties.PacketType;

public class HeaderValidator implements Validator {

	@Override
	public boolean validate(Frame frame) {
		if(frame.getHeader().getPacketType().equals(PacketType.DISCOVERY.toString()) && 
				Integer.parseInt(frame.getHeader().getLength()) != 1)
			return false;
		return Helpers.isInEnums(PacketType.class, frame.getHeader().getPacketType());
	}
}
