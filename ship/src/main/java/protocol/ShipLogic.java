package protocol;

import static protocol.ProtocolProperties.MASTER_ID;

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
import serial.SocketClient;
import ui.log.LogListModel;

public class ShipLogic extends Observable implements Observer {
	private Serial serial;
	private Ship ship;
	private boolean simulation;
	SocketClient socketClient;

	public ShipLogic(Serial serial, Ship ship, SocketClient socketClient) {
		this.serial = serial;
		this.ship = ship;
		simulation = false;
		this.socketClient = socketClient;
	}

	@Override
	public void update(Observable o, Object arg) {
		Frame frame = (Frame) arg;
		PacketType pt = PacketType.getName(frame.getHeader().getPacketType());
		Frame sendFrame;
		switch (pt) {
		case DISCOVERY:
			LogListModel.add("Discovery received");
			if(ship.checkDiscovery()) {
				checkDiscovery(frame, ship);
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
			sendFrame = checkToken(ship);
			replyController(sendFrame);
			break;
		default:
			break;
		}
	}
	
	public void checkDiscovery(Frame frame, Ship ship) {
		int timeWindow = Integer.parseInt(frame.getData().getTimeWindow(), 2);
		Random interval = new Random();
		int delayMs = interval.nextInt(timeWindow*1000) + 1;
		LogListModel.add("Interval for ship: " + Integer.parseInt(ship.getId(),2) + ", interval: " + delayMs + " ms");
		Frame sendFrame = FrameCreator.createAck(ship.getId(), MASTER_ID);
		ackDelay(sendFrame, delayMs);
		ship.resetDiscoveryCounter();
	}
	
	private void ackDelay(Frame frame, long delayMs) {
		AckDelayScheduler ackDelayScheduler = new AckDelayScheduler();
		ackDelayScheduler.sendAckAfterDelay(frame, this.serial, delayMs, socketClient);
	}

	public Frame checkToken(Ship ship) {
		Frame sendFrame = null;

		if (simulation && ship.getStatus().getAction().equals(ActionType.IDLE.toString()) && ship.getIdleTime() >= 10) {
			Status newStatus = DecisionMaker.getRandomAction(StatusType.getName(ship.getStatus().getPosition()));
			ship.setStatus(new Status(ship.getStatus().getPosition(), newStatus.getAction(), PermissionType.ASK.toString()));
			ship.setActionList(new ArrayList<>());
			ship.addAction(newStatus);
		} else if (ship.getIdleTime() < 10 && simulation) {
			ship.addIdleTime(1);
			ship.addDiscoveryCounter();
		}

		if (ship.getActionList().size() > 0) {
			ship.addDiscoveryCounter();
			if (ship.getActionList().get(0).getAction().equals(ActionType.IDLE.toString())) {
				sendFrame = FrameCreator.createStatus(ship.getId(), ProtocolProperties.MASTER_ID, ship.getStatus());
				LogListModel.add("Ship number " + Integer.parseInt(ship.getId(), 2) + " sends STATUS");
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
		Helpers.sendParsedFrame(frame, serial, null, socketClient);
	}

	public void checkShipMovement(Frame frame, Ship ship) {
		System.out.println(frame.toString());
		String dataType = frame.getData().getDataType();
		String permission = frame.getData().getStatus().getPermission();
		String frameStatus = frame.getData().getStatus().getPosition();
		String actionStatus = ship.getActionList().get(0).getPosition();
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
					Status newStatus = DecisionMaker.getRandomAction(StatusType.getName(frame.getData().getStatus().getPosition()));
					ship.setStatus(new Status(frame.getData().getStatus().getPosition(), newStatus.getAction(), PermissionType.ASK.toString()));
					ship.setActionList(new ArrayList<>());
					ship.addAction(newStatus);
				} else {
					ship.setStatus(new Status(frame.getData().getStatus().getPosition(), ActionType.IDLE.toString(), PermissionType.ASK.toString()));
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
			LogListModel.add("Ship: "+shipId +" NOT permission to: " +ActionType.getName(frameAction).name());
			LogListModel.add("New status to ship: "+ shipId + 
					", STATUS: " + StatusType.getName(frameStatus).name() + 
					", ACTION: " + ActionType.getName(frameAction).name() +
					", PERMISSION: " + PermissionType.getName(framePermission).name());
			ship.setStatus(frame.getData().getStatus());

		}
	}

	private void notifyPanel() {
		setChanged();
		notifyObservers();
	}
	
	public void connect() {
		socketClient.connect();
	}
	
	public void waitForDiscovery() {
		socketClient.waitForCommunication();
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

	public SocketClient getSocketClient() {
		return socketClient;
	}

	public void setSocketClient(SocketClient socketClient) {
		this.socketClient = socketClient;
	}
	
	

}
