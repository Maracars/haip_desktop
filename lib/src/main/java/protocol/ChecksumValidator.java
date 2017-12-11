package protocol;

import models.Frame;

public class ChecksumValidator implements Validator{

	@Override
	public boolean validate(Frame frame) {
		return true;
	}

}
