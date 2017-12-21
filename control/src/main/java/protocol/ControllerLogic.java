package protocol;

import static protocol.ProtocolProperties.LOOP_CONNECTED_BOATS;
import static protocol.ProtocolProperties.LOOP_IDLE_BOATS;
import static protocol.ProtocolProperties.MASTER_ID;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import helpers.Helpers;
import models.Frame;
import models.Mooring;
import models.Port;
import models.Ship;
import models.Status;
import protocol.ProtocolProperties.ActionType;
import protocol.ProtocolProperties.PacketType;
import protocol.ProtocolProperties.PermissionType;
import protocol.ProtocolProperties.StatusType;
import serial.Serial;

// TODO These functions have been done here. Why? Idk, but have to be moved somewhere else. Where? Idk.
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

		this.thread = new Thread(this);
	}

	public void startLogic() {
		if (!this.thread.isAlive()) {
			this.running = true;
			this.thread.start();
		}
	}

	public void stopLogic() {
		if (this.thread.isAlive()) {
			this.running = false;
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
			} else {
				System.out.println("Discovery is sent to boats");
			}
			try {
				//TODO I have no fucking idea how big the delay should be
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void control(String boat) {
		Integer boat_id = Integer.parseInt(boat);

		Frame fr = FrameCreator.createToken(ProtocolProperties.MASTER_ID, Helpers.toByteBinString(boat, 8));
		if (serial != null && serial.isConnected()) {
			Helpers.sendParsedFrame(fr, serial);

		} else {
			System.out.println("Sent validFrame token to boat number :" + boat_id);

		}

		long count = 0;
		//noinspection StatementWithEmptyBody
		while (count++ < ProtocolProperties.TIMEOUT && receivedList.isEmpty()) ;

		// TODO Here we take the first packet received, dunno if we must ensure we have just one...
		// Here we check that we have received something or has timed out, and that the boat that has sent is the one we want
		if (!receivedList.isEmpty() && receivedList.get(0).getOriginId().equals(Helpers.toByteBinString(boat, 8))) {
			if (receivedList.get(0).getData().getStatus().getAction().equals(ActionType.IDLE.toString())) {
				addTimeout(boat_id);
				System.out.println("Asked for iddle, and added timeout" + timeouts);

			} else {
				//TODO Here we must send the response to the request.
				if (idleBoats.contains(boat_id)) addConnectedBoat(boat_id);
				System.out.println("Ship number " + boat + " sent " + receivedList);
				checkRequest(receivedList.get(0));
			}

		} else {
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

	public void checkRequest(Frame frame) {
		Status status = frame.getData().getStatus();
		Status nextStatus;
		String status_str = status.getStatus();
		String action_str = status.getAction();
		Ship ship = new Ship(frame.getOriginId());
		String parking = null;

		// Dock, leave
		if (status_str.equals(StatusType.PARKING.toString()) && action_str.equals(ActionType.LEAVE.toString())) {
			boolean okay = port.addToTransitionZone(ship, action_str);
			nextStatus = new Status(StatusType.TRANSIT.toString(), ActionType.LEAVE.toString());

			if (okay) {
				nextStatus.setPermission(PermissionType.ALLOW.toString());
				System.out.println("Ship :" + frame.getOriginId() + " is going from the dock to the transit zone");
			} else {
				System.out.println("Ship " + frame.getOriginId() + " access to transit zone denied, not enough space");

				nextStatus.setPermission(PermissionType.DENY.toString());
			}
		}
		// Transit, leave or enter
		else if (status_str.equals(StatusType.TRANSIT.toString())
				&& (action_str.equals(ActionType.ENTER.toString())) || action_str.equals(ActionType.LEAVE.toString())) {
			port.removeFromTransitZone(ship);

			if (action_str.equals(ActionType.LEAVE.toString())) {
				nextStatus = new Status(StatusType.SEA.toString(), ActionType.LEAVE.toString(), PermissionType.ALLOW.toString());
				System.out.println("Ship " + frame.getOriginId() + " is going from the transit zone to the sea. Goodbye!");
			} else {
				nextStatus = new Status(StatusType.PARKING.toString(), ActionType.ENTER.toString(), PermissionType.ALLOW.toString());
				System.out.println("Ship " + frame.getOriginId() + " is going from the transit zone to the dock");
			}
			
		}
		// Sea, enter
		else if (status_str.equals(StatusType.SEA.toString()) && action_str.equals(ActionType.ENTER.toString())) {
			Mooring freeMooring = port.getFreeMooring(ship);
			boolean okay = port.addToTransitionZone(ship, action_str);

			nextStatus = new Status(StatusType.TRANSIT.toString(), ActionType.ENTER.toString());
			if (okay) {
				nextStatus.setPermission(PermissionType.ALLOW.toString());
				System.out.println("Ship " + frame.getOriginId() + " is going from the sea to the transit zone");
				System.out.println("The mooring assigned: " + freeMooring.getId());
				parking = freeMooring.getId();
				System.out.println("PARKING ASSIGNEEEED "+parking);
			} else {
				nextStatus.setPermission(PermissionType.DENY.toString());
				System.out.println("Ship " + frame.getOriginId() + " access to transit zone denied, not enough space in transit zone");
				System.out.println("The mooring assigned: " + freeMooring.getId());
				// TODO Don't know if this line is necessary (do we have to tell the ship its mooring if it doesn't have access yet???)
				parking = freeMooring.getId();
			}
		}
		// Invalid
		else {
			nextStatus = new Status(StatusType.TRANSIT.toString(), ActionType.ENTER.toString(), PermissionType.INVALID.toString());
			System.out.println("Invalid state");
		}
		Frame nextFrame = FrameCreator.createResponse(MASTER_ID, ship.getId(), nextStatus, parking);

		// Send frame
		if (serial != null && serial.isConnected()) {
			Helpers.sendParsedFrame(nextFrame, serial);
		} else {
			System.out.println("Sent frame");
		}
		setSentRequest(nextFrame);
		
		if(nextStatus.getPermission().equals(PermissionType.ALLOW.toString())) {
			ship.setStatus(nextStatus);
		}else{
			status.setPermission(PermissionType.DENY.toString());
			ship.setStatus(status);
		}
		
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
			Ship ship = new Ship(frame.getOriginId());
			updateMap(ship);
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
