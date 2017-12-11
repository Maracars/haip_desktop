package protocol;

import java.util.ArrayList;
import java.util.List;

import models.Frame;

public class FrameParser {

	private static final String MAX_LENGTH = "11111111";
	public static final int FIN_PACKET = 1;
	public static final int BAD_PACKET = -1;
	public static final int UNFIN_PACKET = 0;
	public static Frame frame;
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
		filters.add(new OriginFilter());
		filters.add(new DestinationFilter());
		filters.add(new LengthFilter());
		filters.add(new DataFilter());
		filters.add(new ChecksumFilter());
	}

	public static int parseRx(String byteString) {
		parseData(byteString);

		if (!filterData()) {
			//Si algún filtro falla, que hacemos?
			resetCommunication();
			return BAD_PACKET;
		}

		checkPacketData(byteString);
		return checkPacketFinal();
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

	public static void resetCommunication() {
		bytesCounter = 0;
		dataCounter = 0;
		frame = new Frame();
	}

	private static int checkPacketFinal() {
		if (frame.getChecksum() != null) {
			// notifyNodeLogic();
			//resetCommunication();
			return FIN_PACKET;
		} else {
			if (dataCounter > Integer.parseInt(frame.getLength() == null ? MAX_LENGTH : frame.getLength(), 2)
					|| dataCounter == 0) {
				bytesCounter++;
			}
		}
		return UNFIN_PACKET;
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

}
