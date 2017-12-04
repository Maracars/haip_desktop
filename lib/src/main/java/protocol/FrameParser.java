package protocol;

import java.util.ArrayList;
import java.util.List;

import models.Frame;
import serial.Serial;

public class FrameParser{	
	
	Serial serialTx;
	int bytesCounter;
	List<FrameFilter> filters;
	Frame frame;
	
	public FrameParser(Serial serialTx) {
		this.serialTx = serialTx;
		bytesCounter = 0;
		frame = new Frame();
		filters = new ArrayList<FrameFilter>();
		initializeFilters();
	}

	public void initializeFilters() {
		filters.add(new HeaderFilter());
		IdentificationFilter idFilter = new IdentificationFilter();
		filters.add(idFilter);
		filters.add(idFilter);
		filters.add(new LengthFilter());
		//filters.add(new DataFilter());
		filters.add(new ChecksumFilter());
	}
	
	public void parseRx(String byteString) {
		frame = filters.get(bytesCounter).parseRx(frame, byteString);

		if(!filters.get(bytesCounter).filter(frame)) {
			//Si algún filtro falla, que hacemos?
			bytesCounter = 0;
			frame = new Frame();
		}
		
		//Falta por ver como parseamos el DATA
		
		if(frame.getChecksum() != null) {
			//Terminamos de recibir el paquete.
			bytesCounter = 0;
			//Llamar al node logic
			//frame = new Frame();
		}else{
			bytesCounter++;
		}
		
	}
	
	public void parseTx(Frame frame) {
		//Parse transmitted frame
		String fullString = frame.toString();
		List<String> stringList = splitStringByNumber(fullString, 8);
	}

	List<String> splitStringByNumber(String string, int subStringLength) {
		List<String> strings = new ArrayList<>();
		int index = 0;
		while (index < string.length()) {
			strings.add(string.substring(index, Math.min(index + subStringLength, string.length())));
			index += subStringLength;
		}
		return strings;
	}

	public Frame getFrame() {
		return frame;
	}

}
