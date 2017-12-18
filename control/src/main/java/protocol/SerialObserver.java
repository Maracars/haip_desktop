package protocol;

import models.Frame;
import models.Ship;
import serial.Serial;
import ui.tables.TableData;
import ui.tables.TableModel;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import static protocol.ProtocolProperties.MASTER_ID;

public class SerialObserver implements Observer {
	Serial serial;
	ControllerLogic controllerLogic;
	Frame frame;
	TableModel tableModel;

	public SerialObserver(Serial serial, ControllerLogic controllerLogic, TableModel tableModel) {
		this.serial = serial;
		this.serial.addObserver(this);

		this.controllerLogic = controllerLogic;
		this.controllerLogic.addObserver(this);

		this.frame = null;
		this.tableModel = tableModel;
	}

	@Override
	public void update(Observable observable, Object object) {
		this.frame = (Frame) object;

		if (this.frame.getData() != null && this.frame.getData().getType().equals(ProtocolProperties.PacketType.DATA)) {
			if (this.frame.getOriginId() != MASTER_ID) {
				int shipID = Integer.parseInt(this.frame.getOriginId(), 2);
				int status = Integer.parseInt(this.frame.getData().getStatus().getStatus(), 2);
				int action = Integer.parseInt(this.frame.getData().getStatus().getAction(), 2);

				TableData tableData = new TableData(shipID, status, action, false);
				this.tableModel.add(tableData);
			}
			else {
				int shipID = Integer.parseInt(this.frame.getDestinationId(), 2);
				boolean permission = this.frame.getData().getStatus().getPermission().equals("11");
				this.tableModel.updatePermission(shipID, permission);
			}
		}
	}
}
