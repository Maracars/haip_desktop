package protocol;

import jssc.SerialPortException;
import models.*;
import protocol.ProtocolProperties.ActionType;
import protocol.ProtocolProperties.PermissionType;
import protocol.ProtocolProperties.StatusType;
import ui.panels.MainPanel;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Scanner;


public class ProbaObserver extends Observable {
	private String watched = "";

	public static void main(String[] args) throws SerialPortException {
		ProbaObserver probaObserver = new ProbaObserver();
		Status status = new Status(StatusType.PARKING.toString(), ActionType.LEAVE.toString(), PermissionType.ASK.toString());
		Ship ship = new Ship("00000011", status);
		ShipLogic watcher = new ShipLogic(null, ship);
		MainPanel panel = new MainPanel(null, ship, watcher);
		
		watcher.addObserver(panel);
		probaObserver.addObserver(watcher);
		Scanner scanner = new Scanner(System.in);
		while (true) {
			String line = scanner.nextLine();
			probaObserver.setWatched(line);
		}

	}

	public void setWatched(String string) {
		watched += string;

		int result = FrameParser.parseRx(watched);
		if (result == FrameParser.BAD_PACKET) {
			watched = "";
		} else if (result == FrameParser.FIN_PACKET) {
			setChanged();
			notifyObservers(FrameParser.getFrame());
			watched = "";

		}

	}

}