package protocol;

import models.Frame;

import java.util.ArrayList;
import java.util.List;

import static protocol.ProtocolProperties.MASTER_ID;
import static protocol.ProtocolProperties.PacketType;

public class IdentificationFilter implements FrameFilter {

	int bytesCounter;
	List<FrameFilter> filters;

	public IdentificationFilter() {
		bytesCounter = 0;
		filters = new ArrayList<>();
		initializeFilters();
	}

	@Override
	public Frame parseRx(Frame frame, String byteString) {
		frame = filters.get(bytesCounter).parseRx(frame, byteString);
		return frame;
	}
	
	@Override
	public List<String> parseTx(Frame frame, List<String> byteList) {
		byteList = filters.get(bytesCounter).parseTx(frame, byteList);
		bytesCounter++;
		return byteList;
	}

	@Override
	public boolean filter(Frame frame) {
		boolean filter = filters.get(bytesCounter).filter(frame);
		bytesCounter++;
		return filter;
	}

	public void initializeFilters() {
		filters.add(new OriginFilter());
		filters.add(new DestinationFilter());
	}
	


	public class OriginFilter implements FrameFilter {

		@Override
		public Frame parseRx(Frame frame, String byteString) {
			frame.setOriginId(byteString);
			return frame;
		}

		@Override
		public boolean filter(Frame frame) {
			//Check if the packet is a discovery packet and is sent by master
			if (frame.getHeader().getPacketType().equals(PacketType.DISCOVERY) &&
					!frame.getOriginId().equals(MASTER_ID))
				return false;
			return true;
		}

		@Override
		public List<String> parseTx(Frame frame, List<String> byteList) {
			byteList.add(frame.getOriginId().toString());
			return byteList;
		}

	}

	public class DestinationFilter implements FrameFilter {

		@Override
		public Frame parseRx(Frame frame, String byteString) {
			frame.setDestinationId(byteString);
			return frame;
		}

		@Override
		public boolean filter(Frame frame) {
			//Check if both origin and destination are not equal
			if (frame.getOriginId().equals(frame.getDestinationId()))
				return false;
			return true;
		}

		@Override
		public List<String> parseTx(Frame frame, List<String> byteList) {
			byteList.add(frame.getDestinationId());
			return byteList;
		}

	}

}
