package protocol.validators;

import helpers.Helpers;
import models.Data;
import models.Frame;
import models.Status;
import protocol.ProtocolProperties.ActionType;
import protocol.ProtocolProperties.DataType;
import protocol.ProtocolProperties.PermissionType;
import protocol.ProtocolProperties.StatusType;

public class DataValidator implements Validator {


	@Override
	public boolean validate(Frame frame) {
		//Check if length and real data length are equal
		Data data = frame.getData();
		Status status = data.getStatus();
		// TODO Here we should make a list of classes and comparable things
		if (Integer.parseInt(frame.getLength(), 2) != (data.toString().length() / 8))
			return false;

		if (!Helpers.isInEnums(DataType.class, data.getType()))
			return false;

		if (!Helpers.isInEnums(ActionType.class, status.getAction()))
			return false;

		if (Helpers.isInEnums(StatusType.class, status.getStatus()))
			return false;

		if (Helpers.isInEnums(PermissionType.class, status.getPermission()))
			return false;

		return true;

	}


}
