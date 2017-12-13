package protocol;

public final class ProtocolProperties {

	public static final int START_FRAME = 3;
	public static final int HEADER = 8;
	public static final int PACKET_TYPE = 2;
	public static final int COUNTER = 3;
	public static final int ORIGIN_ID = 8;
	public static final int DESTINATION_ID = 8;
	public static final int LENGTH = 8;
	public static final int INF = 8;
	public static final int CHECKSUM = 8;
	// TODO This has to be defined
	public static final long TIMEOUT = 1000000000;
	public static final int TYPE = 2;
	public static final int STATUS = 2;
	public static final int ACTION = 2;
	public static final int PERMISSION = 2;
	public static final int PARKING = 8;

	public static final String START_FRAME_VALUE = "101";
	public static final int LOOP_CONNECTED_BOATS = 3;
	public static final int LOOP_IDDLE_BOATS = 2;
	public static final int TIMEOUTED_LOOP_LIMIT = 5;


	public enum DataType {
		STATUS("00"),
		REQUEST("01"),
		RESPONSE("10"),
		NULL("");
		private String string;

		DataType(String name) {
			string = name;
		}

		@Override
		public String toString() {
			return string;
		}
	}

	public enum PacketType {
		DISCOVERY("00"),
		ACK("01"),
		DATA("10"),
		TOKEN("11");
		private String string;

		PacketType(String name) {
			string = name;
		}

		@Override
		public String toString() {
			return string;
		}
	}

	public enum ActionType {
		IDDLE("10"),
		ENTER("00"),
		LEAVE("01");
		private String string;

		ActionType(String name) {
			string = name;
		}

		@Override
		public String toString() {
			return string;
		}
		
		public String getName() {
			return this.name();
		}
	}

	public enum StatusType {
		PARKING("00"),
		TRANSIT("01"),
		SEA("10");
		private String string;

		StatusType(String name) {
			string = name;
		}

		@Override
		public String toString() {
			return string;
		}
	}

	public enum PermissionType {
		DENY("00"),
		ALLOW("11");
		private String string;

		PermissionType(String name) {
			string = name;
		}

		@Override
		public String toString() {
			return string;
		}
	}

	public static final String MASTER_ID = "00000000";


}
