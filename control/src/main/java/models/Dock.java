package models;

import java.util.ArrayList;
import java.util.List;

public class Dock {
	private String id;
	private List<Mooring> moorings;

	public Dock(String id, List<Mooring> moorings) {
		this.id = id;
		this.moorings = moorings;
	}

	public Dock(String id) {
		this.id = id;
		this.moorings = new ArrayList<>();
	}

	public List<Mooring> getMoorings() {
		return moorings;
	}

	public void setMoorings(ArrayList<Mooring> moorings) {
		this.moorings = moorings;
	}
}
