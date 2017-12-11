package ui.tables;

public class TableData {

	int shipID;
	int position;
	int desiredAction;
	boolean allowed;
	
	public TableData(int shipID, int position, int desiredAction, boolean allowed) {
		this.shipID = shipID;
		this.position = position;
		this.desiredAction = desiredAction;
		this.allowed = allowed;
	}

	public Object getFieldAt(int column) {
		switch (column) {
			case 0: return shipID;
			case 1: {
				switch (position) {
					case 0: return "Docking Bay";
					case 1: return "Transit Zone";
					case 2: return "Outside";
					default: return "Error: Illegal Position";
				}
			}
			case 2: {
				switch (desiredAction) {
					case 0: return "Get Inside";
					case 1: return "Get Outside";
					case 2: return "Stay Idle";
					default: return "Error: Illegal Action";
				}
			}
			case 3: return allowed;
			default: return null;
		}
	}
}