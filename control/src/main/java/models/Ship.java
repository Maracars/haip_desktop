package models;

public class Ship {

	private String id;
	private Status status;

	public Ship(String id) {
		this.id = id;
	}

	public Ship(String id, Status status) {
		this.id = id;
		this.status = status;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Ship) {
			Ship ship = (Ship) obj;
			return (this.id.equals(ship.getId()));
		}
		return false;
	}
}
