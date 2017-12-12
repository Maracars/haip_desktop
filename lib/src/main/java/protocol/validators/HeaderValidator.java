package protocol.validators;

import helpers.Helpers;
import models.Frame;
import protocol.ProtocolProperties.PacketType;

import static protocol.ProtocolProperties.START_FRAME_VALUE;

public class HeaderValidator implements Validator {

	@Override
	public boolean validate(Frame frame) {
		if (!frame.getHeader().getStartFrame().equals(START_FRAME_VALUE)) {
			return false;
		}
		return Helpers.isInEnums(PacketType.class, frame.getHeader().getPacketType());

	}

}
