package protocol.validators;

import helpers.Helpers;
import models.Data;
import models.Frame;
import models.Status;
import protocol.ProtocolProperties.ActionType;
import protocol.ProtocolProperties.DataType;
import protocol.ProtocolProperties.PermissionType;
import protocol.ProtocolProperties.StatusType;

import java.util.HashMap;
import java.util.Map;

public class DataValidator implements Validator {

	@SuppressWarnings("unchecked")
	@Override
	public boolean validate(Frame frame) {
		//Check if length and real data length are equal
		Data data = frame.getData();
		Status status = data.getStatus();

		Map<Class, String> validator = new HashMap<Class, String>() {{
			put(DataType.class, data.getType());
			put(ActionType.class, status.getAction());
			put(StatusType.class, status.getStatus());
			put(PermissionType.class, status.getPermission());
		}};
		// TODO Here we should make a list of classes and comparable things
		if (Integer.parseInt(frame.getLength(), 2) != (data.toString().length() / 8))
			return false;

		for (Map.Entry<Class, String> entry : validator.entrySet()) {

			if (!Helpers.isInEnums(entry.getKey(), entry.getValue())) {
				return false;
			}
		}

		return true;

	}


}
