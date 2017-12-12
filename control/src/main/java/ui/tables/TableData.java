package ui.tables;

public class TableData {

	int shipID;
	int position;
	int action;
	boolean permission;
	
	public TableData(int shipID, int position, int action, boolean permission) {
		this.shipID = shipID;
		this.position = position;
		this.action = action;
		this.permission = permission;
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
				switch (action) {
					case 0: return "Get Inside";
					case 1: return "Get Outside";
					case 2: return "Stay Idle";
					default: return "Error: Illegal Action";
				}
			}
			case 3: return permission;
			default: return null;
		}
	}
}