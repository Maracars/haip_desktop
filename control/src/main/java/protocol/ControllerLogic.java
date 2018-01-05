package protocol;

import helpers.Helpers;
import models.*;
import protocol.ProtocolProperties.*;
import serial.Serial;
import ui.log.LogModel;

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
	private volatile boolean running;

	@SuppressWarnings("unchecked")
	public ControllerLogic(Serial serial, Port port) {
		this.port = port;
		receivedList = Collections.synchronizedList(new ArrayList());
		connectedBoats = new CopyOnWriteArraySet<>();
		idleBoats = new CopyOnWriteArraySet<>();
		timeouts = new HashMap<>();
		this.serial = serial;
	}

	public void startLogic() {
		this.running = true;
		this.thread = new Thread(this);
		this.thread.start();
	}

	public void stopLogic() {
		this.running = false;
		try {
			this.thread.join();
		} catch (InterruptedException e) {
			LogModel.add("Couldn't stop system");
		}
	}

	public boolean isRunning() {
		return running;
	}

	@Override
	public void run() {
		while (running) {
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
			LogModel.add("Discovery is sent to boats");

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

		Frame fr = FrameCreator.createToken(ProtocolProperties.MASTER_ID, Helpers.toByteBinString(boat, 8));
		if (serial != null && serial.isConnected()) {
			Helpers.sendParsedFrame(fr, serial);
		}
		System.out.println("Sent validFrame token to boat number :" + boat_id);

		long count = 0;
		//noinspection StatementWithEmptyBody
		while (count++ < ProtocolProperties.TIMEOUT && receivedList.isEmpty()) ;

		// TODO Here we take the first packet received, dunno if we must ensure we have just one...
		// Here we check that we have received something or has timed out, and that the boat that has sent is the one we want
		if (!receivedList.isEmpty() && receivedList.get(0).getOriginId().equals(Helpers.toByteBinString(boat, 8))) {
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
		String status_str = nextStatus.getStatus();
		String action_str = nextStatus.getAction();

		Ship ship = new Ship(frame.getOriginId());
		int shipID = Integer.parseInt(frame.getOriginId(), 2);
		String parking = null;

		// Dock, leave
		if (status_str.equals(StatusType.PARKING.toString()) && action_str.equals(ActionType.LEAVE.toString())) {
			boolean okay = port.addToTransitionZone(ship, action_str);
			if (okay) {
				nextStatus.setStatus(StatusType.TRANSIT.toString());
				nextStatus.setAction(ActionType.LEAVE.toString());
				nextStatus.setPermission(PermissionType.ALLOW.toString());
				LogModel.add("Ship " + shipID + " is going from the dock to the transit zone.");
			} else {
				//nextStatus.setStatus(StatusType.PARKING.toString());
				//nextStatus.setAction(ActionType.LEAVE.toString());
				nextStatus.setPermission(PermissionType.DENY.toString());
				LogModel.add("Ship " + shipID + " access to transit zone denied, not enough space.");
			}
		}
		// Transit, leave or enter
		else if (status_str.equals(StatusType.TRANSIT.toString()) &&
				(action_str.equals(ActionType.ENTER.toString())) || action_str.equals(ActionType.LEAVE.toString())) {

			if (action_str.equals(ActionType.LEAVE.toString())) {
				nextStatus.setStatus(StatusType.SEA.toString());
				nextStatus.setAction(ActionType.LEAVE.toString());
				nextStatus.setPermission(PermissionType.ALLOW.toString());
				LogModel.add("Ship " + shipID + " is going from the transit zone to the sea. Goodbye!");
			} else {
				nextStatus.setStatus(StatusType.PARKING.toString());
				nextStatus.setAction(ActionType.ENTER.toString());
				nextStatus.setPermission(PermissionType.ALLOW.toString());
				LogModel.add("Ship " + shipID + " is going from the transit zone to the dock.");
			}
			port.removeFromTransitZone(ship);
		}
		// Sea, enter
		else if (status_str.equals(StatusType.SEA.toString()) && action_str.equals(ActionType.ENTER.toString())) {
			Mooring freeMooring = port.getFreeMooring(ship);

			if (freeMooring != null) {
				parking = freeMooring.getId();
				LogModel.add("The mooring assigned: " + Integer.parseInt(parking, 2));
				boolean freeTransit = port.addToTransitionZone(ship, action_str);

				if (freeTransit) {
					nextStatus.setStatus(StatusType.TRANSIT.toString());
					nextStatus.setAction(ActionType.ENTER.toString());
					nextStatus.setPermission(PermissionType.ALLOW.toString());
					LogModel.add("Ship " + shipID + " is going from the sea to the transit zone");
				} else {
					//nextStatus.setStatus(StatusType.SEA.toString());
					//nextStatus.setAction(ActionType.ENTER.toString());
					nextStatus.setPermission(PermissionType.DENY.toString());
					LogModel.add("Ship " + shipID + " access to transit zone denied, not enough space in transit zone");
				}
			}
			else {
				//nextStatus.setStatus(StatusType.SEA.toString());
				//nextStatus.setAction(ActionType.ENTER.toString());
				nextStatus.setPermission(PermissionType.DENY.toString());
				LogModel.add("Ship " + shipID + " access denied, no free mooring");
			}
		}
		// Invalid
		else {
			//nextStatus.setStatus(StatusType.TRANSIT.toString());
			//nextStatus.setAction(ActionType.ENTER.toString());
			nextStatus.setPermission(PermissionType.INVALID.toString());
			LogModel.add("Ship " + shipID + " is in an invalid state");
		}
		Frame nextFrame = FrameCreator.createResponse(MASTER_ID, ship.getId(), nextStatus, parking);
		System.out.println("Next frame to send: " + nextFrame.toString());

		// Send frame
		if (serial != null && serial.isConnected()) {
			Helpers.sendParsedFrame(nextFrame, serial);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Sent frame");
		}
		setSentRequest(nextFrame);

		ship.setStatus(nextStatus);
		updateMap(ship);
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
		//if (PacketType.ACK.equals(PacketType.getName(frame.getHeader().getPacketType()))) {
		if (frame.getHeader().getPacketType().equals(PacketType.ACK.toString())) {
			connectedBoats.add(Integer.parseInt(frame.getOriginId(), 2));
			System.out.println(connectedBoats);
			//Ship ship = new Ship(frame.getOriginId());
			//updateMap(ship);
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
