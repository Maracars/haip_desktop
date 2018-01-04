package protocol;

import static protocol.ProtocolProperties.MASTER_ID;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import helpers.Helpers;
import models.Frame;
import models.Ship;
import models.Status;
import protocol.ProtocolProperties.ActionType;
import protocol.ProtocolProperties.DataType;
import protocol.ProtocolProperties.PacketType;
import protocol.ProtocolProperties.PermissionType;
import protocol.ProtocolProperties.StatusType;
import serial.Serial;

public class ShipLogic extends Observable implements Observer{

	Serial serial;
	Ship ship;
	boolean simulation;
	ArrayList<Ship> simulationShips;
	List<String> actionList;

	public ShipLogic(Serial serial, Ship ship) {
		this.serial = serial;
		this.ship = ship;
		simulation = false;
		actionList = new ArrayList<String>();
		simulationShips = new ArrayList<>();
	}

	@Override
	public void update(Observable o, Object arg) {
		Frame frame = (Frame) arg;
		PacketType pt = PacketType.getName(frame.getHeader().getPacketType());
		Frame sendFrame = null;
		System.out.println("Received frame "+frame.toString());
		switch(pt) {
		case DISCOVERY:
			sendFrame = FrameCreator.createAck(ship.getId(), MASTER_ID);
			if(serial == null) {
				System.out.println("Ship number " + Integer.parseInt(ship.getId(), 2) + " trying to connect");
				System.out.println("Ship number " + Integer.parseInt(ship.getId(), 2) + " sends ACK: " + frame.toString());
			}else {
				System.out.println("Ship number " + Integer.parseInt(ship.getId(),2) + " connected");
				if(serial.isConnected())
					replyController(sendFrame);
			}
			break;
		case DATA:
			checkShipMovement(frame, ship);
			notifyPanel();
			break;
		case TOKEN:
			System.out.println("Controller gives permission to talk to ship number " + Integer.parseInt(ship.getId(), 2));
			sendFrame = checkToken(frame, ship);
			if(serial != null) {
				replyController(sendFrame);

			}
			break;
		default:
			break;
		}


	}

	public Frame checkToken(Frame frame, Ship ship) {
		Frame sendFrame = null;
		if(ship.getActionList().size() > 0) {
			System.out.println("Nueva accion");
			System.out.println("Numero de acciones "+ship.getActionList().size());
			if(ship.getActionList().get(0).getAction().equals(ActionType.IDLE.toString())) {
				sendFrame = FrameCreator.createStatus(ship.getId(), ProtocolProperties.MASTER_ID, ship.getStatus());
				System.out.println("Ship number " + Integer.parseInt(ship.getId()) + " sends STATUS: " + sendFrame.toString());
			}else if (ship.getActionList().get(0).getAction().equals(ActionType.ENTER.toString()) || ship.getActionList().get(0).getAction().equals(ActionType.LEAVE.toString())) {
				sendFrame = FrameCreator.createRequest(ship.getId(), ProtocolProperties.MASTER_ID, ship.getStatus());
				System.out.println("Ship number " + Integer.parseInt(ship.getId(), 2) + " sends REQUEST: " + sendFrame.toString());
			}
		}else{
			sendFrame = FrameCreator.createStatus(ship.getId(), ProtocolProperties.MASTER_ID, ship.getStatus());
			System.out.println("Ship number " + Integer.parseInt(ship.getId()) + " sends STATUS: " + sendFrame.toString());
		}
		return sendFrame;

	}

	public void replyController(Frame frame) {
		Helpers.sendParsedFrame(frame, serial);
	}

	public void checkShipMovement(Frame frame, Ship ship) {
		if(frame.getData().getType().equals(DataType.RESPONSE.toString()) && frame.getData().getStatus().getPermission().equals(PermissionType.ALLOW.toString())) {
			System.out.println("Ship number " + Integer.parseInt(ship.getId(), 2) + " has permission to perform the operation: " + ActionType.getName(ship.getStatus().getAction()).name());
			
			if(frame.getData().getStatus().getStatus().equals(ship.getActionList().get(0).getStatus())) {
				System.out.println("Operation finish for ship number " + Integer.parseInt(ship.getId(), 2) + " status: " + StatusType.getName(ship.getStatus().getStatus()).name());
				System.out.println("Setting ship status to IDLE and now can ask for a new action");
				if(simulation) {
					Status newStatus = DecisionMaker.getRandomAction(StatusType.getName(frame.getData().getStatus().getStatus()));
					ship.setStatus(new Status(frame.getData().getStatus().getStatus(),newStatus.getAction(), PermissionType.ASK.toString()));
					ship.setActionList(new ArrayList<>());
					ship.addAction(newStatus);
				}else{
					ship.setStatus(new Status(frame.getData().getStatus().getStatus(), ActionType.IDLE.toString(), PermissionType.ASK.toString()));
					ship.setActionList(new ArrayList<Status>());
				}

			}else if (frame.getData().getStatus().getStatus().equals(StatusType.TRANSIT.toString())) {
				System.out.println("Performing the operation for ship number "+Integer.parseInt(ship.getId(), 2));
				ship.setStatus(frame.getData().getStatus());
			}else{
				System.out.println("Ship changes to new status, STATUS: "+StatusType.getName(frame.getData().getStatus().getStatus()) + 
						", ACTION: "+ActionType.getName(frame.getData().getStatus().getAction()).name() + 
						" PERMISSION: "+PermissionType.getName(frame.getData().getStatus().getPermission()).name());
				if(frame.getData().getStatus().getAction().equals(ActionType.ENTER.toString())) {
					System.out.println("Parking assigned: "+frame.getData().getParking());
					ship.setParking(frame.getData().getParking());
				}
				ship.setStatus(frame.getData().getStatus());
			}
		}
		if (frame.getData().getType().equals(DataType.RESPONSE.toString()) && frame.getData().getStatus().getPermission().equals(PermissionType.DENY.toString())) {
			System.out.println("Ship number " + Integer.parseInt(ship.getId(), 2) + " has NOT permission to perform the operation: " + ActionType.getName(ship.getStatus().getAction()).name());
			System.out.println("Ship changes to new status, STATUS: "+StatusType.getName(frame.getData().getStatus().getStatus()) + 
					", ACTION: "+ActionType.getName(frame.getData().getStatus().getAction()).name() + 
					" PERMISSION: "+PermissionType.getName(frame.getData().getStatus().getPermission()).name());
			ship.setStatus(frame.getData().getStatus());

		}
	}

	private void notifyPanel() {
		setChanged();
		notifyObservers();
	}

	public Serial getSerial() {
		return serial;
	}

	public void setSimulationStarted() {
		simulation = true;
		
	}

}
