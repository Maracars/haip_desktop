package models;

public class Frame {

	private Header header;
	private String originId;
	private String destinationId;
	private Data data;
	private String checksum;

	public Frame(Header header, String originId, String destinationId, Data data, String checksum) {
		this.header = header;
		this.originId = originId;
		this.destinationId = destinationId;
		this.data = data;
		this.checksum = checksum;
	}

	public Frame(Header header, String originId, String destinationId, Data data) {
		this.header = header;
		this.originId = originId;
		this.destinationId = destinationId;
		this.data = data;
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
		return header.toString() + originId + destinationId + ((data == null) ? "" : data.toString()) + ((checksum == null) ? "" : checksum);
	}


}
