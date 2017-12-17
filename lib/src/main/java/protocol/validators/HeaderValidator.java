package protocol.validators;

import helpers.Helpers;
import models.Frame;
import protocol.ProtocolProperties.PacketType;

public class HeaderValidator implements Validator {

	@Override
	public boolean validate(Frame frame) {
		return Helpers.isInEnums(PacketType.class, frame.getHeader().getPacketType());
	}
}
