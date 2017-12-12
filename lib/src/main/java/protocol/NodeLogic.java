package protocol;

import helpers.CRC8;
import helpers.Helpers;
import models.Data;
import models.Frame;
import models.Header;
import models.Status;
import serial.Serial;

import java.util.*;

import static protocol.ProtocolProperties.DataType;
import static protocol.ProtocolProperties.PacketType;

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
		Frame fr = createToken(ProtocolProperties.MASTER_ID, Helpers.toByteBinString(boat));
		sendParsedFrame(fr);

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


	public void sendParsedFrame(Frame frame) {
		List<String> listBytes = FrameParser.parseTx(frame);
		try {

			serial.writeStrings(listBytes);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public Frame createToken(String origin, String dest) {
		return createFrame(PacketType.TOKEN, origin, dest);
	}

	public Frame createDiscovery(String origin, String dest) {
		return createFrame(PacketType.DISCOVERY, origin, dest);
	}


	// TODO These two functions can be set into one, but like this may be more legible
	public Frame createRequest(String origin, String dest, Status status) {
		return createFrame(status, PacketType.DATA, DataType.REQUEST, origin, dest);
	}

	public Frame createResponse(String origin, String dest, Status status) {
		return createFrame(status, PacketType.DATA, DataType.RESPONSE, origin, dest);
	}


	public Frame createFrame(PacketType type, String origin, String dest) {

		return createFrame(new Status("", "", ""), type, DataType.NULL, origin, dest);
	}


	public Frame createFrame(Status status, PacketType type, DataType dataType, String origin, String dest) {

		Data data = new Data(dataType.toString(), status);
		String dataStr = Helpers.toByteBinString(data.toString());
		// TODO We have think about how the counter work
		Header header = new Header(ProtocolProperties.START_FRAME_VALUE, type.toString(), "000");
		String checksum = Helpers.toByteBinString(CRC8.toCRC8(dataStr));
		Frame frame = new Frame(header, origin, dest, Helpers.toByteBinString("" + dataStr.length()), data, checksum);

		return frame;
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
		receivedList.add(arg.toString());
		Frame fr = createToken(ProtocolProperties.MASTER_ID, arg.toString());

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
