package protocol;

import helpers.CRC8;
import helpers.Helpers;
import models.Data;
import models.Frame;
import models.Header;
import models.Status;
import serial.Serial;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import static protocol.ProtocolProperties.DataType;
import static protocol.ProtocolProperties.PacketType;

// TODO These functions have been done here. Why? Idk, but have to be moved somewhere else. Where? Idk.
public class NodeLogic implements Observer {

	private Boolean received;
	private String packet;
	private ArrayList<String> lista;

	public NodeLogic() {
		lista = new ArrayList<>();
	}

	public void controllerIokse(Serial serial, String dest) {

		received = false;
		//TODO The observable is not still implemented, this would be a global variable in Serial, after parsing the txur, we change and notifyObservers
		// Gut exampol http://www.tutorialspoint.com/java/util/observable_addobserver.htm
		serial.addObserver(this);

		Frame fr = createToken(ProtocolProperties.MASTER_ID, dest);
		sendParsedFrame(fr);

		long count = 0;
		while (count < ProtocolProperties.TIMEOUT || received) {
			count++;
		}
		if (received) {
			//TODO Here we must send the response to the request.
			System.out.println("We have received the request of the boat");
		}

		checkRequest();

	}

	public void checkRequest() {
	}


	public void sendParsedFrame(Frame frame) {
		List<String> listBytes = FrameParser.parseTx(frame);
		try {
			System.out.println(listBytes);
			//Serial.writeBytes(listBytes);
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

		return createFrame(new Status("", ""), type, DataType.NULL, origin, dest);
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
		lista.add(arg.toString());
		System.out.println(lista);
		Frame fr = createToken(ProtocolProperties.MASTER_ID, arg.toString());

	}
}
