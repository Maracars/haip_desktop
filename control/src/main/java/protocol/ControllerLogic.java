package protocol;

import helpers.Helpers;
import models.Frame;
import models.Status;
import serial.Serial;

import java.util.*;

import static protocol.ProtocolProperties.ActionType;
import static protocol.ProtocolProperties.StatusType;

// TODO These functions have been done here. Why? Idk, but have to be moved somewhere else. Where? Idk.
public class ControllerLogic implements Observer, Runnable {

	private Serial serial;
	private List<Frame> receivedList;
	private ArrayList<Integer> connectedBoats;
	private ArrayList<Integer> iddledBoats;
	private HashMap<Integer, Integer> timeouts;

	@SuppressWarnings("unchecked")
	public ControllerLogic(Serial serial) {
		this.serial = serial;
		receivedList = Collections.synchronizedList(new ArrayList());
		connectedBoats = new ArrayList<>();
		iddledBoats = new ArrayList<>();
		timeouts = new HashMap<>();
		connectedBoats.add(1);
	}

	public void controllerIokse(String boat) {

		Integer boat_id = Integer.parseInt(boat);
		Frame fr = FrameCreator.createToken(ProtocolProperties.MASTER_ID, Helpers.toByteBinString(boat));
		Helpers.sendParsedFrame(fr, serial);

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
				if (iddledBoats.contains(boat_id)) addConnectedBoat(boat_id);
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

		if (timeouts.get(boat_id) >= ProtocolProperties.TIMEOUTED_LOOP_LIMIT) addIddleBoat(boat_id);
	}

	// TODO Check status and give response to the boat
	public void checkRequest(Frame frame) {
		Status status = frame.getData().getStatus();
		String status_str = status.getStatus();
		String action_str = status.getAction();

		if (status_str.equals(StatusType.PARKING.toString()) && action_str.equals(ActionType.LEAVE.toString())) {


		} else if (status_str.equals(StatusType.TRANSIT.toString())) {

		} else if (status_str.equals(StatusType.SEA.toString()) && action_str.equals(ActionType.ENTER.toString())) {

		}

	}

	private void addConnectedBoat(Integer boat) {
		connectedBoats.add(boat);
		iddledBoats.remove(boat);
		timeouts.put(boat, 0);
	}

	private void addIddleBoat(Integer boat) {
		System.out.println("Iddle boat added: " + boat);
		iddledBoats.add(boat);
		connectedBoats.remove(boat);
	}

	@Override
	public void update(Observable o, Object arg) {
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
				for (Integer boat : iddledBoats) {

					controllerIokse(boat.toString());
				}
			}


		}
	}
}
