package protocol;

import helpers.Helpers;
import models.*;
import protocol.ProtocolProperties.*;
import serial.Serial;
import ui.log.LogListModel;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

import static protocol.ProtocolProperties.*;

public class ControllerLogic extends Observable implements Observer, Runnable {
	private Port port;
	private List<Frame> receivedList;

	private Set<Integer> connectedBoats;
	private Set<Integer> idleBoats;
	private Set<Integer> disconnectedBoats;

	private HashMap<Integer, Integer> idleTimeouts;
	private HashMap<Integer, Integer> disconnectTimeouts;

	private Serial serial;
	private Thread thread;
	private volatile boolean active;

	@SuppressWarnings("unchecked")
	public ControllerLogic(Serial serial, Port port) {
		this.port = port;
		this.receivedList = Collections.synchronizedList(new ArrayList());

		this.connectedBoats = new CopyOnWriteArraySet<>();
		this.idleBoats = new CopyOnWriteArraySet<>();
		this.disconnectedBoats = new CopyOnWriteArraySet<>();

		this.idleTimeouts = new HashMap<>();
		this.disconnectTimeouts = new HashMap<>();

		this.serial = serial;
		this.active = false;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean state) throws InterruptedException {
		this.active = state;
		if (active) {
			this.thread = new Thread(this);
			this.thread.start();
		} else {
			this.thread.join();
		}
	}

	@Override
	public void run() {
		while (active) {
			if (serial != null && serial.isConnected()) {
				Helpers.sendParsedFrame(FrameCreator.createDiscovery(), serial);
			}
			LogListModel.add("Discovery is sent to boats");

			try {
				Thread.sleep(ACK_TIME_SLOT * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			for (int i = 0; i < LOOP_IDLE_BOATS; i++) {
				for (int j = 0; j < LOOP_CONNECTED_BOATS; j++) {
					for (Integer boat : connectedBoats) {
						control(boat.toString());
					}
				}
				for (Integer boat : idleBoats) {
					control(boat.toString());
				}
			}
		}
	}

	private void control(String boat) {
		long startingTime, elapsedTime;
		Integer shipId = Integer.parseInt(boat);
		Frame fr = FrameCreator.createToken(ProtocolProperties.MASTER_ID, Helpers.toNbitBinaryString(boat, 8));

		System.out.println("Sent token to boat number " + shipId);
		if (serial != null && serial.isConnected()) {
			Helpers.sendParsedFrame(fr, serial);
		}

		startingTime = System.currentTimeMillis();
		do {
			elapsedTime = System.currentTimeMillis() - startingTime;
		} while (receivedList.isEmpty() && elapsedTime < TOKEN_TIMEOUT);

		// Here we check that we have received something or has timed out, and that the boat that has sent is the one we want
		if (!receivedList.isEmpty()
				&& receivedList.get(0).getOriginId().equals(Helpers.toNbitBinaryString(boat, 8))) {
			// If asked for idle
			if (receivedList.get(0).getData().getStatus().getAction().equals(ActionType.IDLE.toString())) {
				LogListModel.add("Boat number " + shipId + " asked for idle");
				// If wasn't idle, set idle
				if (!idleBoats.contains(shipId)) fromConnectedToIdle(shipId);
				// If was idle and wants to stay idle, count for the disconnect timeout
				else countForDisconnectTimeout(shipId);
				updateMap(new Ship(receivedList.get(0).getOriginId(), receivedList.get(0).getData().getStatus()));
			}
			// If wants to do something
			else {
				// If was idle, set connected
				if (idleBoats.contains(shipId)) fromIdleToConnected(shipId);
				checkRequest(receivedList.get(0));

				System.out.println("Ship number " + boat + " sent " + receivedList);
			}
		} else {
			countForIdleTimeout(shipId);

			if (!receivedList.isEmpty()) System.out.println("Invalid packet " + boat + " " + receivedList.get(0).getOriginId());
		}
		receivedList.clear();
	}

	private void countForIdleTimeout(Integer shipId) {
		this.idleTimeouts.put(shipId, this.idleTimeouts.getOrDefault(shipId, 0) + 1);
		if (this.idleTimeouts.get(shipId) >= ProtocolProperties.IDLE_TIMEOUT) {
			fromConnectedToIdle(shipId);
		}
	}

	private void fromConnectedToIdle(Integer shipId) {
		this.connectedBoats.remove(shipId);
		this.idleBoats.add(shipId);

		this.idleTimeouts.put(shipId, 0);
		LogListModel.add("These are the idle boats: " + idleBoats);
	}

	private void fromIdleToConnected(Integer shipId) {
		this.idleBoats.remove(shipId);
		this.connectedBoats.add(shipId);
	}

	private void countForDisconnectTimeout(Integer shipId) {
		this.disconnectTimeouts.put(shipId, this.disconnectTimeouts.getOrDefault(shipId, 0) + 1);
		if (this.disconnectTimeouts.get(shipId) >= ProtocolProperties.DISCONNECT_TIMEOUT) {
			fromIdleToDisconnected(shipId);
		}
	}

	private void fromIdleToDisconnected(Integer shipId) {
		this.idleBoats.remove(shipId);
		this.disconnectedBoats.add(shipId);

		this.disconnectTimeouts.put(shipId, 0);
		LogListModel.add("Boat " + shipId + " disconnected");
	}

	private void fromDisconnectedToIdle(Integer shipId) {
		this.disconnectedBoats.remove(shipId);
		this.idleBoats.add(shipId);
		LogListModel.add("Boat " + shipId + " reconnected");
	}

	private void checkRequest(Frame frame) {
		Status nextStatus = frame.getData().getStatus();
		String statusStr = nextStatus.getStatus();
		String actionStr = nextStatus.getAction();

		Ship ship = new Ship(frame.getOriginId());
		int shipID = Integer.parseInt(frame.getOriginId(), 2);
		String parking = null;

		// Dock, leave
		if (statusStr.equals(StatusType.PARKING.toString()) && actionStr.equals(ActionType.LEAVE.toString())) {
			checkDockLeaveRequest(ship, actionStr, nextStatus, shipID);
		}
		// Transit, leave or enter
		else if (statusStr.equals(StatusType.TRANSIT.toString()) &&
				(actionStr.equals(ActionType.ENTER.toString())) || actionStr.equals(ActionType.LEAVE.toString())) {
			checkTransitRequest(ship, actionStr, nextStatus, shipID);
		}
		// Sea, enter
		else if (statusStr.equals(StatusType.SEA.toString()) && actionStr.equals(ActionType.ENTER.toString())) {
			parking = checkSeaEnterRequest(ship, actionStr, nextStatus, shipID);
		}
		// Invalid
		else {
			checkInvalidRequest(nextStatus, shipID);
		}
		Frame nextFrame = FrameCreator.createResponse(MASTER_ID, ship.getId(), nextStatus, parking);
		System.out.println("Next frame to send: " + nextFrame.toString());

		// Send frame
		sendFrame(nextFrame);
		setSentRequest(nextFrame);

		ship.setStatus(nextStatus);
		updateMap(ship);
	}

	private void checkDockLeaveRequest(Ship ship, String actionStr, Status nextStatus, int shipID) {
		boolean okay = port.addToTransitionZone(ship, actionStr);
		if (okay) {
			nextStatus.setStatus(StatusType.TRANSIT.toString());
			nextStatus.setAction(ActionType.LEAVE.toString());
			nextStatus.setPermission(PermissionType.ALLOW.toString());
			LogListModel.add("Ship " + shipID + " leaving dock");
		} else {
			nextStatus.setPermission(PermissionType.DENY.toString());
			LogListModel.add("Not enough space in transit for ship " + shipID);
		}
	}

	private void checkTransitRequest(Ship ship, String actionStr, Status nextStatus, int shipID) {
		if (actionStr.equals(ActionType.LEAVE.toString())) {
			nextStatus.setStatus(StatusType.SEA.toString());
			nextStatus.setPermission(PermissionType.ALLOW.toString());
			LogListModel.add("Ship " + shipID + " leaving to the sea");
		} else {
			nextStatus.setStatus(StatusType.PARKING.toString());
			nextStatus.setPermission(PermissionType.ALLOW.toString());
			LogListModel.add("Ship " + shipID + " entering dock");
		}
		port.removeFromTransitZone(ship);
	}

	private String checkSeaEnterRequest(Ship ship, String actionStr, Status nextStatus, int shipID) {
		Mooring freeMooring = port.getFreeMooring(ship);
		String parking = null;

		if (freeMooring != null) {
			parking = freeMooring.getId();
			LogListModel.add("Assigned mooring " + Integer.parseInt(parking, 2));
			boolean freeTransit = port.addToTransitionZone(ship, actionStr);

			if (freeTransit) {
				nextStatus.setStatus(StatusType.TRANSIT.toString());
				nextStatus.setAction(ActionType.ENTER.toString());
				nextStatus.setPermission(PermissionType.ALLOW.toString());
				LogListModel.add("Ship " + shipID + " entering transit zone");
			} else {
				nextStatus.setPermission(PermissionType.DENY.toString());
				LogListModel.add("Not enough space in transit for ship " + shipID);
			}
		} else {
			nextStatus.setPermission(PermissionType.DENY.toString());
			LogListModel.add("Ship " + shipID + " access denied, no free mooring");
		}
		return parking;
	}

	private void checkInvalidRequest(Status nextStatus, int shipID) {
		nextStatus.setPermission(PermissionType.INVALID.toString());
		LogListModel.add("Ship " + shipID + " is in an invalid state");
	}

	private void sendFrame(Frame frame) {
		if (serial != null && serial.isConnected()) {
			Helpers.sendParsedFrame(frame, serial);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Sent frame");
		}
	}

	private void setSentRequest(Frame frame) {
		setChanged();
		notifyObservers(frame);
	}

	@Override
	public void update(Observable o, Object arg) {
		Frame frame = (Frame) arg;
		if (frame.getHeader().getPacketType().equals(PacketType.ACK.toString())) {
			int shipId = Integer.parseInt(frame.getOriginId(), 2);
			if (disconnectedBoats.contains(shipId)) {
				fromDisconnectedToIdle(shipId);
			} else {
				connectedBoats.add(shipId);
			}
			System.out.println(connectedBoats);
		} else {
			receivedList.add(frame);
		}
	}

	private void updateMap(Ship ship) {
		setChanged();
		notifyObservers(ship);
	}
}
