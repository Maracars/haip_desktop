package models;

public class Status {

	private String status;
	private String action;
	private String permission;

	public Status(String status, String action, String permission) {
		this.status = status;
		this.action = action;
		this.permission = permission;
	}

	public Status() {}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	@Override
	public String toString() {
		return status + action + permission;
	}
}
