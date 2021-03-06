package models;

import java.util.ArrayList;
import java.util.List;

import helpers.Helpers;
import protocol.ShipLogic;
import serial.Serial;

public class Ship {

	private static final int MAX_CYCLES_DISCOVERY = 10;
	private int idleTime;
	private String id;
	private Status status;
	private String parking;
	private int discoveryCounter;
	private List<Status> actionList;

	public Ship(String id, Status status) {
		this.id = id;
		this.status = status;
		idleTime = 0;
		actionList = new ArrayList<>();
		this.discoveryCounter = 0;
	}

	public Ship() {
	}

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

	public List<Status> getActionList() {
		return actionList;
	}

	public void setActionList(List<Status> actionList) {
		this.actionList = actionList;
	}

	public void addAction(Status newStatus) {
		this.resetDiscoveryCounter();
		this.actionList.add(newStatus);
	}

	public String getParking() {
		return parking;
	}

	public void setParking(String parking) {
		this.parking = parking;
	}

	public int getIdleTime() {
		return idleTime;
	}

	public void setIdleTime(int idleTime) {
		this.idleTime = idleTime;
	}

	public void addIdleTime(int sumTime) {
		idleTime += sumTime;
	}

	public void addDiscoveryCounter() {
		this.discoveryCounter++;
	}
	
	public void resetDiscoveryCounter() {
		this.discoveryCounter = 0;
	}
	
	public boolean checkDiscovery() {
		if(discoveryCounter >= MAX_CYCLES_DISCOVERY || discoveryCounter == 0) {
			return true;
		}
		return false;
	}

}
