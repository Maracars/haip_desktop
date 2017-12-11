package ui.tables;

public class TableData {

	int shipID;
	int position;
	int desiredAction;
	boolean allowed;
	
	public TableData(int shipID, int position) {
		this.shipID = shipID;
		this.position = position;
	}

	public Class<?> getFieldClass(int index) {
		switch (index) {
			case 0: return Integer.class;
			case 1: return Boolean.class;
			default: return String.class;
		}
	}

	public Object getFieldAt(int column) {
		switch (column) {
			case 0: return shipID;
			case 1: return position;
			default: return null;
		}
	}
}