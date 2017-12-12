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

	@SuppressWarnings("unchecked")
	public NodeLogic(Serial serial) {
		this.serial = serial;
		receivedList = Collections.synchronizedList(new ArrayList());
		connectedBoats = new ArrayList<>();
		connectedBoats.add(1);
	}

	public void controllerIokse(String dest) {

		// TODO This is going to be called for each boat, here we should have a list of connected boats, those that are iddle...
		Frame fr = createToken(ProtocolProperties.MASTER_ID, dest);
		sendParsedFrame(fr);

		long count = 0;
		while (count++ < ProtocolProperties.TIMEOUT && receivedList.isEmpty()) {
		}
		if (!receivedList.isEmpty()) {
			//TODO Here we must send the response to the request.
			System.out.println("Ship number " + dest + " sent " + receivedList);
			checkRequest(receivedList);
			receivedList.clear();
		} else {
			System.out.println("timeout");
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

	public Frame createAck(String origin, String dest) {
		return createFrame(PacketType.ACK, origin, dest);
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
		// TODO The start frame has to be set and also how counter's work
		Header header = new Header("101", type.toString(), "000");
		Frame frame = new Frame(header, origin, dest, Helpers.strLenToBin(data.toString()), data, CRC8.toCRC8(data.toString()));

		return frame;
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
			for (Integer boat : connectedBoats) {

				controllerIokse(boat.toString());
			}

		}
	}
}
