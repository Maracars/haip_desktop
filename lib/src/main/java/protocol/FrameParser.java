package protocol;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import models.Frame;

public class FrameParser {

	private static final String MAX_LENGTH = "11111111";
	static Frame frame;
	private static List<FrameFilter> filters;

	// TODO These should be parameters...
	static int bytesCounter;
	static int dataCounter;

	static {
		bytesCounter = 0;
		dataCounter = 0;
		frame = new Frame();
		filters = new ArrayList<>();
		initializeFilters();
	}

	private static void initializeFilters() {
		filters.add(new HeaderFilter());
		IdentificationFilter idFilter = new IdentificationFilter();
		filters.add(idFilter);
		filters.add(idFilter);
		filters.add(new LengthFilter());
		filters.add(new DataFilter());
		filters.add(new ChecksumFilter());
	}

	public static void parseRx(String byteString) {
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

	private static void checkPacketData(String byteString) {
		if (frame.getLength() != null) {
			if (dataCounter < Integer.parseInt(frame.getLength(), 2)) {
				dataCounter++;
			} else {
				bytesCounter++;
			}
		}
	}

	private static void resetCommunication() {
		bytesCounter = 0;
		dataCounter = 0;
		frame = new Frame();
	}

	private static void checkPacketFinal() {
		if (frame.getChecksum() != null) {
			// notifyNodeLogic();
			resetCommunication();
		} else {
			if (dataCounter > Integer.parseInt(frame.getLength() == null ? MAX_LENGTH : frame.getLength(), 2)
					|| dataCounter == 0) {
				bytesCounter++;
			}
		}
	}


	private static void parseData(String byteString) {
		if (dataCounter != 0) {
			frame = filters.get(bytesCounter + 1).parseRx(frame, byteString);
		} else {
			frame = filters.get(bytesCounter).parseRx(frame, byteString);
		}
	}

	private static boolean filterData() {
		return filters.get(bytesCounter).filter(frame);
	}

	public static List<String> parseTx(Frame frame) {
		List<String> byteList = new ArrayList<>();
		for (FrameFilter filter : filters) {
			byteList = filter.parseTx(frame, byteList);
		}
		return byteList;
	}

	public static Frame getFrame() {
		return frame;
	}


}
