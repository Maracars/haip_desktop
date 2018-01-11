package ui;

import org.junit.Test;
import ui.panels.MainPanel;

import javax.swing.*;

public class ControllerTest {
	public ControllerTest() {
		new MainPanel();
	}

	@Test
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		new ControllerTest();
	}
}
