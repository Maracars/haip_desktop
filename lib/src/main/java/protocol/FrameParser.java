package protocol;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import models.Frame;

public class FrameParser {

	public static final int FIN_PACKET = 1;
	public static final int BAD_PACKET = -1;
	public static final int UNFIN_PACKET = 0;
	public static Frame frame;
	private static List<FrameFilter> filters;

	static {
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

		if(!checkPacketSize(byteString))
			return UNFIN_PACKET;

		parseData(byteString);

		if (!filterData()) {
			resetCommunication();
			return BAD_PACKET;
		}
		
		return checkPacketFinal();
	}

	private static boolean checkPacketSize(String byteString) {
		if(new BigInteger(byteString, 2).toByteArray().length >= 5) 
			return true;
		return false;	
	}

	public static void resetCommunication() {
		frame = new Frame();
	}

	private static int checkPacketFinal() {
		if (frame.getChecksum() != null) {
			return FIN_PACKET;
		}
		return UNFIN_PACKET;
	}

	private static void parseData(String byteString) {
		for (FrameFilter filter : filters) {
			frame = filter.parseRx(frame, byteString);
		}
	}

	private static boolean filterData() {
		boolean filtered = true;
		for (FrameFilter filter : filters) {
			if(!filter.filter(frame)) {
				filtered = false;
			}
		}
		return filtered;
	}

	public static List<String> parseTx(Frame frame) {
		List<String> byteList = new ArrayList<>();
		for (FrameFilter filter : filters) {
			byteList = filter.parseTx(frame, byteList);
		}
		return byteList;
	}  

}
