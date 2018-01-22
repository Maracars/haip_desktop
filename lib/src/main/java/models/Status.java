package models;

public class Status {

	private String position;
	private String action;
	private String permission;

	public Status(String position, String action, String permission) {
		this.position = position;
		this.action = action;
		this.permission = permission;
	}

	public Status(String position, String action) {
		this.position = position;
		this.action = action;
	}

	public Status() {
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	@Override
	public String toString() {
		return position + action + permission;
	}
}
