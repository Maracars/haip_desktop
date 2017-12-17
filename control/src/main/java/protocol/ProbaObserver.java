package protocol;

import jssc.SerialPortException;
import models.*;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Scanner;


public class ProbaObserver extends Observable {
	private String watched = "";

	public static void main(String[] args) throws SerialPortException {
		ProbaObserver probaObserver = new ProbaObserver();

		ArrayList<Mooring> moorings = new ArrayList<>();
		probaObserver.initMoorings(moorings);
		Dock dock = new Dock("Albert Dock", moorings);
		Port port = new Port(dock);
		ControllerLogic watcher = new ControllerLogic(null, port);
		Thread th = new Thread(watcher);
		probaObserver.addObserver(watcher);
		th.start();
		Scanner scanner = new Scanner(System.in);
		while (true) {
			String line = scanner.nextLine();
			probaObserver.setWatched(line);

		}

	}


	public void initMoorings(ArrayList<Mooring> moorings) {
		for (Integer i = 0; i < 10; i++) {
			Ship ship = null;
			if (i == 2) {
				ship = new Ship("00000010");

			}
			moorings.add(new Mooring(i.toString(), ship));

		}
		Status status = new Status(ProtocolProperties.StatusType.PARKING.toString(), ProtocolProperties.ActionType.IDLE.toString(), ProtocolProperties.PermissionType.ASK.toString());

		System.out.println("jajajjaaiokese" + FrameCreator.createAck("00000001", "00000000"));
	}

	public void setWatched(String string) {
		watched += string;

		boolean parsed = FrameParser.parseRx(watched);

		if (parsed) {
			setChanged();
			notifyObservers(FrameParser.getFrame());
			watched = "";
		} else {
			watched = "";
		}
	}


}