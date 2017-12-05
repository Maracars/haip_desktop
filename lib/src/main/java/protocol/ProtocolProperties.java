package protocol;

public final class ProtocolProperties {

    public static final int START_FRAME = 3;
    public static final int PACKET_TYPE = 2;
    public static final int COUNTER = 3;
    public static final int ORIGIN_ID = 8;
    public static final int DESTINATION_ID = 8;
    public static final int LENGTH = 8;
    public static final int CHECKSUM = 8;
    
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
        }
    }
    public static final String MASTER_ID = "00000000";

    // TODO This have to been deleted
    public static final String DISCOVERY = "00";
    public static final String ACK = "01";
    public static final String DATA = "10";
}
