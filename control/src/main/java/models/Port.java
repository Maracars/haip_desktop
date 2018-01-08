package models;

import helpers.IndexAwareSet;
import settings.Settings;

import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import static protocol.ProtocolProperties.ActionType;

public class Port {

	private Dock dock;
	private IndexAwareSet<Ship> transitZone;
	private LinkedList<Ship> transitQueue;

	public Port(Dock dock) {
		this.dock = dock;
		this.transitZone = new IndexAwareSet<>(new CopyOnWriteArraySet<>());
		this.transitQueue = new LinkedList<>();
	}

	public Dock getDock() {
		return dock;
	}

	public IndexAwareSet<Ship> getTransitZone() {
		return transitZone;
	}

	public LinkedList<Ship> getTransitQueue() {
		return transitQueue;
	}


	public boolean addToTransitionZone(Ship ship, String action) {	
		if (!transitQueue.contains(ship)) {
			transitQueue.add(ship);
		}
		if (transitQueue.getFirst().equals(ship) && transitZone.size() < Settings.getProperties().get(1)) {
			transitZone.add(ship);
			if (action.equals(ActionType.LEAVE.toString())) {
				freeMooring(ship);
			}
			transitQueue.removeFirst();
			return true;
		}
		return false;
	}

	public void removeFromTransitZone(Ship ship) {
		transitZone.remove(ship);
	}


	private void freeMooring(Ship ship) {
		for (Mooring mooring : dock.getMoorings()) {
			if (ship.equals(mooring.getShip())) {
				mooring.setShip(null);
			}
		}
	}

	public Mooring getFreeMooring(Ship ship) {
		// Check if ship already has mooring assigned
		for (Mooring mooring : dock.getMoorings()) {
			if (mooring.getShip() != null && ship.equals(mooring.getShip())) {
				return mooring;
			}
		}
		// If it doesn't have one assigned, assign one
		for (Mooring mooring : dock.getMoorings()) {
			if (mooring.getShip() == null) {
				mooring.setShip(ship);
				return mooring;
			}
		}
		return null;
	}
}
