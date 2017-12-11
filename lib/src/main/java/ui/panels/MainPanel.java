package ui.panels;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.management.monitor.CounterMonitor;
import javax.swing.*;

import jssc.SerialPortException;
import serial.Serial;
import ui.log.LogModel;
import ui.tables.CellRenderer;
import ui.tables.ColumnModel;
import ui.tables.TableData;
import ui.tables.TableModel;

public class MainPanel {
	// Window
	JFrame window;

	// Panels
	JSplitPane splitPane;
	JTabbedPane tabbedPane;

	// Menu Bar Elements
	JMenuBar menuBar;
	JMenu menuExit;

	// Actions
	AbstractAction exitAction;

	// Table Elements
	JTable table;
	CellRenderer cellRenderer;
	TableModel tableModel;
	ColumnModel columnModel;
	List<TableData> tableDataList;

	// Log Elements
	LogModel logModel;

	// Buttons
	JButton CommButton;

	// Serial Communication
	Serial serial;

	public MainPanel() {
		this.initActions();
		this.initTable();

		window = new JFrame("Haip Ain't an Infor Project");
		window.setLocation(0, 0);
		window.setSize(1300, 700);
		window.setExtendedState(JFrame.MAXIMIZED_BOTH);
		window.setIconImage((new ImageIcon("lib/src/main/resources/HAIP_squaredLogo.png").getImage()));

		window.setJMenuBar(createMenuBar());
		window.getContentPane().add(createSplitPane(), BorderLayout.CENTER);

		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
	}

	public void initTable() {
		tableDataList = new ArrayList<>();
		cellRenderer = new CellRenderer();
		columnModel = new ColumnModel(cellRenderer);
		tableModel = new TableModel(columnModel, tableDataList);
		table = new JTable(tableModel, columnModel);
		table.setRowHeight(30);
	}

	public void updateTable() {
		table.repaint();
	}

	private Component createSplitPane() {
		this.splitPane = new JSplitPane();

		this.splitPane.setLeftComponent(createLeftPanel());
		this.splitPane.setRightComponent(createTabbedPane());
		this.splitPane.setDividerLocation(300);

		return splitPane;
	}

	private Component createLeftPanel() {
		JPanel panel = new JPanel(new BorderLayout(10, 10));

		panel.add(createLogPanel(), BorderLayout.CENTER);
		panel.add(createButtonsPanel(), BorderLayout.SOUTH);

		return panel;
	}

	private Component createLogPanel() {
		logModel = new LogModel();
		return new LogPanel(logModel);
	}

	private Component createButtonsPanel() {
		JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
		CommAction commAction = new CommAction("Connect", new ImageIcon("lib/src/main/resources/icons/com.png"),
				"Connection", KeyEvent.VK_ENTER);

		CommButton = new JButton(commAction);
		panel.add(CommButton);

		return panel;
	}

	private Component createTabbedPane() {
		this.tabbedPane = new JTabbedPane();
		Icon icon = null;

		JComponent panel1 = (JComponent) createTab1();
		tabbedPane.addTab("Tab 1", icon, panel1, "Does nothing");
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

		JComponent panel2 = (JComponent) createTab2();
		tabbedPane.addTab("Tab 2", icon, panel2, "Does nothing");
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_2);

		return tabbedPane;
	}

	private Component createTab1() {
		return new ImagePanel("lib/src/main/resources/HAIP_logo.png");
	}

	private Component createTab2() {
		JScrollPane panelScrollTabla = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		panelScrollTabla.setViewportView(table);
		return panelScrollTabla;
	}

	private JMenuBar createMenuBar() {
		menuBar = new JMenuBar();
		menuBar.add(createExitMenu());
		return menuBar;
	}

	private JMenu createExitMenu() {
		menuExit = new JMenu("Exit");
		menuExit.add(exitAction);
		return menuExit;
	}

	private void initActions() {
		exitAction = new ExitAction("Exit", new ImageIcon("lib/src/main/resources/icons/exit.png"),
				"Exit", KeyEvent.VK_S);
	}

	public class ExitAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		String text;
		Icon icon;

		public ExitAction(String text, Icon icon, String description, Integer mnemonic) {
			super(text, icon);
			this.text = text;
			this.icon = icon;
			this.putValue(Action.SHORT_DESCRIPTION, description);
			this.putValue(Action.MNEMONIC_KEY, mnemonic);
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (JOptionPane.showConfirmDialog(window, "Are you sure you want to exit?", "Warning",
					JOptionPane.YES_NO_OPTION) == 0) {
				window.dispose();
			}
		}
	}

	public class CommAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		String text;
		Icon icon;

		public CommAction(String text, Icon icon, String description, Integer mnemonic) {
			super(text, icon);
			this.text = text;
			this.icon = icon;
			this.putValue(Action.SHORT_DESCRIPTION, description);
			this.putValue(Action.MNEMONIC_KEY, mnemonic);
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (serial == null) {
				serial = new Serial();

				if (serial.isConnected()) {
					CommButton.setText("Disconnect");
				}
			}
			else {
				CommButton.setText("Connect");
			}
		}
	}

	public void closeConnection() {
		if (serial != null) {
			try {
				serial.closeConnection();
			}
			catch (SerialPortException e) {
				e.printStackTrace();
			}
			serial = null;
		}
	}

}
