package protocol.validators;

import helpers.CRC8;
import models.Frame;

import static protocol.ProtocolProperties.*;

public class ChecksumValidator implements Validator {

	@Override
	public boolean validate(Frame frame) {

		Integer len = Integer.parseInt(frame.getLength(), 2);
		String expected = CRC8.toCRC8(frame.toString().substring(0, HEADER + ORIGIN_ID + DESTINATION_ID + LENGTH + len));


		return expected.equals(frame.getChecksum());
	}

}
