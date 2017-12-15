package protocol;

import helpers.Helpers;
import models.*;
import serial.Serial;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

import static protocol.ProtocolProperties.*;

// TODO These functions have been done here. Why? Idk, but have to be moved somewhere else. Where? Idk.
public class ControllerLogic extends Observable implements Observer, Runnable {

	private Serial serial;
	private List<Frame> receivedList;
	private Set<Integer> connectedBoats;
	private Set<Integer> idleBoats;
	private HashMap<Integer, Integer> timeouts;
	private Port port;

	@SuppressWarnings("unchecked")
	public ControllerLogic(Serial serial, Port port) {
		this.serial = serial;
		this.serial.addObserver(this);
		this.port = port;
		receivedList = Collections.synchronizedList(new ArrayList());
		connectedBoats = new CopyOnWriteArraySet<>();
		idleBoats = new CopyOnWriteArraySet<>();
		timeouts = new HashMap<>();
	}

	public void control(String boat) {

		Integer boat_id = Integer.parseInt(boat);

		Frame fr = FrameCreator.createToken(ProtocolProperties.MASTER_ID, Helpers.toByteBinString(boat));
		if (serial != null && serial.isConnected()) {
			Helpers.sendParsedFrame(fr, serial);

		} else {
			System.out.println("Sent parsed token to boat number " + boat_id);

		}

		long count = 0;
		//noinspection StatementWithEmptyBody
		while (count++ < ProtocolProperties.TIMEOUT && receivedList.isEmpty()) ;

		// TODO Here we take the first packet received, dunno if we must ensure we have just one...
		// Here we check that we have received something or has timed out, and that the boat that has sent is the one we want
		if (!receivedList.isEmpty() && receivedList.get(0).getOriginId().equals(Helpers.toByteBinString(boat))) {
			if (receivedList.get(0).getData().getStatus().getAction().equals(ActionType.IDLE.toString())) {
				addTimeout(boat_id);
			} else {
				//TODO Here we must send the response to the request.
				if (idleBoats.contains(boat_id)) addConnectedBoat(boat_id);
				System.out.println("Ship number " + boat + " sent " + receivedList);
				checkRequest(receivedList.get(0));
			}

		} else {
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

		if (status_str.equals(StatusType.PARKING.toString()) && action_str.equals(ActionType.LEAVE.toString())) {
			boolean okay = port.addToTransitionZone(ship, action_str);
			nextStatus = new Status(StatusType.TRANSIT.toString(), ActionType.LEAVE.toString());

			if (okay) {
				nextStatus.setPermission(PermissionType.ALLOW.toString());

				System.out.println("Ship :" + frame.getOriginId() + " is going to the transit zone, to leave the dock");
			} else {
				nextStatus.setPermission(PermissionType.DENY.toString());


			}

		} else if (status_str.equals(StatusType.TRANSIT.toString())) {


			if (action_str.equals(ActionType.LEAVE.toString())) {
				nextStatus = new Status(StatusType.SEA.toString(), ActionType.LEAVE.toString(), PermissionType.ALLOW.toString());

				System.out.println("Ship :" + frame.getOriginId() + " is going from the transit zone to the sea. Goodbye!");

			} else {
				nextStatus = new Status(StatusType.PARKING.toString(), ActionType.ENTER.toString(), PermissionType.ALLOW.toString());

				System.out.println("Ship :" + frame.getOriginId() + " is going from the transit zone to the dock");
			}


		} else if (status_str.equals(StatusType.SEA.toString()) && action_str.equals(ActionType.ENTER.toString())) {
			Mooring freeMooring = port.getFreeMooring(ship);
			boolean okay = port.addToTransitionZone(ship, action_str);

			nextStatus = new Status(StatusType.TRANSIT.toString(), ActionType.ENTER.toString());
			if (okay) {
				nextStatus.setPermission(PermissionType.ALLOW.toString());
				System.out.println("Ship :" + frame.getOriginId() + " is going to the transit zone, to enter to the dock");
				System.out.println("The mooring assigned: " + freeMooring.getId());
				parking = freeMooring.getId();
			} else {
				nextStatus.setPermission(PermissionType.DENY.toString());

			}

		} else {
			nextStatus = new Status(StatusType.TRANSIT.toString(), ActionType.ENTER.toString(), PermissionType.INVALID.toString());
			System.out.println("Invalid state");
		}
		Frame nextFrame = FrameCreator.createResponse(ProtocolProperties.MASTER_ID, ship.getId(), nextStatus, parking);

		if (serial == null || !serial.isConnected()) {
			System.out.println("Sent frame");
		} else {
			Helpers.sendParsedFrame(nextFrame, serial);
		}
		setSentRequest(nextFrame);
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

		System.out.println("jajaj d puta madre");
		Frame frame = (Frame) arg;
		if (PacketType.ACK.equals(PacketType.getName(frame.getHeader().getPacketType()))) {
			connectedBoats.add(Integer.parseInt(frame.getOriginId(), 2));
			System.out.println(connectedBoats);
		} else {
			receivedList.add(frame);
		}
	}

	@Override
	public void run() {
		while (true) {
			for (int k = 0; k < 5; k++) {
				for (int i = 0; i < ProtocolProperties.LOOP_IDLE_BOATS; i++) {

					for (int j = 0; j < ProtocolProperties.LOOP_CONNECTED_BOATS; j++) {
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
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
