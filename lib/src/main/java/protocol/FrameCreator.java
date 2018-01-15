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

	public static Frame createDiscovery() {
		return createFrame(PacketType.DISCOVERY, MASTER_ID, BROADCAST,
				new Data(Helpers.toNbitBinaryString(String.valueOf(ACK_TIME_SLOT), 8)));
	}

	public static Frame createAck(String origin, String dest) {
		return createFrame(PacketType.ACK, origin, dest);
	}

	// TODO These two functions can be set into one, but like this may be more legible
	public static Frame createRequest(String origin, String dest, Status status) {
		return createFrame(PacketType.DATA, origin, dest, DataType.REQUEST, status);
	}

	public static Frame createStatus(String origin, String dest, Status status) {
		return createFrame(PacketType.DATA, origin, dest, DataType.STATUS, status);
	}

	public static Frame createResponse(String origin, String dest, Status status) {
		return createFrame(PacketType.DATA, origin, dest, DataType.RESPONSE, status);
	}

	public static Frame createResponse(String origin, String dest, Status status, String parking) {
		return createFrame(PacketType.DATA, origin, dest, DataType.RESPONSE, status, parking);
	}

	// Empty data
	private static Frame createFrame(PacketType packetType, String origin, String dest) {
		return createFrame(packetType, origin, dest, DataType.NULL, new Status("", "", ""));
	}

	// Default data
	private static Frame createFrame(PacketType packetType, String origin, String dest, DataType dataType, Status status) {
		return createFrame(packetType, origin, dest, dataType, status, null);
	}

	// Data + Parking
	private static Frame createFrame(PacketType packetType, String origin, String dest, DataType dataType, Status status, String parking) {
		return createFrame(packetType, origin, dest, new Data(dataType.toString(), status, parking));
	}

	private static Frame createFrame(PacketType packetType, String origin, String dest, Data data) {
		String dataStr = data.toString();

		// TODO We have to think about how the counter work
		Header header = new Header(Helpers.toNbitBinaryString("" + dataStr.length() / 8, 3), packetType.toString(), "000");
		Frame frame = new Frame(header, origin, dest, data);
		String checksum = CRC8.toCRC8(frame.toString());
		frame.setChecksum(checksum);
		return frame;
	}
}
