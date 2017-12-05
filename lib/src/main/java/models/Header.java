package models;

public class Header {

	// 3 bits
	String startFrame;
	// 2 bits
	String packetType;
	// 3 bits
	String counter;

	public Header(String startFrame, String packetType, String counter) {
		this.startFrame = startFrame;
		this.packetType = packetType;
		this.counter = counter;
	}

	public Header() {
	}

	public String getStartFrame() {
		return startFrame;
	}

	public void setStartFrame(String startFrame) {
		this.startFrame = startFrame;
	}

	public String getPacketType() {
		return packetType;
	}

	public void setPacketType(String packetType) {
		this.packetType = packetType;
	}

	public String getCounter() {
		return counter;
	}

	public void setCounter(String counter) {
		this.counter = counter;
	}

	@Override
	public String toString() {
		return startFrame + packetType + counter;
	}

}
