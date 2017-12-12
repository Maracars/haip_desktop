package protocol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import helpers.Helpers;
import models.Frame;
import serial.Serial;

// TODO These functions have been done here. Why? Idk, but have to be moved somewhere else. Where? Idk.
public class NodeLogic implements Observer, Runnable {

	private Serial serial;
	private List receivedList;
	private ArrayList<Integer> connectedBoats;
	private ArrayList<Integer> iddledBoats;
	private HashMap<Integer, Integer> timeouts;

	@SuppressWarnings("unchecked")
	public NodeLogic(Serial serial) {
		this.serial = serial;
		receivedList = Collections.synchronizedList(new ArrayList());
		connectedBoats = new ArrayList<>();
		iddledBoats = new ArrayList<>();
		timeouts = new HashMap<>();
		connectedBoats.add(1);
	}

	public void controllerIokse(String boat) {

		Integer boat_id = Integer.parseInt(boat);
		// TODO This is going to be called for each boat, here we should have a list of connected boats, those that are iddle...
		Frame fr = FrameCreator.createToken(ProtocolProperties.MASTER_ID, Helpers.toByteBinString(boat));
		Helpers.sendParsedFrame(fr, serial);

		long count = 0;
		while (count++ < ProtocolProperties.TIMEOUT && receivedList.isEmpty()) {
		}
		if (!receivedList.isEmpty()) {
			//TODO Here we must send the response to the request.
			if (iddledBoats.contains(boat_id)) addConnectedBoat(boat_id);
			System.out.println("Ship number " + boat + " sent " + receivedList);
			checkRequest(receivedList);
			receivedList.clear();
		} else {
			System.out.println("timeout");
			timeouts.put(boat_id, timeouts.getOrDefault(boat_id, 0) + 1);

			if (timeouts.get(boat_id) >= ProtocolProperties.TIMEOUTED_LOOP_LIMIT) addIddleBoat(boat_id);
		}

	}

	// TODO Check status and give response to the boat
	public void checkRequest(List receivedList) {

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
		if (arg.getClass().equals(String.class)) {
			receivedList.add(arg.toString());
		}
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
