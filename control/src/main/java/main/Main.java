package main;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import ui.ControllerTest;
import ui.panels.MainPanel;

public class Main {
	
	public Main() {
		new MainPanel();
	}


	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		new ControllerTest();

	}

}
