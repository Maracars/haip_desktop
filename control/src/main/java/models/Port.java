package models;

import settings.Settings;

import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import static protocol.ProtocolProperties.ActionType;

public class Port {

	private Dock dock;
	private Set<Ship> transitZone;
	private LinkedList<Ship> transitQueue;


	public Port(Dock dock) {
		this.dock = dock;
		this.transitZone = new CopyOnWriteArraySet<>();

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
		if(!transitQueue.contains(ship)) {
			transitQueue.add(ship);
		}
		if(transitQueue.getFirst().equals(ship)) {
			transitZone.add(ship);
			if (action.equals(ActionType.LEAVE.toString())) {
				freeMooring(ship);
			}
			transitQueue.removeFirst();
			return true;
		}
		return false;
		/*if ((transitZone.size() < Settings.getProperties().get(1) || transitZone.contains(ship)) && isFirst(ship)) {
			transitZone.add(ship);
			if(transitQueue.contains(ship)) {
				transitQueue.remove(ship);
			}
			return true;
		} else {
			System.out.println("ADD TO TRANSIT QUEUE: "+ship.getId());
			transitQueue.add(ship);
			return false;
		}*/
	}

	public void removeFromTransitZone(Ship ship) {
		transitZone.remove(ship);
		System.out.println("REMOVE FROM TRANSIT ZONE: "+ship.getId());
		/*if (!transitQueue.isEmpty()) {
			transitZone.add(transitQueue.removeFirst());
		}*/
	}
	
	public boolean isFirst(Ship ship) {
		return transitQueue.isEmpty() || transitQueue.getFirst().equals(ship);
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
