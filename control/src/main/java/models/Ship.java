package models;

public class Ship {

	private String id;

	public Ship(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object obj) {
		Ship ship = (Ship) obj;
		return this.id.equals(ship.getId());
	}
}
