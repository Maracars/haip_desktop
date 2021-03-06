package protocol;

public final class ProtocolProperties {

	public static final int HEADER = 8;
	public static final int LENGTH = 3;
	public static final int PACKET_TYPE = 2;
	public static final int COUNTER = 3;

	public static final int ORIGIN_ID = 8;
	public static final int DESTINATION_ID = 8;

	public static final int TYPE = 2;
	public static final int STATUS = 2;
	public static final int ACTION = 2;
	public static final int PERMISSION = 2;
	public static final int PARKING = 8;
	public static final int CHECKSUM = 8;
	public static final int TIME_WINDOW = 8;
	static final long TOKEN_TIMEOUT = 1000;

	public static final String MASTER_ID = "00000000";
	static final String BROADCAST = "11111111";

	static final int LOOP_CONNECTED_BOATS = 5;
	static final int LOOP_IDLE_BOATS = 10;
	static final int IDLE_TIMEOUT = 2;
	static final int DISCONNECT_TIMEOUT = 10;
	static final int ACK_TIME_SLOT = 5;

	public enum PacketType {
		DISCOVERY("00"),
		ACK("01"),
		DATA("10"),
		TOKEN("11");
		private String string;

		PacketType(String name) {
			string = name;
		}

		public static PacketType getName(String value) {
			for (PacketType pt : PacketType.values()) {
				if (value.equals(pt.toString())) {
					return pt;
				}
			}
			return null;
		}

		@Override
		public String toString() {
			return string;
		}
	}

	public enum DataType {
		STATUS("00"),
		REQUEST("01"),
		RESPONSE("10"),
		NULL("");
		private String string;

		DataType(String name) {
			string = name;
		}

		public static DataType getName(String value) {
			for (DataType dt : DataType.values()) {
				if (value.equals(dt.toString())) {
					return dt;
				}
			}
			return null;
		}

		@Override
		public String toString() {
			return string;
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

		public static StatusType getName(String value) {
			for (StatusType st : StatusType.values()) {
				if (value.equals(st.toString())) {
					return st;
				}
			}
			return null;
		}

		@Override
		public String toString() {
			return string;
		}
	}

	public enum ActionType {
		IDLE("10"),
		ENTER("00"),
		LEAVE("01");
		private String string;

		ActionType(String name) {
			string = name;
		}

		public static ActionType getName(String value) {
			for (ActionType at : ActionType.values()) {
				if (value.equals(at.toString())) {
					return at;
				}
			}
			return null;
		}

		@Override
		public String toString() {
			return string;
		}
	}

	public enum PermissionType {
		DENY("00"),
		ALLOW("11"),
		ASK("10"),
		INVALID("01");

		private String string;

		PermissionType(String name) {
			string = name;
		}

		public static PermissionType getName(String value) {
			for (PermissionType pt : PermissionType.values()) {
				if (value.equals(pt.toString())) {
					return pt;
				}
			}
			return null;
		}

		@Override
		public String toString() {
			return string;
		}

	}


}
