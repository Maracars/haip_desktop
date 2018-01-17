package ui.panels;

import helpers.Helpers;
import jiconfont.icons.FontAwesome;
import jiconfont.swing.IconFontSwing;
import models.Dock;
import models.Mooring;
import models.Port;
import protocol.ControllerLogic;
import protocol.SerialObserver;
import serial.Serial;
import settings.Settings;
import ui.dialogs.SettingsDialog;
import ui.log.AutoScrollListPanel;
import ui.log.LogListModel;
import ui.tables.CellRenderer;
import ui.tables.ColumnModel;
import ui.tables.TableModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static settings.Settings.*;
import static ui.panels.ActionMessages.*;

public class MainPanel {
	// UI Elements
	private JFrame window;
	private MapPanel mapPanel;
	private JTable table;
	private TableModel tableModel;
	private ColumnModel columnModel;
	private JButton connectButton, logicButton;
	private AbstractAction connectAction, logicAction, settingsAction, resetAction, exitAction;

	// Settings List
	private Properties properties;
	private List<TextFieldPanel> fieldList;

	// Serial Communication
	private Serial serial;

	// Controller Logic
	private ControllerLogic controllerLogic;

	public MainPanel() {
		this.createJFrame();
		this.initActions();
		this.initTable();
		try {
			Port port = this.initSystem();
			this.createMapPanel(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.addContentToJFrame();
	}

	private void createJFrame() {
		this.window = new JFrame("Haip Ain't an Infor Project");
		this.window.setIconImage((new ImageIcon("control/src/main/resources/HAIP_squaredLogo.png").getImage()));
		this.window.setLocation(0, 0);
		this.window.setSize(new Dimension(java.awt.Toolkit.getDefaultToolkit().getScreenSize()));
		this.window.setExtendedState(this.window.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		this.window.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		IconFontSwing.register(FontAwesome.getIconFont());
	}

	private void initActions() {
		connectAction = new ConnectAction("Connect to board", IconFontSwing.buildIcon(FontAwesome.PLUG, 32),
				"Connection", KeyEvent.VK_C);
		connectAction.setEnabled(true);

		logicAction = new LogicAction("Initialize system", IconFontSwing.buildIcon(FontAwesome.TOGGLE_OFF, 32),
				"Initialize system", KeyEvent.VK_I);
		logicAction.setEnabled(false);

		settingsAction = new SettingsAction("Settings", IconFontSwing.buildIcon(FontAwesome.SLIDERS, 32),
				"Change port properties", KeyEvent.VK_S);
		settingsAction.setEnabled(true);

		resetAction = new ResetAction("Reset", IconFontSwing.buildIcon(FontAwesome.REFRESH, 32),
				"Reset Port", KeyEvent.VK_R);
		resetAction.setEnabled(true);

		exitAction = new ExitAction("Exit", IconFontSwing.buildIcon(FontAwesome.WINDOW_CLOSE, 32),
				"Close program", KeyEvent.VK_X);
		exitAction.setEnabled(true);
	}

	private void initTable() {
		CellRenderer cellRenderer = new CellRenderer();
		this.columnModel = new ColumnModel(cellRenderer);
		this.tableModel = new TableModel(this.columnModel);
		this.table = new JTable(this.tableModel, this.columnModel);

		this.table.setRowHeight(50);
		this.table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		this.table.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 24));
	}

	private void readSettings() throws IOException {
		this.properties = new Properties();
		this.properties.load(new FileReader(FILE_NAME));

		List<String> settings = new ArrayList<>();
		for (int i = 0; i < NUM_OF_SETTINGS; i++) {
			settings.add(properties.getProperty(PROPERTY_NAMES[i], String.valueOf(SHIP_LIMITS[i])));
		}
		Settings.setProperties(settings);
	}

	private Port initSystem() throws IOException {
		this.readSettings();

		this.serial = new Serial();
		Port port = new Port(new Dock("Albert Dock", this.initMoorings()));
		this.controllerLogic = new ControllerLogic(this.serial, port);

		SerialObserver serialObserver = new SerialObserver(this.tableModel);
		this.serial.addObserver(this.controllerLogic);
		this.serial.addObserver(serialObserver);
		this.controllerLogic.addObserver(serialObserver);

		return port;
	}

	private List<Mooring> initMoorings() {
		List<Mooring> moorings = new ArrayList<>();
		for (Integer i = 0; i < Settings.getProperties().get(0); i++) {
			moorings.add(new Mooring(Helpers.toNbitBinaryString(i.toString(), 8), null));
		}
		return moorings;
	}

	private void createMapPanel(Port port) {
		this.mapPanel = new MapPanel(port, this.controllerLogic);
	}

	private void addContentToJFrame() {
		this.window.addWindowListener(this.createWindowClosingAdapter());
		this.window.setJMenuBar(this.createMenuBar());
		this.window.getContentPane().add(this.createSplitPane(), BorderLayout.CENTER);

		this.window.setVisible(true);
	}

	private Component createSplitPane() {
		JSplitPane splitPane = new JSplitPane();

		splitPane.setDividerLocation(320);
		splitPane.setLeftComponent(createLeftPanel());
		splitPane.setRightComponent(createTabbedPane());

		return splitPane;
	}

	private Component createLeftPanel() {
		JPanel leftPanel = new JPanel(new BorderLayout(10, 10));

		try {
			leftPanel.add(createHaipPanel(), BorderLayout.NORTH);
		} catch (IOException e) {
			LogListModel.add(ERROR_READING_LOGO);
		}
		leftPanel.add(createLogPanel(), BorderLayout.CENTER);
		leftPanel.add(createButtonsPanel(), BorderLayout.SOUTH);

		return leftPanel;
	}

	private Component createHaipPanel() throws IOException {
		ImagePanel logoPanel = new ImagePanel("control/src/main/resources/HAIP_logo.png");
		logoPanel.scaleImage(96, 96);

		JLabel title = new JLabel("Haip");
		title.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 32));
		JLabel subtitle = new JLabel("Haip Ain't an Infor Project");
		subtitle.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));

		JPanel textPanel = new JPanel();
		textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
		textPanel.add(Box.createVerticalGlue());
		textPanel.add(title);
		textPanel.add(subtitle);
		textPanel.add(Box.createVerticalGlue());

		JPanel haipPanel = new JPanel(new BorderLayout());
		haipPanel.add(textPanel, BorderLayout.CENTER);
		haipPanel.add(logoPanel, BorderLayout.WEST);

		return haipPanel;
	}

	private Component createLogPanel() {
		return new AutoScrollListPanel(new LogListModel());
	}

	private Component createButtonsPanel() {
		JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
		panel.setBorder(new EmptyBorder(0, 10, 10, 10));

		this.connectButton = new JButton(this.connectAction);
		this.connectButton.setPreferredSize(new Dimension(panel.getWidth(), 72));

		this.logicButton = new JButton(this.logicAction);
		this.logicButton.setPreferredSize(new Dimension(panel.getWidth(), 72));

		panel.add(createSizesPanel());
		panel.add(connectButton);
		panel.add(logicButton);

		return panel;
	}

	private Component createSizesPanel() {
		JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));
		this.fieldList = new ArrayList<>();

		for (int i = 0; i < NUM_OF_SETTINGS; i++) {
			TextFieldPanel textFieldPanel = new TextFieldPanel(PROPERTY_FIELD_TITLES[i],
					String.valueOf(Settings.getProperties().get(i)));
			textFieldPanel.setEditable(false);
			this.fieldList.add(textFieldPanel);
			panel.add(textFieldPanel);
		}

		return panel;
	}

	private Component createTabbedPane() {
		JTabbedPane tabbedPane = new JTabbedPane();

		tabbedPane.addTab("Port Map", null, createTab1(), "Shows port map");
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

		tabbedPane.addTab("Ship Data Table", null, createTab2(), "Shows ship data in a table");
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

		return tabbedPane;
	}

	private Component createTab1() {
		return this.mapPanel;
	}

	private Component createTab2() {
		JScrollPane panelScrollTabla = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		panelScrollTabla.setViewportView(table);
		return panelScrollTabla;
	}

	private JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(createFileMenu());
		return menuBar;
	}

	private JMenu createFileMenu() {
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		fileMenu.add(settingsAction);
		fileMenu.add(resetAction);
		fileMenu.add(exitAction);
		return fileMenu;
	}

	private void connect() {
		try {
			serial.openConnection();
			connectButton.setText("Disconnect from board");
			LogListModel.add(CONNECTION_ESTABLISHED);
			logicAction.setEnabled(true);
		} catch (Exception e) {
			LogListModel.add(e.getMessage());
		}
	}

	private void disconnect() {
		try {
			serial.closeConnection();
			connectButton.setText("Connect to board");
			LogListModel.add(CONNECTION_CLOSED);
			logicAction.setEnabled(false);
		} catch (Exception e) {
			LogListModel.add(e.getMessage());
		}
	}

	private void startSystem() {
		logicButton.setText("Stop system");
		logicButton.setIcon(IconFontSwing.buildIcon(FontAwesome.TOGGLE_ON, 32));
		LogListModel.add(SYSTEM_INITIALIZED);
		connectAction.setEnabled(false);
		settingsAction.setEnabled(false);
		resetAction.setEnabled(false);

		try {
			controllerLogic.setActive(true);
		} catch (InterruptedException e) {
			LogListModel.add(e.getMessage());
		}
	}

	private void stopSystem() {
		Thread deactivator = new Thread(() -> {
			try {
				logicButton.setText("Stopping system...");
				logicAction.setEnabled(false);
				logicButton.setIcon(IconFontSwing.buildIcon(FontAwesome.STOP_CIRCLE, 32));

				controllerLogic.setActive(false);
				LogListModel.add(SYSTEM_STOPPED);

				logicButton.setText("Initialize system");
				logicAction.setEnabled(true);
				logicButton.setIcon(IconFontSwing.buildIcon(FontAwesome.TOGGLE_OFF, 32));

				connectAction.setEnabled(true);
				settingsAction.setEnabled(true);
				resetAction.setEnabled(true);
			} catch (InterruptedException e) {
				LogListModel.add(e.getMessage());
			}
		});
		deactivator.setDaemon(true);
		deactivator.start();
	}

	private void resetPort() {
		this.table.setModel(new TableModel(this.columnModel));
		try {
			Port port = initSystem();
			this.mapPanel.resetPort(port, this.controllerLogic);
			this.controllerLogic.resetPort();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private WindowAdapter createWindowClosingAdapter() {
		return new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				onWindowClosing();
			}
		};
	}

	private void onWindowClosing() {
		/* Before closing window, check if communication and system are disabled
		 * If not, disable and close them before exiting */
		if (!this.serial.isConnected()) {
			this.exitProgram();
		} else {
			if (JOptionPane.showConfirmDialog(this.window, (this.controllerLogic.isActive() ?
							"System is initialized.\n" : "Serial connection is established.\n")
							+ "Do you really want to exit?",
					"Warning", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				if (this.controllerLogic.isActive()) this.stopSystem();
				this.disconnect();
				this.exitProgram();
			}
		}
	}

	private void exitProgram() {
		this.window.dispose();
		System.exit(0);
	}

	public class ConnectAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		String text;
		Icon icon;

		ConnectAction(String text, Icon icon, String description, Integer mnemonic) {
			super(text, icon);
			this.text = text;
			this.icon = icon;
			this.putValue(Action.SHORT_DESCRIPTION, description);
			this.putValue(Action.MNEMONIC_KEY, mnemonic);
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (!serial.isConnected()) connect();
			else disconnect();
		}
	}

	public class LogicAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		String text;
		Icon icon;

		LogicAction(String text, Icon icon, String description, Integer mnemonic) {
			super(text, icon);
			this.text = text;
			this.icon = icon;
			this.putValue(Action.SHORT_DESCRIPTION, description);
			this.putValue(Action.MNEMONIC_KEY, mnemonic);
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (!controllerLogic.isActive()) startSystem();
			else stopSystem();
		}
	}

	private class SettingsAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		String text;
		Icon icon;

		SettingsAction(String text, Icon icon, String description, Integer mnemonic) {
			super(text, icon);
			this.text = text;
			this.icon = icon;
			this.putValue(Action.SHORT_DESCRIPTION, description);
			this.putValue(Action.MNEMONIC_KEY, mnemonic);
		}

		@Override
		public void actionPerformed(ActionEvent actionEvent) {
			SettingsDialog settingsDialog = new SettingsDialog();
			if (settingsDialog.getSettingsChanged()) {
				List<String> settings = settingsDialog.getSettings();
				Settings.setProperties(settings);

				for (int i = 0; i < settings.size(); i++) {
					properties.setProperty(PROPERTY_NAMES[i], settings.get(i));
					try {
						properties.store(new FileWriter(FILE_NAME), null);
					} catch (IOException e) {
						e.printStackTrace();
					}
					fieldList.get(i).setText(settings.get(i));
				}
				resetPort();
			}
		}
	}

	private class ResetAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		String text;
		Icon icon;

		ResetAction(String text, Icon icon, String description, Integer mnemonic) {
			super(text, icon);
			this.text = text;
			this.icon = icon;
			this.putValue(Action.SHORT_DESCRIPTION, description);
			this.putValue(Action.MNEMONIC_KEY, mnemonic);
		}

		@Override
		public void actionPerformed(ActionEvent actionEvent) {
			if (!serial.isConnected()) {
				if (JOptionPane.showConfirmDialog(window, "Do you really want to reset the port?",
						"Warning", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					resetPort();
				}
			} else {
				if (JOptionPane.showConfirmDialog(window, (controllerLogic.isActive() ?
								"System is initialized.\n" : "Serial connection is established.\n")
								+ "Do you really want to reset the port?",
						"Warning", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					if (controllerLogic.isActive()) stopSystem();
					disconnect();
					resetPort();
				}
			}
		}
	}

	private class ExitAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		String text;
		Icon icon;

		ExitAction(String text, Icon icon, String description, Integer mnemonic) {
			super(text, icon);
			this.text = text;
			this.icon = icon;
			this.putValue(Action.SHORT_DESCRIPTION, description);
			this.putValue(Action.MNEMONIC_KEY, mnemonic);
		}

		@Override
		public void actionPerformed(ActionEvent actionEvent) {
			onWindowClosing();
		}
	}
}
