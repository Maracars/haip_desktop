package models;

public class Data {

	//Request = 00
	//Status = 01
	//Response = 10
	private String type;
	private Status status;

	public Data() {
	}

	public Data(String type, Status status) {
		this.type = type;
		this.status = status;
	}


	public Data (String type, Status status) {
		this.type = type;
		this.status = status;
	}

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return type + status.toString();
	}
}
