package protocol;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import models.Frame;
import serial.Serial;

public class FrameParser{	

	Serial serial;
	int bytesCounter;
	Frame frame;
	List<FrameFilter> filters;
	
	public FrameParser(Serial serial) {
		this.serial = serial;
		bytesCounter = 0;
		frame = new Frame();
		filters = new ArrayList<>();
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
	
	public List<String> parseTx(Frame frame) {
		// Parse transmitted frame
		String fullString = frame.toString();

		// Divide in bytes (substrings of length = 8)
		List<String> stringList = splitStringByNumber(fullString, 8);

		// Fill with zeros to get a full byte
		for (final ListIterator<String> i = stringList.listIterator(); i.hasNext();) {
			final String element = i.next();
			i.set(fillWithZeros(element));
		}

		return stringList;
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

	public String fillWithZeros(String binaryString) {
		if (binaryString.length() < 8) {
			for (int i = binaryString.length(); i < 8; i++) {
				binaryString = "0" + binaryString;
			}
		}
		return binaryString;
	}

	public Frame getFrame() {
		return frame;
	}

}
