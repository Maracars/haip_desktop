package protocol.validators;

import models.Frame;

import static protocol.ProtocolProperties.MASTER_ID;

public class DestinationValidator implements Validator{

	@Override
	public boolean validate(Frame frame) {
		//Check if both origin and destination are not equal
		if (frame.getOriginId().equals(frame.getDestinationId()))
			return false;
		//Check if origin or destination is from master
		if(!frame.getOriginId().equals(MASTER_ID) && !frame.getDestinationId().equals(MASTER_ID))
			return false;
		return true;
	}

}
