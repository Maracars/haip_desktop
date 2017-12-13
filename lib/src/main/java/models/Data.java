package models;

public class Data {

	private String type;
	private Status status;
	private String parking;

	public Data() {}

	public Data(String type, Status status) {
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

	public String getParking() {
		return parking;
	}

	public void setParking(String parking) {
		this.parking = parking;
	}

	@Override
	public String toString() {
		return type + status.toString() + ((parking) == null ? "" : parking);
	}



}
