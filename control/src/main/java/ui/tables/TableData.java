package ui.tables;

public class TableData {

	private int shipID;
	private int position;
	private int action;
	private boolean permission;

	public TableData(int shipID, int position, int action, boolean permission) {
		this.shipID = shipID;
		this.position = position;
		this.action = action;
		this.permission = permission;
	}

	public Object getFieldAt(int column) {
		switch (column) {
			case 0:
				return shipID;
			case 1: {
				switch (position) {
					case 0:
						return "Dock";
					case 1:
						return "Transit Zone";
					case 2:
						return "Sea";
					default:
						return "ERROR: Illegal Position";
				}
			}
			case 2: {
				switch (action) {
					case 0:
						return "Enter";
					case 1:
						return "Leave";
					case 2:
						return "Idle";
					default:
						return "ERROR: Illegal Action";
				}
			}
			case 3:
				return permission;
			default:
				return null;
		}
	}

	public int getShipID() {
		return shipID;
	}

	public void setShipID(int shipID) {
		this.shipID = shipID;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}

	public boolean isPermission() {
		return permission;
	}

	public void setPermission(boolean permission) {
		this.permission = permission;
	}
}