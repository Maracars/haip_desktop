package models;

import java.util.ArrayList;

public class Dock {

	private String id;
	private ArrayList<Mooring> moorings;


	public ArrayList<Mooring> getMoorings() {
		return moorings;
	}

	public void setMoorings(ArrayList<Mooring> moorings) {
		this.moorings = moorings;
	}
}
