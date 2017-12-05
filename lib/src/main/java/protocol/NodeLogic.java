package protocol;

import helpers.CRC8;
import helpers.Helpers;
import models.Data;
import models.Frame;
import models.Header;
import models.Status;

import static protocol.ProtocolProperties.PacketType;
import static protocol.ProtocolProperties.DataType;
// TODO These functions have been done here. Why? Idk, but have to be moved somewhere else. Where? Idk.
public class NodeLogic {


	public Frame createDiscovery(String origin, String dest, Status status) {
		return createFrame(status, PacketType.DISCOVERY, DataType.REQUEST, origin, dest);
	}

	// TODO These two functions can be set into one, but like this may be more legible
	public Frame createRequest(String origin, String dest, Status status) {
		return createFrame(status, PacketType.DATA, DataType.REQUEST, origin, dest);
	}

	public Frame createResponse(String origin, String dest, Status status) {
		return createFrame(status, PacketType.DATA, DataType.RESPONSE, origin, dest);
	}

	public Frame createAck(String origin, String dest) {
		return createFrame(PacketType.ACK, DataType.RESPONSE, origin, dest);
	}

	public Frame createFrame(PacketType type, DataType dataType, String origin, String dest) {

		return createFrame(new Status("", ""), type, dataType, origin, dest);
	}


	public Frame createFrame(Status status, PacketType type, DataType dataType, String origin, String dest) {

		Data data = new Data(dataType.toString(), status);
		// TODO The start frame has to be set and also how counter's work
		Header header = new Header("iokese", type.toString(), "00000000");
		Frame frame = new Frame(header, origin, dest, Helpers.strLenToBin(data.toString()), data, CRC8.toCRC8(data.toString()));

		return frame;
	}

}
