package protocol;

import models.Frame;

public class DestinationValidator implements Validator{

	@Override
	public boolean validate(Frame frame) {
		//Check if both origin and destination are not equal
		if (frame.getOriginId().equals(frame.getDestinationId()))
			return false;
		return true;
	}

}
