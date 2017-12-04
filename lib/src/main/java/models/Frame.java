package models;

public class Frame {
	
	Header header;
	String originId;
	String destinationId;
	String length;
	Data data;
	String checksum;

	public Frame(Header header, String originId, String destinationId, String length, Data data, String checksum) {
		this.header = header;
		this.originId = originId;
		this.destinationId = destinationId;
		this.length = length;
		this.data = data;
		this.checksum = checksum;
	}

	public Frame() {
	}

	public Header getHeader() {
		return header;
	}
	public void setHeader(Header header) {
		this.header = header;
	}
	public String getOriginId() {
		return originId;
	}
	public void setOriginId(String originId) {
		this.originId = originId;
	}
	public String getDestinationId() {
		return destinationId;
	}
	public void setDestinationId(String destinationId) {
		this.destinationId = destinationId;
	}
	public String getLength() {
		return length;
	}
	public void setLength(String length) {
		this.length = length;
	}
	public Data getData() {
		return data;
	}
	public void setData(Data data) {
		this.data = data;
	}
	public String getChecksum() {
		return checksum;
	}
	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

	@Override
	public String toString() {
		return header.toString() + originId + destinationId + length + data.toString() + checksum;
	}

}
