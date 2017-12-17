package models;

public class Header {
	// 3 bits
	private String length;
	// 2 bits
	private String packetType;
	// 3 bits
	private String counter;

	public Header(String length, String packetType, String counter) {
		this.length = length;
		this.packetType = packetType;
		this.counter = counter;
	}

	public Header() {
	}

	public String getLength() {
		return length;
	}

	public void setLength(String length) {
		this.length = length;
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
		return length + packetType + counter;
	}

}
