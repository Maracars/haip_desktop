package models;

import protocol.ProtocolProperties;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import static protocol.ProtocolProperties.ActionType;

public class Port {

	private Dock dock;
	private Set<Ship> transitZone;
	private LinkedList<Ship> transitQueue;


	public Port(Dock dock) {
		this.dock = dock;
		this.transitZone = new HashSet<>();
		this.transitQueue = new LinkedList<>();
	}

	public Dock getDock() {
		return dock;
	}

	public Set<Ship> getTransitZone() {
		return transitZone;
	}

	public LinkedList<Ship> getTransitQueue() {
		return transitQueue;
	}


	public boolean addToTransitionZone(Ship ship, String action) {
		if (transitZone.size() < ProtocolProperties.TRANSIT_ZONE_SHIP_LIMIT) {
			transitZone.add(ship);
			if (action.equals(ActionType.LEAVE.toString())) {
				freeMooring(ship);

			}
			return true;
		} else {
			transitQueue.add(ship);
			return false;
		}
	}

	public void removeFromTransitZone(Ship ship) {
		transitZone.remove(ship);
		if (!transitQueue.isEmpty()) {
			transitZone.add(transitQueue.removeFirst());
		}
	}

	private void freeMooring(Ship ship) {
		for (Mooring mooring : dock.getMoorings()) {
			if (ship.equals(mooring.getShip())) {
				mooring.setShip(null);
			}
		}
	}

	public Mooring getFreeMooring(Ship ship) {

		for (Mooring mooring : dock.getMoorings()) {
			if (ship.equals(mooring.getShip())) {
				return mooring;
			}
		}
		for (Mooring mooring : dock.getMoorings()) {
			if (mooring.getShip() == null) {
				mooring.setShip(ship);
				return mooring;
			}
		}
		return null;
	}
}
