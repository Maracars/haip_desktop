package protocol;

import java.util.Arrays;

import models.Frame;
import protocol.ProtocolProperties.ActionType;
import protocol.ProtocolProperties.DataType;
import protocol.ProtocolProperties.PermissionType;
import protocol.ProtocolProperties.StatusType;

public class DataValidator implements Validator{
	
	

	@Override
	public boolean validate(Frame frame) {
		//Check if length and real data length are equal
		if(Integer.parseInt(frame.getLength(), 2) != (frame.getData().toString().length()/8))
			return false;
		//Check if data type is valid
		if(!Arrays.asList(DataType.values()).contains(frame.getData().getType()))
			return false;
		//Check if action type is valid
		if(!Arrays.asList(ActionType.values()).contains(frame.getData().getStatus().getAction()))
			return false;
		//Check if status type is valid
		if(!Arrays.asList(StatusType.values()).contains(frame.getData().getStatus().getStatus()))
			return false;
		//Check if permission is valid
		if(!Arrays.asList(PermissionType.values()).contains(frame.getData().getStatus().getPermission()))
			return false;
		return true;
		
	}


}
