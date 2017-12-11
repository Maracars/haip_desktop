package protocol;

import java.util.ArrayList;
import java.util.List;

import models.Frame;

public class HeaderValidator implements Validator {

	List<String> packetTypeValues;

	public HeaderValidator() {
		packetTypeValues = new ArrayList<>();
		initializePacketTypes();
	}

	private void initializePacketTypes() {
		packetTypeValues.add("00");
		packetTypeValues.add("01");
		packetTypeValues.add("10");
	}

	@Override
	public boolean validate(Frame frame) {
		if (!packetTypeValues.contains(frame.getHeader().getPacketType()))
			return false;
		return true;
	}

}
