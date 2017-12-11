package protocol;

public final class ProtocolProperties {

	public static final int START_FRAME = 3;
	public static final int PACKET_TYPE = 2;
	public static final int COUNTER = 3;
	public static final int ORIGIN_ID = 8;
	public static final int DESTINATION_ID = 8;
	public static final int LENGTH = 8;
	public static final int CHECKSUM = 8;
	// TODO This has to be defined
	public static final long TIMEOUT = 100000000;

	public enum DataType {
		STATUS {
			@Override
			public String toString() {
				return "00";
			}
		},
		REQUEST {
			@Override
			public String toString() {
				return "01";
			}
		},
		RESPONSE {
			@Override
			public String toString() {
				return "10";
			}
		},
		NULL {
			@Override
			public String toString() {
				return "";
			}
		}

	}

	public enum PacketType {
		DISCOVERY {
			@Override
			public String toString() {
				return "00";
			}
		},
		ACK {
			@Override
			public String toString() {
				return "01";
			}
		},
		DATA {
			@Override
			public String toString() {
				return "10";
			}
		},
		TOKEN {
			@Override
			public String toString() {
				return "11";
			}
		}
	}

	public static final String MASTER_ID = "00000000";


}
