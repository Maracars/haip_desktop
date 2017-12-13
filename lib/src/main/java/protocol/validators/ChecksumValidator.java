package protocol.validators;

import static protocol.ProtocolProperties.DESTINATION_ID;
import static protocol.ProtocolProperties.HEADER;
import static protocol.ProtocolProperties.LENGTH;
import static protocol.ProtocolProperties.ORIGIN_ID;

import helpers.CRC8;
import models.Frame;

public class ChecksumValidator implements Validator {

	@Override
	public boolean validate(Frame frame) {
		if(!CRC8.toCRC8(frame.toString().substring(0 ,
				HEADER + ORIGIN_ID + DESTINATION_ID + LENGTH + Integer.parseInt(frame.getLength(),
						2)*8)).equals(frame.getChecksum()))
			return false;
		return true;
	}

}
