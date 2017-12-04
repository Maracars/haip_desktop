package protocol;

import java.util.ArrayList;
import java.util.List;

import models.Frame;
import static protocol.ProtocolProperties.DISCOVERY;
import static protocol.ProtocolProperties.MASTER_ID;

public class IdentificationFilter implements FrameFilter{
	
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
	public boolean filter(Frame frame) {
		boolean filter = filters.get(bytesCounter).filter(frame);
		bytesCounter++;
		return filter;
	}

	public void initializeFilters() {
		filters.add(new OriginFilter());
		filters.add(new DestinationFilter());
	}
	
	public class OriginFilter implements FrameFilter{

		@Override
		public Frame parseRx(Frame frame, String byteString) {
			frame.setOriginId(byteString);
			return frame;
		}

		@Override
		public boolean filter(Frame frame) {
			//Check if the packet is a discovery packet and is sent by master
			if(frame.getHeader().getPacketType().equals(DISCOVERY) && 
					!frame.getOriginId().equals(MASTER_ID))
				return false;
			return true;
		}
		
	}
	
	public class DestinationFilter implements FrameFilter{

		@Override
		public Frame parseRx(Frame frame, String byteString) {
			frame.setDestinationId(byteString);
			return frame;
		}

		@Override
		public boolean filter(Frame frame) {
			//Check if both origin and destination are not equal
			if(frame.getOriginId().equals(frame.getDestinationId()))
				return false;
			return true;
		}
		
	}


}
