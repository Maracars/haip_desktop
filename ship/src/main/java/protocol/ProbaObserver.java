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
		SimulationShipLogic simulationShipLogic = new SimulationShipLogic(watcher);
		MainPanel panel = new MainPanel(null, ship, watcher, simulationShipLogic);
		watcher.addObserver(panel);
		probaObserver.addObserver(watcher);
		probaObserver.addObserver(simulationShipLogic);
		Scanner scanner = new Scanner(System.in);
		while (true) {
			String line = scanner.nextLine();
			probaObserver.setWatched(line);
		}

	}

	public void setWatched(String string) {
		watched += string;

		boolean result = FrameParser.parseRx(watched);
		if (!result) {
			watched = "";
		} else if (result) {
			setChanged();
			notifyObservers(FrameParser.getFrame());
			watched = "";

		}

	}

}