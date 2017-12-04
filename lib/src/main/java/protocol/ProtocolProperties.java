package protocol;

public final class ProtocolProperties {

    public static final int START_FRAME = 3;
    public static final int PACKET_TYPE = 2;
    public static final int COUNTER = 3;
    public static final int ORIGIN_ID = 8;
    public static final int DESTINATION_ID = 8;
    public static final int LENGTH = 8;
    public static final int CHECKSUM = 8;

    public enum PacketType {
        DISCOVERY {
            @Override
            public String toString() {
                return "";
            }
        },
        ACK {
            @Override
            public String toString() {
                return "bronze";
            }
        },
        DATA {
            @Override
            public String toString() {
                return "silver";
            }
        }
    }
    public static final String MASTER_ID = "00000000";

    // TODO This have to been deleted
    public static final String DISCOVERY = "00";
    public static final String ACK = "01";
    public static final String DATA = "10";
}
