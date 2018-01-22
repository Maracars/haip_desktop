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

	private Set<Integer> connectedShips;
	private Set<Integer> idleShips;
	private Set<Integer> disconnectedShips;

	private HashMap<Integer, Integer> idleTimeouts;
	private HashMap<Integer, Integer> disconnectTimeouts;

	private Serial serial;
	private Thread thread;
	private volatile boolean active;

	@SuppressWarnings("unchecked")
	public ControllerLogic(Serial serial, Port port) {
		this.port = port;
		this.receivedList = Collections.synchronizedList(new ArrayList());

		this.connectedShips = new CopyOnWriteArraySet<>();
		this.idleShips = new CopyOnWriteArraySet<>();
		this.disconnectedShips = new CopyOnWriteArraySet<>();

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
			Frame frame = FrameCreator.createDiscovery();
			sendFrame(frame);
			LogListModel.add("Discovery is sent to boats");

			try {
				Thread.sleep(ACK_TIME_SLOT * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			for (int i = 0; i < LOOP_IDLE_BOATS; i++) {
				for (int j = 0; j < LOOP_CONNECTED_BOATS; j++) {
					for (int ship : connectedShips) {
						control(ship);
					}
				}
				for (Integer ship : idleShips) {
					control(ship);
				}
			}
		}
	}

	private void control(int shipId) {
		long startingTime, elapsedTime;
		String shipIdStr = String.valueOf(shipId);
		Frame fr = FrameCreator.createToken(ProtocolProperties.MASTER_ID, Helpers.toNbitBinaryString(shipIdStr, 8));

		System.out.println("Sent token to ship number " + shipId);
		if (serial != null && serial.isConnected()) {
			Helpers.sendParsedFrame(fr, serial);
		}

		startingTime = System.currentTimeMillis();
		do {
			elapsedTime = System.currentTimeMillis() - startingTime;
		} while (receivedList.isEmpty() && elapsedTime < TOKEN_TIMEOUT);

		// Here we check that we have received something or has timed out, and that the boat that has sent is the one we want
		if (!receivedList.isEmpty()
				&& receivedList.get(0).getOriginId().equals(Helpers.toNbitBinaryString(shipIdStr, 8))) {
			// If asked for idle
			if (receivedList.get(0).getData().getStatus().getAction().equals(ActionType.IDLE.toString())) {
				LogListModel.add("Ship number " + shipId + " asked for idle");
				// If wasn't idle, set idle
				if (!idleShips.contains(shipId)) fromConnectedToIdle(shipId);
				// If was idle and wants to stay idle, count for the disconnect timeout
				else countForDisconnectTimeout(shipId);
				for (Frame frame : receivedList) {
					notifyMap(new Ship(frame.getOriginId(), frame.getData().getStatus()));
				}
			}
			// If wants to do something
			else {
				// If was idle, set connected
				if (idleShips.contains(shipId)) fromIdleToConnected(shipId);
				checkRequest(receivedList.get(0));

				System.out.println("Ship number " + shipId + " sent " + receivedList);
			}
		} else {
			countForIdleTimeout(shipId);

			if (!receivedList.isEmpty()) System.out.println("Invalid packet from ship " + shipId + " "
					+ receivedList.get(0).getOriginId());
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
		this.connectedShips.remove(shipId);
		this.idleShips.add(shipId);

		this.idleTimeouts.put(shipId, 0);
		LogListModel.add("These are the idle ships: " + idleShips);
	}

	private void fromIdleToConnected(Integer shipId) {
		this.idleShips.remove(shipId);
		this.connectedShips.add(shipId);
	}

	private void countForDisconnectTimeout(Integer shipId) {
		this.disconnectTimeouts.put(shipId, this.disconnectTimeouts.getOrDefault(shipId, 0) + 1);
		if (this.disconnectTimeouts.get(shipId) >= ProtocolProperties.DISCONNECT_TIMEOUT) {
			fromIdleToDisconnected(shipId);
		}
	}

	private void fromIdleToDisconnected(Integer shipId) {
		this.idleShips.remove(shipId);
		this.disconnectedShips.add(shipId);

		this.disconnectTimeouts.put(shipId, 0);
		LogListModel.add("Ship " + shipId + " disconnected");
	}

	private void fromDisconnectedToIdle(Integer shipId) {
		this.disconnectedShips.remove(shipId);
		this.idleShips.add(shipId);
		LogListModel.add("Ship " + shipId + " reconnected");
	}

	private void checkRequest(Frame frame) {
		Status nextStatus = frame.getData().getStatus();
		String statusStr = nextStatus.getPosition();
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
		notifyResponseSent(nextFrame);

		ship.setStatus(nextStatus);
		notifyMap(ship);
	}

	private void checkDockLeaveRequest(Ship ship, String actionStr, Status nextStatus, int shipID) {
		boolean okay = port.addToTransitionZone(ship, actionStr);
		if (okay) {
			nextStatus.setPosition(StatusType.TRANSIT.toString());
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
			nextStatus.setPosition(StatusType.SEA.toString());
			nextStatus.setPermission(PermissionType.ALLOW.toString());
			LogListModel.add("Ship " + shipID + " leaving to the sea");
		} else {
			nextStatus.setPosition(StatusType.PARKING.toString());
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
				nextStatus.setPosition(StatusType.TRANSIT.toString());
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
			/*try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}*/
		} else {
			System.out.println("Sent frame");
		}
	}

	private void notifyResponseSent(Frame frame) {
		setChanged();
		notifyObservers(frame);
	}

	private void notifyMap(Ship ship) {
		setChanged();
		notifyObservers(ship);
	}

	@Override
	public void update(Observable o, Object arg) {
		Frame frame = (Frame) arg;
		if (frame.getHeader().getPacketType().equals(PacketType.ACK.toString())) {
			int shipId = Integer.parseInt(frame.getOriginId(), 2);
			if (disconnectedShips.contains(shipId)) {
				fromDisconnectedToIdle(shipId);
			} else {
				connectedShips.add(shipId);
				LogListModel.add("New ship connected: "+shipId);
			}
			System.out.println(connectedShips);
		} else {
			receivedList.add(frame);
		}
	}

	public void resetPort() {
		receivedList.clear();
		connectedShips.clear();
		idleShips.clear();
		disconnectedShips.clear();
		idleTimeouts.clear();
		disconnectTimeouts.clear();
	}
}
