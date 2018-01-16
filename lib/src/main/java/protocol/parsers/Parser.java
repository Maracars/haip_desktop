package protocol.parsers;

import models.Frame;

import java.util.List;

public interface Parser {
	Frame parseRx(Frame frame, String byteString);

	List<Byte> parseTx(Frame frame, List<Byte> byteList);
}
