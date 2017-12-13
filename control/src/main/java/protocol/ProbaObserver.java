package protocol;

import jssc.SerialPortException;
import models.Dock;
import models.Mooring;
import models.Port;

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
		for (Integer i = 0; i < 50; i++) {
			moorings.add(new Mooring(i.toString()));
		}
	}

	public void setWatched(String string) {
		watched += string;
		System.out.println(watched);

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