package protocol;

import models.Frame;
import serial.Serial;
import ui.tables.TableData;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class SerialObserver implements Observer {
    Serial serial;
    Frame frame;
    List<TableData> tableDataList;

    public SerialObserver(Serial serial, List<TableData> tableDataList) {
        this.serial = serial;
        this.serial.addObserver(this);
        this.tableDataList = tableDataList;
        this.frame = null;
    }

    @Override
    public void update(Observable observable, Object object) {
        this.frame = (Frame) object;

        int shipID = Integer.parseInt(this.frame.getOriginId());
        int status = Integer.parseInt(this.frame.getData().getStatus().getStatus());
        int action = Integer.parseInt(this.frame.getData().getStatus().getPermission());

        System.out.println(shipID);
        System.out.println(status);
        System.out.println(action);

        TableData tableData = new TableData(shipID, status, action, false);
        this.tableDataList.add(tableData);
    }
}
