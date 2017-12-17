package protocol.validators;

import helpers.CRC8;
import models.Frame;

import static protocol.ProtocolProperties.*;

public class ChecksumValidator implements Validator {

	@Override
	public boolean validate(Frame frame) {
		Integer length = (Integer.parseInt(frame.getHeader().getLength(), 2) * 8);
		String expected = CRC8.toCRC8(frame.toString().substring(0, HEADER + ORIGIN_ID + DESTINATION_ID + length));

		return expected.equals(frame.getChecksum());
	}
}
