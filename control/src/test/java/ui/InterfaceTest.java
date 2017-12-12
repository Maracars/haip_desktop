package ui;

import org.junit.Test;
import serial.Serial;
import ui.panels.MainPanel;

import javax.swing.*;

public class InterfaceTest {

	@Test
	public void openUI() {
		Serial serial = new Serial();
		new MainPanel(serial);
	}

	@Test
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		new InterfaceTest().openUI();
	}
}
