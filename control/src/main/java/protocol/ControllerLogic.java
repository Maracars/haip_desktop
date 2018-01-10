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
	private HashMap<Integer, Integer> timeouts;

	private Serial serial;

	private Thread thread;
	private volatile boolean active;

	@SuppressWarnings("unchecked")
	public ControllerLogic(Serial serial, Port port) {
		this.port = port;
		receivedList = Collections.synchronizedList(new ArrayList());
		connectedBoats = new CopyOnWriteArraySet<>();
		idleBoats = new CopyOnWriteArraySet<>();
		timeouts = new HashMap<>();
		this.serial = serial;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean state) throws InterruptedException {
		this.active = state;
		if (active) {
			this.thread = new Thread(this);
			this.thread.start();
		}
		else {
			this.thread.join();
		}
	}

	@Override
	public void run() {
		while (active) {
			for (int k = 0; k < 5; k++) {
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
			if (serial != null && serial.isConnected()) {
				Helpers.sendParsedFrame(FrameCreator.createDiscovery(), serial);
			}
			LogListModel.add("Discovery is sent to boats");

			try {
				//TODO I have no fucking idea how big the delay should be
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void control(String boat) {
		Integer boat_id = Integer.parseInt(boat);

		Frame fr = FrameCreator.createToken(ProtocolProperties.MASTER_ID, Helpers.toNbitBinaryString(boat, 8));
		if (serial != null && serial.isConnected()) {
			Helpers.sendParsedFrame(fr, serial);
		}
		System.out.println("Sent token to boat number " + boat_id);

		long count = 0;
		//noinspection StatementWithEmptyBody
		while (count++ < ProtocolProperties.TIMEOUT && receivedList.isEmpty()) ;

		// TODO Here we take the first packet received, dunno if we must ensure we have just one...
		// Here we check that we have received something or has timed out, and that the boat that has sent is the one we want
		if (!receivedList.isEmpty() && receivedList.get(0).getOriginId().equals(Helpers.toNbitBinaryString(boat, 8))) {
			if (receivedList.get(0).getData().getStatus().getAction().equals(ActionType.IDLE.toString())) {
				addTimeout(boat_id);
				updateMap(new Ship(receivedList.get(0).getOriginId(), receivedList.get(0).getData().getStatus()));
				System.out.println("Asked for idle, and added timeout" + timeouts);
			}
			else {
				//TODO Here we must send the response to the request.
				if (idleBoats.contains(boat_id)) {
					addConnectedBoat(boat_id);
				}
				System.out.println("Ship number " + boat + " sent " + receivedList);
				checkRequest(receivedList.get(0));
			}
		}
		else {
			if (!receivedList.isEmpty()) {
				System.out.println("Invalid packet " + boat + " " + receivedList.get(0).getOriginId());
			}
			addTimeout(boat_id);
		}
		receivedList.clear();
	}

	private void addTimeout(Integer boat_id) {
		timeouts.put(boat_id, timeouts.getOrDefault(boat_id, 0) + 1);

		if (timeouts.get(boat_id) >= ProtocolProperties.TIMEOUTED_LOOP_LIMIT) addIdleBoat(boat_id);
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

	private void checkDockLeaveRequest(Ship ship, String actionStr, Status nextStatus, int shipID){
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

	private String checkSeaEnterRequest(Ship ship, String actionStr, Status nextStatus, int shipID){
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
		}
		else {
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

	private void addConnectedBoat(Integer boat) {
		connectedBoats.add(boat);
		idleBoats.remove(boat);
		timeouts.put(boat, 0);
	}

	private void addIdleBoat(Integer boat) {
		idleBoats.add(boat);
		System.out.println("Idle boat added: " + boat + " these are the idle boats: " + idleBoats);
		connectedBoats.remove(boat);
	}

	private void setSentRequest(Frame frame) {
		setChanged();
		notifyObservers(frame);
	}

	@Override
	public void update(Observable o, Object arg) {
		Frame frame = (Frame) arg;
		if (frame.getHeader().getPacketType().equals(PacketType.ACK.toString())) {
			connectedBoats.add(Integer.parseInt(frame.getOriginId(), 2));
			System.out.println(connectedBoats);
		} else {
			receivedList.add(frame);
		}
	}

	private void updateMap(Ship ship) {
		setChanged();
		notifyObservers(ship);
	}

	public Port getPort() {
		return port;
	}
}
