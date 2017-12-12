package protocol;

import java.util.ArrayList;
import java.util.List;

import models.Frame;
import static protocol.ProtocolProperties.PacketType;
import static protocol.ProtocolProperties.START_FRAME_VALUE;

public class HeaderValidator implements Validator {

	List<String> packetTypeValues;

	public HeaderValidator() {
		packetTypeValues = new ArrayList<>();
		initializePacketTypes();
	}

	private void initializePacketTypes() {
		packetTypeValues.add(PacketType.DISCOVERY.toString());
		packetTypeValues.add(PacketType.DATA.toString());
		packetTypeValues.add(PacketType.ACK.toString());
		packetTypeValues.add(PacketType.TOKEN.toString());
	}

	@Override
	public boolean validate(Frame frame) {
		if(!frame.getHeader().getStartFrame().equals(START_FRAME_VALUE))
			return false;
		if (!packetTypeValues.contains(frame.getHeader().getPacketType()))
			return false;
		return true;
	}

}
