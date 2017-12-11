package protocol;

import java.util.Observable;
import java.util.Scanner;


public class ProbaObserver extends Observable {
	private String watchedValue;

	public static void main(String[] args) {
		ProbaObserver watched = new ProbaObserver();
		NodeLogic watcher = new NodeLogic();
		Thread th = new Thread(watcher);
		watched.addObserver(watcher);
		th.start();
		Scanner scanner = new Scanner(System.in);
		while (true) {
			String line = scanner.nextLine();
			watched.setValue(line);
		}

	}

	private void setValue(String value) {
		if (!value.equals(watchedValue)) {
			watchedValue = value;
			setChanged();
			notifyObservers(value);
		}
	}


}