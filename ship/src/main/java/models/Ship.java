package models;

import java.util.List;

public class Ship {
	
	private String id;
	private Status status;
	private List<String> actionList;
	
	public Ship(String id, Status status) {
		this.id = id;
		this.status = status;
	}
	
	public Ship() {}
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public List<String> getActionList() {
		return actionList;
	}

	public void setActionList(List<String> actionList) {
		this.actionList = actionList;
	}
}
