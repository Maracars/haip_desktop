package protocol;

import helpers.Helpers;
import models.*;
import serial.Serial;

import java.util.*;

import static protocol.ProtocolProperties.ActionType;
import static protocol.ProtocolProperties.StatusType;

// TODO These functions have been done here. Why? Idk, but have to be moved somewhere else. Where? Idk.
public class ControllerLogic implements Observer, Runnable {

	private Serial serial;
	private List<Frame> receivedList;
	private ArrayList<Integer> connectedBoats;
	private ArrayList<Integer> idleBoats;
	private HashMap<Integer, Integer> timeouts;
	private Port port;

	@SuppressWarnings("unchecked")
	public ControllerLogic(Serial serial, Port port) {
		this.serial = serial;
		this.port = port;
		receivedList = Collections.synchronizedList(new ArrayList());
		connectedBoats = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7));
		idleBoats = new ArrayList<>();
		timeouts = new HashMap<>();
	}

	public void controllerIokse(String boat) {

		Integer boat_id = Integer.parseInt(boat);

		Frame fr = FrameCreator.createToken(ProtocolProperties.MASTER_ID, Helpers.toByteBinString(boat));
		if (serial == null) {
			System.out.println(fr );
		} else {
			Helpers.sendParsedFrame(fr, serial);

		}

		long count = 0;
		//noinspection StatementWithEmptyBody
		while (count++ < ProtocolProperties.TIMEOUT && receivedList.isEmpty()) {
		}
		// TODO Here we take the first packet received, dunno if we must ensure we have just one...
		// Here we check that we have received something or has timed out, and that the boat that has sent is the one we want
		if (!receivedList.isEmpty() && receivedList.get(0).getOriginId().equals(boat)) {
			if (receivedList.get(0).getData().getStatus().getAction().equals(ActionType.IDDLE.toString())) {
				System.out.println("iddle");
				addTimeout(boat_id);
			} else {
				//TODO Here we must send the response to the request.
				if (idleBoats.contains(boat_id)) addConnectedBoat(boat_id);
				System.out.println("Ship number " + boat + " sent " + receivedList);
				checkRequest(receivedList.get(0));
				receivedList.clear();
			}

		} else {
			System.out.println("timeout");
			addTimeout(boat_id);

		}

	}

	private void addTimeout(Integer boat_id) {
		timeouts.put(boat_id, timeouts.getOrDefault(boat_id, 0) + 1);

		if (timeouts.get(boat_id) >= ProtocolProperties.TIMEOUTED_LOOP_LIMIT) addIdleBoat(boat_id);
	}

	// TODO Check status and give response to the boat
	public void checkRequest(Frame frame) {
		Status status = frame.getData().getStatus();
		String status_str = status.getStatus();
		String action_str = status.getAction();
		Ship ship = new Ship(frame.getOriginId());

		if (status_str.equals(StatusType.PARKING.toString()) && action_str.equals(ActionType.LEAVE.toString())) {
			boolean okay = port.addToTransitionZone(ship, action_str);

			//TODO Taking into account if there's place in the transition zone(okay), send the response to the boat

		} else if (status_str.equals(StatusType.TRANSIT.toString())) {
			// TODO Here we should give the permission to leave or enter, taking what's asked.

		} else if (status_str.equals(StatusType.SEA.toString()) && action_str.equals(ActionType.ENTER.toString())) {
			Mooring freeMooring = port.getFreeMooring(ship);
			// TODO We have to send the id of this mooring in the data
			boolean okay = port.addToTransitionZone(ship, action_str);
			//TODO Taking into account if there's place in the transition zone(okay), send the response to the boat


		}

	}

	private void addConnectedBoat(Integer boat) {
		connectedBoats.add(boat);
		idleBoats.remove(boat);
		timeouts.put(boat, 0);
	}

	private void addIdleBoat(Integer boat) {
		System.out.println("Idle boat added: " + boat);
		idleBoats.add(boat);
		connectedBoats.remove(boat);
	}

	@Override
	public void update(Observable o, Object arg) {
		System.out.println(arg);
		receivedList.add((Frame) arg);
	}

	@Override
	public void run() {
		while (true) {
			// This loop is repeated x times before calling the discovery function.
			for (int i = 0; i < ProtocolProperties.LOOP_IDDLE_BOATS; i++) {

				for (int j = 0; j < ProtocolProperties.LOOP_CONNECTED_BOATS; j++) {
					for (Integer boat : connectedBoats) {

						controllerIokse(boat.toString());
					}
				}
				for (Integer boat : idleBoats) {

					controllerIokse(boat.toString());
				}
			}


		}
	}
}
