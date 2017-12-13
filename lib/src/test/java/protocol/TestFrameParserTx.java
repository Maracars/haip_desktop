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
    List<String> stringList;

    @Test
    public void testFrameParserTx() {
        frame = new Frame(new Header("101", "01", "000"),
                "10101010", "00000000", "00000001",
                new Data("01", new Status("10", "01", "11")), "01001100");

        stringList = FrameParser.parseTx(frame);
        System.out.println(stringList);
    }
}
