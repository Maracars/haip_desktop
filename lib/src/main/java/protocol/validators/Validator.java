package protocol.validators;

import models.Frame;

public interface Validator {
	boolean validate(Frame frame);
}
