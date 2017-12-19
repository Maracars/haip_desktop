package protocol;

import models.Frame;
import serial.Serial;
import ui.tables.TableData;
import ui.tables.TableModel;

import java.util.Observable;
import java.util.Observer;

import static protocol.ProtocolProperties.MASTER_ID;
import static protocol.ProtocolProperties.PacketType;

public class SerialObserver implements Observer {
	Frame frame;
	TableModel tableModel;

	public SerialObserver(TableModel tableModel) {
		this.frame = null;
		this.tableModel = tableModel;
	}

	@Override
	public void update(Observable observable, Object object) {
		this.frame = (Frame) object;

		if (this.frame.getData() != null && this.frame.getData().getType().equals(PacketType.DATA.toString())) {
			if (this.frame.getOriginId() != MASTER_ID) {
				int shipID = Integer.parseInt(this.frame.getOriginId(), 2);
				int status = Integer.parseInt(this.frame.getData().getStatus().getStatus(), 2);
				int action = Integer.parseInt(this.frame.getData().getStatus().getAction(), 2);

				TableData tableData = new TableData(shipID, status, action, false);
				this.tableModel.add(tableData);
			}
			else {
				int shipID = Integer.parseInt(this.frame.getDestinationId(), 2);
				boolean permission = this.frame.getData().getStatus().getPermission()
						.equals(ProtocolProperties.PermissionType.ALLOW.toString());
				this.tableModel.updatePermission(shipID, permission);
			}
		}
	}
}
