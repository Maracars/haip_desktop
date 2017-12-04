package models;

public class Status {

	String position;
	String action;

	public Status(String position, String action) {
		this.position = position;
		this.action = action;
	}

	public Status() {
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	@Override
	public String toString() {
		return position + action;
	}
}
