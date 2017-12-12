package protocol;

import models.Frame;

import static protocol.ProtocolProperties.START_FRAME_VALUE;

public class HeaderValidator implements Validator {

	@Override
	public boolean validate(Frame frame) {
		if (!frame.getHeader().getStartFrame().equals(START_FRAME_VALUE)) {
			return false;
		}

		/*List<String> jaja = Stream.of(PacketType.values()).map(PacketType::toString).filter(s -> s == frame.getHeader().getPacketType())
				.collect(Collectors.toList());*/

		return true;
	}

}
