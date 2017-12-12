package protocol;

import static protocol.ProtocolProperties.MASTER_ID;

import java.util.Observable;
import java.util.Observer;

import helpers.Helpers;
import models.Frame;
import models.Ship;
import protocol.ProtocolProperties.PacketType;
import protocol.ProtocolProperties.PermissionType;
import serial.Serial;

public class ShipLogic implements Observer{
	
	Serial serial;
	Ship ship;
	
	public ShipLogic(Serial serial) {
		this.serial = serial;
	}

	@Override
	public void update(Observable o, Object arg) {
		Frame frame = (Frame) arg;
		PacketType pt = PacketType.valueOf(frame.getHeader().getPacketType());
		Frame sendFrame = null;
		switch(pt) {
		case DISCOVERY:
			sendFrame = FrameCreator.createAck(ship.getId(), MASTER_ID);
			replyController(sendFrame);
			System.out.println("Ship number " + ship.getId() + " trying to connect");
			break;
		case DATA:
			//Here we have to check also the response of the controller
			checkShipMovement(sendFrame);
			break;
		case TOKEN:
			sendFrame = FrameCreator.createRequest(ship.getId(), MASTER_ID, ship.getStatus());
			replyController(sendFrame);
			System.out.println("Controller gives permission to talk to ship number " + ship.getId());
			break;
		default:
			break;
		}
		
		
	}
	
	public void replyController(Frame frame) {
		Helpers.sendParsedFrame(frame, serial);
	}
	
	public void checkShipMovement(Frame frame) {
		PermissionType permType = PermissionType.valueOf(frame.getData().getStatus().getPermission());
		if(permType.equals(PermissionType.ALLOW)) {
			//It has permission
			ship.setStatus(frame.getData().getStatus());
		}else if(permType.equals(PermissionType.DENY)) {
			//Not permission
		}
	}

}
