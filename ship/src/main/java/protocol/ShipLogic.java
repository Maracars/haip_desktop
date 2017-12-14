package protocol;

import static protocol.ProtocolProperties.MASTER_ID;

import java.util.Observable;
import java.util.Observer;

import helpers.Helpers;
import models.Frame;
import models.Ship;
import protocol.ProtocolProperties.ActionType;
import protocol.ProtocolProperties.DataType;
import protocol.ProtocolProperties.PacketType;
import protocol.ProtocolProperties.PermissionType;
import protocol.ProtocolProperties.StatusType;
import serial.Serial;

public class ShipLogic implements Observer{
	
	Serial serial;
	Ship ship;
	
	public ShipLogic(Serial serial, Ship ship) {
		this.serial = serial;
		this.ship = ship;
	}

	@Override
	public void update(Observable o, Object arg) {
		Frame frame = (Frame) arg;
		PacketType pt = PacketType.getName(frame.getHeader().getPacketType());
		Frame sendFrame = null;
		switch(pt) {
		case DISCOVERY:
			sendFrame = FrameCreator.createAck(ship.getId(), MASTER_ID);
			if(serial == null) {
				System.out.println("Ship number " + Integer.parseInt(ship.getId(), 2) + " trying to connect");
				System.out.println("Ship number " + Integer.parseInt(ship.getId(), 2) + " sends ACK: " + frame.toString());
			}else {
				replyController(sendFrame);
			}
			break;
		case DATA:
			// Here we have to check also the response of the controller
			checkShipMovement(frame);
			break;
		case TOKEN:
			// Here we have to check (with the interface) what wants to do the ship, if it wants to send only status or request an action
			System.out.println("Controller gives permission to talk to ship number " + Integer.parseInt(ship.getId(), 2));
			sendFrame = checkToken(frame);
			if(serial != null) {
				replyController(sendFrame);
			}
			break;
		default:
			break;
		}
		
		
	}
	
	private Frame checkToken(Frame frame) {
		Frame sendFrame = null;
		if(ship.getStatus().getAction().equals(ActionType.IDLE.toString())) {
			sendFrame = FrameCreator.createStatus(ship.getId(), ProtocolProperties.MASTER_ID, ship.getStatus());
			System.out.println("Ship number " + Integer.parseInt(ship.getId()) + " sends STATUS: " + sendFrame.toString());
		}else if (ship.getStatus().getAction().equals(ActionType.ENTER.toString()) || ship.getStatus().getAction().equals(ActionType.LEAVE.toString())) {
			sendFrame = FrameCreator.createRequest(ship.getId(), ProtocolProperties.MASTER_ID, ship.getStatus());
			System.out.println("Ship number " + Integer.parseInt(ship.getId(), 2) + " sends REQUEST: " + sendFrame.toString());
		}
		
		return sendFrame;
		
	}

	public void replyController(Frame frame) {
		Helpers.sendParsedFrame(frame, serial);
	}
	
	public void checkShipMovement(Frame frame) {
		if(frame.getData().getType().equals(DataType.RESPONSE.toString()) && frame.getData().getStatus().getPermission().equals(PermissionType.ALLOW.toString())) {
			System.out.println("Ship number " + Integer.parseInt(ship.getId(), 2) + " has permission to perform the operation: " + ActionType.getName(ship.getStatus().getAction()).name());
			if(frame.getData().getStatus().getAction().equals(ActionType.ENTER.toString())) {
				System.out.println("Parking assigned: "+frame.getData().getParking());
			}
			System.out.println("Ship changes to new status, STATUS: "+StatusType.getName(frame.getData().getStatus().getStatus()) + 
					", ACTION: "+ActionType.getName(frame.getData().getStatus().getAction()).name() + 
					" PERMISSION: "+PermissionType.getName(frame.getData().getStatus().getPermission()).name());
			ship.setStatus(frame.getData().getStatus());
		}
		if (frame.getData().getType().equals(DataType.RESPONSE.toString()) && frame.getData().getStatus().getPermission().equals(PermissionType.DENY.toString())) {
			System.out.println("Ship number " + Integer.parseInt(ship.getId(), 2) + " has NOT permission to perform the operation: " + ActionType.getName(ship.getStatus().getAction()).name());
			System.out.println("Ship changes to new status, STATUS: "+StatusType.getName(frame.getData().getStatus().getStatus()) + 
					", ACTION: "+ActionType.getName(frame.getData().getStatus().getAction()).name() + 
					" PERMISSION: "+PermissionType.getName(frame.getData().getStatus().getPermission()).name());
			ship.setStatus(frame.getData().getStatus());
		}
		if(frame.getData().getType().equals(DataType.STATUS.toString()) && frame.getData().getStatus().getPermission().equals(PermissionType.ALLOW.toString())) {
			System.out.println("Ship number " + Integer.parseInt(ship.getId()) + " has started to perform the operation: " + ActionType.getName(ship.getStatus().getAction()).name() + " and controller is asking to change its status");
		}
	}

}
