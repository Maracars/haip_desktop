package models;

public class Mooring {

	private String id;
	// If null is available, if has ship is not available
	private Ship ship;

	public Mooring(String id) {
		this.id = id;
		this.ship = null;
	}

	public Mooring(String id, Ship ship) {
		this.id = id;
		this.ship = null;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Ship getShip() {
		return ship;
	}

	public void setShip(Ship ship) {
		this.ship = ship;
	}
}
