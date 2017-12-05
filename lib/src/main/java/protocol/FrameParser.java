package protocol;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import models.Frame;
import serial.Serial;

public class FrameParser extends Observable {

	private static final String MAX_LENGTH = "11111111";
	Serial serial;
	int bytesCounter;
	Frame frame;
	int dataCounter;
	private static List<FrameFilter> filters;

	public FrameParser(Serial serial) {
		this.serial = serial;
		bytesCounter = 0;
		dataCounter = 0;
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
		filters.add(new DataFilter());
		filters.add(new ChecksumFilter());
	}

	public void parseRx(String byteString) {
		parseData(byteString);

		if (!filterData()) {
			//Si algún filtro falla, que hacemos?
			bytesCounter = 0;
			dataCounter = 0;
			frame = new Frame();
		}

		checkPacketData(byteString);

		checkPacketFinal();
	}

	public void checkPacketData(String byteString) {
		if (frame.getLength() != null) {
			if (dataCounter < Integer.parseInt(frame.getLength(), 2)) {
				dataCounter++;
			} else {
				bytesCounter++;
			}
		}
	}

	public void resetCommunication() {
		bytesCounter = 0;
		dataCounter = 0;
		frame = new Frame();
	}

	public void checkPacketFinal() {
		if (frame.getChecksum() != null) {
			notifyNodeLogic();
			resetCommunication();
		} else {
			if (dataCounter > Integer.parseInt(frame.getLength() == null ? MAX_LENGTH : frame.getLength(), 2)
					|| dataCounter == 0) {
				bytesCounter++;
			}
		}
	}
	
	public void notifyNodeLogic() {
		this.setChanged();
		this.notifyObservers(frame);
	}

	public void parseData(String byteString) {
		if (dataCounter != 0) {
			frame = filters.get(bytesCounter + 1).parseRx(frame, byteString);
		} else {
			frame = filters.get(bytesCounter).parseRx(frame, byteString);
		}
	}

	public boolean filterData() {
		return filters.get(bytesCounter).filter(frame);
	}

	public static List<String> parseTx(Frame frame) {
		List<String> byteList = new ArrayList<>();
		for(FrameFilter filter : filters) {
			byteList = filter.parseTx(frame, byteList);
		}
		return byteList;
	}

	public Frame getFrame() {
		return frame;
	}
	
	

}
