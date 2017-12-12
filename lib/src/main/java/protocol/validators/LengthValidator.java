package protocol.validators;

import models.Frame;

public class LengthValidator implements Validator{

	@Override
	public boolean validate(Frame frame) {
		return true;
	}

}
