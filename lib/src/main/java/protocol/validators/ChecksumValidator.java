package protocol.validators;

import helpers.CRC8;
import models.Frame;

import static protocol.ProtocolProperties.*;

public class ChecksumValidator implements Validator {

	@Override
	public boolean validate(Frame frame) {
		Integer length = (Integer.parseInt(frame.getHeader().getLength(), 2) * 8);
		String expected = null;
		try {
			expected = CRC8.toCRC8(frame.toString().substring(0, HEADER + ORIGIN_ID + DESTINATION_ID + length));
		}catch(StringIndexOutOfBoundsException e) {
			return false;
		}
		return expected.equals(frame.getChecksum());
	}
}
