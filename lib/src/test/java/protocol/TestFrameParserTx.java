package protocol;

import models.Data;
import models.Frame;
import models.Header;
import models.Status;
import org.junit.Test;
import serial.Serial;

import java.util.List;

public class TestFrameParserTx {

    Frame frame;
    Serial serial;
    List<String> stringList;

    @Test
    public void testFrameParserTx() {
        frame = new Frame(new Header("010", "00", "000"),
                "00000000", "11111111", "00000001",
                new Data("01", new Status("110", "011")), "11111111");
        serial = new Serial();
        stringList = FrameParser.parseTx(frame);
        System.out.println(stringList);
    }
}
