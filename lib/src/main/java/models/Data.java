package models;

public class Data {

	private String dataType;
	private Status status;
	private String parking;
	private String timeWindow;	// In seconds

	public Data() {
	}

	public Data(String type, Status status) {
		this.dataType = type;
		this.status = status;
	}
	
	public Data(String timeWindow) {
		this.timeWindow = timeWindow;
		this.dataType = null;
		this.status = null;
		this.parking = null;
	}

	public Data(String type, Status status, String parking) {
		this.dataType = type;
		this.status = status;
		this.parking = parking;
		this.timeWindow = null;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
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
		return dataType + status.toString() + ((parking) == null ? "" : parking);
	}


}
