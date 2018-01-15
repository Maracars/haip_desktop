package models;

public class Data {

	private String type;
	private Status status;
	private String parking;
	private String timeWindow;

	public Data() {
	}

	public Data(String type, Status status) {
		this.type = type;
		this.status = status;
	}
	
	public Data(String timeWindow) {
		this.timeWindow = timeWindow;
		this.type = null;
		this.status = null;
		this.parking = null;
	}

	public Data(String type, Status status, String parking) {
		this.type = type;
		this.status = status;
		this.parking = parking;
		this.timeWindow = null;
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

	public String getTimeWindow() {
		return timeWindow;
	}

	public void setTimeWindow(String timeWindow) {
		this.timeWindow = timeWindow;
	}

	@Override
	public String toString() {
		if(timeWindow != null)
			return timeWindow;
		return type + status.toString() + ((parking) == null ? "" : parking);
	}


}
