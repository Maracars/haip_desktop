package protocol;

import static protocol.ProtocolProperties.START_FRAME_VALUE;

import java.util.Arrays;

import models.Frame;
import protocol.ProtocolProperties.PacketType;

public class HeaderValidator implements Validator {

	@Override
	public boolean validate(Frame frame) {
		if(!frame.getHeader().getStartFrame().equals(START_FRAME_VALUE))
			return false;
		if(!Arrays.asList(PacketType.values()).contains(frame.getHeader().getPacketType()))
			return false;
		return true;
	}

}
