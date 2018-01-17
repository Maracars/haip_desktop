package main;

import ui.panels.MainPanel;

import javax.swing.*;

public class Main {
	
	private Main() {
		new MainPanel();
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		new Main();
	}
}
