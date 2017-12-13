package protocol;

import models.Frame;
import serial.Serial;
import ui.tables.TableData;
import ui.tables.TableModel;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class SerialObserver implements Observer {
    Serial serial;
    Frame frame;
    TableModel tableModel;

    public SerialObserver(Serial serial, TableModel tableModel) {
        this.serial = serial;
        this.serial.addObserver(this);
        this.tableModel = tableModel;
        this.frame = null;
    }

    @Override
    public void update(Observable observable, Object object) {
        if (object.getClass().equals(Frame.class)) {
            this.frame = (Frame) object;

            int shipID = Integer.parseInt(this.frame.getOriginId(), 2);
            int status = Integer.parseInt(this.frame.getData().getStatus().getStatus(), 2);
            int action = Integer.parseInt(this.frame.getData().getStatus().getAction(), 2);

            TableData tableData = new TableData(shipID, status, action, false);
            this.tableModel.add(tableData);
        }
    }
}
