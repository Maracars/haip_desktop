package models;

public class Data {

	//Request = 00
	//Status = 01
	//Response = 10
	String type;
	Status status;

	public String getType() {
		return type;
	}
	public void setType(String startFrame) {
		this.type = type;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(String packetType) {
		this.status = status;
	}

	@Override
	public String toString() {
		return type + status.toString();
	}
}
