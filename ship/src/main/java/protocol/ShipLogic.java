package protocol;

import static protocol.ProtocolProperties.MASTER_ID;
import static protocol.ProtocolProperties.TOKEN_TIMEOUT;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

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
import ui.log.LogListModel;

public class ShipLogic extends Observable implements Observer {
	private Serial serial;
	private Ship ship;
	private boolean simulation;

	public ShipLogic(Serial serial, Ship ship) {
		this.serial = serial;
		this.ship = ship;
		simulation = false;
	}

	@Override
	public void update(Observable o, Object arg) {
		Frame frame = (Frame) arg;
		PacketType pt = PacketType.getName(frame.getHeader().getPacketType());
		Frame sendFrame;
		switch (pt) {
		case DISCOVERY:
			System.out.println("Discoveryyy");
			if(ship.checkDiscovery()) {
				System.out.println("ACK");
				LogListModel.add("Ship number " + Integer.parseInt(ship.getId()) + " sends ACK to connect");
				sendFrame = checkDiscovery(frame, ship);
				replyController(sendFrame);
			}
			break;
		case DATA:
			System.out.println("DATA");
			checkShipMovement(frame, ship);
			notifyPanel();
			break;
		case TOKEN:
			System.out.println("TOKEN");
			LogListModel.add("Permission to talk: " + Integer.parseInt(ship.getId(), 2));
			sendFrame = checkToken(frame, ship);
			replyController(sendFrame);
			break;
		default:
			break;
		}
	}
	
	public Frame checkDiscovery(Frame frame, Ship ship) {
		int timeWindow = Integer.parseInt(frame.getData().getTimeWindow(), 2);
		Random interval = new Random();
		System.out.println("Time Window from controller: "+timeWindow);
		int sleep = interval.nextInt(timeWindow);
		System.out.println("Random interval: "+sleep);
		System.out.println("Waiting to the interval");
		waitForDiscoveryDelay(sleep);
		System.out.println("Wait finish");
		Frame sendFrame = FrameCreator.createAck(ship.getId(), MASTER_ID);
		ship.resetDiscoveryCounter();
		return sendFrame;
	}
	
	public void waitForDiscoveryDelay(long sleep) {
		long startingTime = System.currentTimeMillis()*1000;
		long elapsedTime = 0;
		do {
			elapsedTime = System.currentTimeMillis()*1000 - startingTime;
		} while (elapsedTime < sleep);
		
	}

	public Frame checkToken(Frame frame, Ship ship) {
		Frame sendFrame = null;

		if (simulation && ship.getStatus().getAction().equals(ActionType.IDLE.toString()) && ship.getIdleTime() >= 20) {
			Status newStatus = DecisionMaker.getRandomAction(StatusType.getName(ship.getStatus().getStatus()));
			ship.setStatus(new Status(ship.getStatus().getStatus(), newStatus.getAction(), PermissionType.ASK.toString()));
			ship.setActionList(new ArrayList<>());
			ship.addAction(newStatus);
		} else if (ship.getIdleTime() < 20 && simulation) {
			ship.addIdleTime(1);
		}

		if (ship.getActionList().size() > 0) {
			ship.addDiscoveryCounter();
			if (ship.getActionList().get(0).getAction().equals(ActionType.IDLE.toString())) {
				sendFrame = FrameCreator.createStatus(ship.getId(), ProtocolProperties.MASTER_ID, ship.getStatus());
				LogListModel.add("Ship number " + Integer.parseInt(ship.getId()) + " sends STATUS");
			} else if (ship.getActionList().get(0).getAction().equals(ActionType.ENTER.toString()) || ship.getActionList().get(0).getAction().equals(ActionType.LEAVE.toString())) {
				sendFrame = FrameCreator.createRequest(ship.getId(), ProtocolProperties.MASTER_ID, ship.getStatus());
				LogListModel.add("Ship number " + Integer.parseInt(ship.getId(), 2) + " sends REQUEST");
			}
		} else {
			sendFrame = FrameCreator.createStatus(ship.getId(), ProtocolProperties.MASTER_ID, ship.getStatus());
			LogListModel.add("Ship number " + Integer.parseInt(ship.getId()) + " sends STATUS");
		}
		return sendFrame;

	}

	public void replyController(Frame frame) {
		Helpers.sendParsedFrame(frame, serial);
	}

	public void checkShipMovement(Frame frame, Ship ship) {
		String dataType = frame.getData().getDataType();
		String permission = frame.getData().getStatus().getPermission();
		String frameStatus = frame.getData().getStatus().getStatus();
		String actionStatus = ship.getActionList().get(0).getStatus();
		String frameAction = frame.getData().getStatus().getAction();
		String framePermission = frame.getData().getStatus().getPermission();
		String shipAction = ActionType.getName(ship.getStatus().getAction()).name();
		int shipId = Integer.parseInt(ship.getId(), 2);

		if (dataType.equals(DataType.RESPONSE.toString()) && permission.equals(PermissionType.ALLOW.toString())) {
			LogListModel.add("Permission to ship: " + shipId + ", " + shipAction);
			if (frameStatus.equals(actionStatus)) {
				LogListModel.add("Operation finish ship: " + shipId);
				LogListModel.add("Ship " + shipId + " IDLE");
				if (simulation) {
					Status newStatus = DecisionMaker.getRandomAction(StatusType.getName(frame.getData().getStatus().getStatus()));
					ship.setStatus(new Status(frame.getData().getStatus().getStatus(), newStatus.getAction(), PermissionType.ASK.toString()));
					ship.setActionList(new ArrayList<>());
					ship.addAction(newStatus);
				} else {
					ship.setStatus(new Status(frame.getData().getStatus().getStatus(), ActionType.IDLE.toString(), PermissionType.ASK.toString()));
					ship.setActionList(new ArrayList<>());
				}

			} else if (frameStatus.equals(StatusType.TRANSIT.toString())) {
				LogListModel.add("Performing operation ship: " + shipId);
				ship.setStatus(frame.getData().getStatus());
			} else {
				LogListModel.add("New status to ship: " + shipId +
						", STATUS: " + StatusType.getName(frameStatus).name() +
						", ACTION: " + ActionType.getName(frameAction).name() +
						", PERMISSION: " + PermissionType.getName(framePermission).name());
				if (frameAction.equals(ActionType.ENTER.toString())) {
					LogListModel.add("Ship: " + shipId + ", parking: " + frame.getData().getParking());
					ship.setParking(frame.getData().getParking());
				}
				ship.setStatus(frame.getData().getStatus());
			}
		}
		if (dataType.equals(DataType.RESPONSE.toString()) && framePermission.equals(PermissionType.DENY.toString())) {
			System.out.println("Ship number " + shipId + " has NOT permission to perform the operation: " + ActionType.getName(frameAction).name());
			System.out.println("Ship changes to new status, STATUS: " + StatusType.getName(frameStatus) +
					", ACTION: " + ActionType.getName(frameAction).name() +
					" PERMISSION: " + PermissionType.getName(framePermission).name());
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

	public void setSimulationStopped() {
		simulation = false;
	}

}
