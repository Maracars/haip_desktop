package protocol;

import helpers.CRC8;
import helpers.Helpers;
import models.Data;
import models.Frame;
import models.Header;
import models.Status;
import protocol.ProtocolProperties.*;

import static protocol.ProtocolProperties.*;

public class FrameCreator {


	public static Frame createToken(String origin, String dest) {
		return createFrame(PacketType.TOKEN, origin, dest);
	}

	public static Frame createDiscovery(String origin, String dest) {
		return createFrame(PacketType.DISCOVERY, origin, dest);
	}

	public static Frame createAck(String origin, String dest) {
		return createFrame(PacketType.ACK, origin, dest);
	}


	// TODO These two functions can be set into one, but like this may be more legible
	public static Frame createRequest(String origin, String dest, Status status) {
		return createFrame(status, PacketType.DATA, DataType.REQUEST, origin, dest);
	}

	public static Frame createResponse(String origin, String dest, Status status) {
		return createFrame(status, PacketType.DATA, DataType.RESPONSE, origin, dest);
	}


	public static Frame createFrame(PacketType type, String origin, String dest) {

		return createFrame(new Status("", "", ""), type, DataType.NULL, origin, dest);
	}


	public static Frame createFrame(Status status, PacketType type, DataType dataType, String origin, String dest) {

		Data data = new Data(dataType.toString(), status);
		String dataStr = Helpers.toByteBinString(data.toString());

		// TODO We have think about how the counter work
		Header header = new Header(ProtocolProperties.START_FRAME_VALUE, type.toString(), "000");
		Frame frame = new Frame(header, origin, dest, Helpers.toByteBinString("" + dataStr.length()), data);
		String packet = frame.toString().substring(0, HEADER + ORIGIN_ID + DESTINATION_ID + LENGTH + dataStr.length());
		String checksum = CRC8.toCRC8(packet);
		frame.setChecksum(checksum);
		return frame;
	}
}
