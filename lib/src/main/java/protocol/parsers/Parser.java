package protocol.parsers;

import java.util.List;

import models.Frame;

public interface Parser {
	
	public Frame parseRx(Frame frame, String byteString);
	
	public List<String> parseTx(Frame frame, List<String> byteList);

}
