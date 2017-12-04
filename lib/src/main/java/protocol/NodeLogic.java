package protocol;

import helpers.CRC8;
import models.Frame;
import models.Header;

import static protocol.ProtocolProperties.COUNTER;
import static protocol.ProtocolProperties.PACKET_TYPE;
import static protocol.ProtocolProperties.START_FRAME;
import static protocol.ProtocolProperties.PacketType;

public class NodeLogic {


    public Frame sendAcknowledgment(Frame frame, String byteString, String dest, String origin) {
        CRC8 crc8 = new CRC8();
        crc8.reset();
        crc8.update("000".getBytes());

        Header header = new Header();
        header.setStartFrame(byteString.substring(0, START_FRAME - 1));
        header.setPacketType(PacketType.ACK.toString());
        header.setCounter(byteString.substring(PACKET_TYPE, COUNTER - 1));
        frame.setLength("00000000");
        frame.setHeader(header);
        frame.setDestinationId(dest);
        //frame.getOriginId(origin);
        // TODO Checksum function to call
        frame.setChecksum("" + crc8.getValue());

        return frame;
    }

}
