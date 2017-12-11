package ui.panels;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

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
    JPanel leftPanel;
	ImagePanel logoPanel;

	// Menu Bar Elements
	JMenuBar menuBar;
	JMenu menuExit;

	// Actions
	AbstractAction exitAction, commAction, initAction;

	// Table Elements
	JTable table;
	CellRenderer cellRenderer;
	TableModel tableModel;
	ColumnModel columnModel;
	List<TableData> tableDataList;

	// Log Elements
	LogModel logModel;

	// Buttons
	JButton commButton, initButton;

	// Serial Communication
	Serial serial;

	// System Initialized
	boolean systemInitialized;

	public MainPanel() {
		this.serial = new Serial();
		this.systemInitialized = false;
		this.initActions();
		this.initTable();

		window = new JFrame("Haip Ain't an Infor Project");
		window.setIconImage((new ImageIcon("control/src/main/resources/HAIP_squaredLogo.png").getImage()));
		window.setLocation(0, 0);
		window.setSize(1300, 700);
		window.setExtendedState(JFrame.MAXIMIZED_BOTH);

		window.setJMenuBar(this.createMenuBar());
		window.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		window.addWindowListener(this.createWindowClosingAdapter());

		window.getContentPane().add(this.createSplitPane(), BorderLayout.CENTER);

		window.setVisible(true);
	}

	private Component createSplitPane() {
		this.splitPane = new JSplitPane();

		this.splitPane.setDividerLocation(this.window.getWidth() / 4);
		this.splitPane.setLeftComponent(createLeftPanel());
		this.splitPane.setRightComponent(createTabbedPane());

		return splitPane;
	}

	private Component createLeftPanel() {
		leftPanel = new JPanel(new BorderLayout(10, 10));

        leftPanel.add(createLogoPanel(), BorderLayout.NORTH);
        leftPanel.add(createLogPanel(), BorderLayout.CENTER);
        leftPanel.add(createButtonsPanel(), BorderLayout.SOUTH);

		return leftPanel;
	}

	private Component createLogoPanel() {
		this.logoPanel = null;
		try {
			logoPanel = new ImagePanel("control/src/main/resources/HAIP_logo.png");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		logoPanel.scaleImage(this.splitPane.getDividerLocation(), this.splitPane.getDividerLocation());

		return logoPanel;
	}

	private Component createLogPanel() {
		logModel = new LogModel();
		return new LogPanel(logModel);
	}

	private Component createButtonsPanel() {
		JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
		panel.setBorder(new EmptyBorder(0, 10, 10, 10));

		this.commButton = new JButton(this.commAction);
		this.commButton.setPreferredSize(new Dimension(panel.getWidth(), this.window.getHeight() / 10));
		panel.add(commButton);

		this.initButton = new JButton(this.initAction);
		this.initButton.setPreferredSize(new Dimension(panel.getWidth(), this.window.getHeight() / 10));
		this.initButton.setEnabled(false);
		panel.add(initButton);

		return panel;
	}

	private Component createTabbedPane() {
		this.tabbedPane = new JTabbedPane();
		Icon icon = null;

		JComponent panel1 = (JComponent) createTab1();
		tabbedPane.addTab("Port Map", icon, panel1, "Shows port map");
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

		JComponent panel2 = (JComponent) createTab2();
		tabbedPane.addTab("Ship Data Table", icon, panel2, "Shows ship data in a table");
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_2);

		return tabbedPane;
	}

	private Component createTab1() {
		ImagePanel imagePanel = null;
		try {
			imagePanel = new ImagePanel("control/src/main/resources/HAIP_logo.png");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return imagePanel;
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

	private WindowAdapter createWindowClosingAdapter() {
		return new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				/* Before closing window, check if communication and system are disabled
				 * If not, disable and close them before exiting */
				if (!serial.isConnected() && !systemInitialized) {
					((JFrame)e.getSource()).dispose();
				}
				else {
					int dialogResult = JOptionPane.showConfirmDialog(window,
							((systemInitialized) ?
									"System is initialized.\n" : "Serial connection is established.\n")
									+ "Do you really want to exit?",
							"Warning",
							JOptionPane.YES_NO_OPTION);

					if (dialogResult == JOptionPane.YES_OPTION) {
						stopSystem();
						try {
							serial.closeConnection();
						} catch (SerialPortException e1) {
							e1.printStackTrace();
						}
						((JFrame)e.getSource()).dispose();
					}
				}
			}
		};
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

	private void initActions() {
		exitAction = new ExitAction("Exit",
                new ImageIcon("control/src/main/resources/icons/exit.png"),
                "Exit", KeyEvent.VK_X);
		commAction = new CommAction("Connect",
                new ImageIcon("control/src/main/resources/icons/comm.png"),
				"Connection", KeyEvent.VK_C);
		initAction = new InitAction("Initialize system",
                new ImageIcon("control/src/main/resources/icons/start.png"),
                "Initialize system", KeyEvent.VK_I);
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
			if (!serial.isConnected()) {
				try {
					serial.openConnection();
					commButton.setText("Disconnect");
					initButton.setEnabled(true);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			else {
				try {
					serial.closeConnection();
					commButton.setText("Connect");
					initButton.setEnabled(false);
				}
				catch (SerialPortException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public class InitAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		String text;
		Icon icon;

		public InitAction(String text, Icon icon, String description, Integer mnemonic) {
			super(text, icon);
			this.text = text;
			this.icon = icon;
			this.putValue(Action.SHORT_DESCRIPTION, description);
			this.putValue(Action.MNEMONIC_KEY, mnemonic);
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (!systemInitialized) {
				// Init System
				initSystem();

				initButton.setText("Stop system");
				initButton.setIcon(new ImageIcon("control/src/main/resources/icons/start.png"));
				commButton.setEnabled(false);
			}
			else {
				// Stop System
				stopSystem();

				initButton.setText("Initialize system");
				initButton.setIcon(new ImageIcon("control/src/main/resources/icons/stop.png"));
				commButton.setEnabled(true);
			}
		}
	}

	public void initSystem() {
		// TODO Init System
		systemInitialized = true;
	}

	public void stopSystem() {
		// TODO Stop System
		systemInitialized = false;
	}
}