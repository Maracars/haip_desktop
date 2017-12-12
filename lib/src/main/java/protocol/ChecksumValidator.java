package protocol;

import helpers.CRC8;
import models.Frame;

public class ChecksumValidator implements Validator {

	@Override
	public boolean validate(Frame frame) {
		if(!CRC8.toCRC8(frame.getData().toString()).equals(frame.getChecksum()))
			return false;
		return true;
	}

}
