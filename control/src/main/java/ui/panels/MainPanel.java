package ui.panels;

import jiconfont.icons.FontAwesome;
import jiconfont.swing.IconFontSwing;
import jssc.SerialPortException;
import protocol.ControllerLogic;
import protocol.SerialObserver;
import serial.Serial;
import ui.dialogs.SettingsDialog;
import ui.log.LogModel;
import ui.log.LogPanel;
import ui.tables.CellRenderer;
import ui.tables.ColumnModel;
import ui.tables.TableModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import static ui.panels.ActionMessages.*;

public class MainPanel {
	// Swing Elements
	private JFrame window;
	private JTable table;
	private TableModel tableModel;
	private LogModel logModel;
	private JButton connectButton, logicButton;
	private AbstractAction connectAction, logicAction;

	// Serial Communication
	private Serial serial;
	private SerialObserver serialObserver;

	// Controller Logic
	private ControllerLogic controllerLogic;

	public MainPanel(Serial serial, ControllerLogic controllerLogic) {
		this.createJFrame();
        this.initThings(serial, controllerLogic);
        this.addContentToJFrame();
	}

	private void createJFrame() {
		this.window = new JFrame("Haip Ain't an Infor Project");
		this.window.setIconImage((new ImageIcon("control/src/main/resources/HAIP_squaredLogo.png").getImage()));
		this.window.setLocation(0, 0);
		this.window.setSize(new Dimension(java.awt.Toolkit.getDefaultToolkit().getScreenSize()));
		this.window.setExtendedState(this.window.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		this.window.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	}

	private void initThings(Serial serial, ControllerLogic controllerLogic) {
		IconFontSwing.register(FontAwesome.getIconFont());
		this.initActions();
		this.initTable();

		this.serial = serial;
		this.controllerLogic = controllerLogic;
		this.serialObserver = new SerialObserver(this.tableModel);

		this.serial.addObserver(this.controllerLogic);
		this.serial.addObserver(this.serialObserver);
		this.controllerLogic.addObserver(this.serialObserver);
	}

	private void addContentToJFrame() {
		this.window.addWindowListener(this.createWindowClosingAdapter());
		this.window.setJMenuBar(this.createMenuBar());
		this.window.getContentPane().add(this.createSplitPane(), BorderLayout.CENTER);

		this.window.setVisible(true);
	}

	private Component createSplitPane() {
		JSplitPane splitPane = new JSplitPane();

		splitPane.setDividerLocation(this.window.getWidth() / 7);
		splitPane.setLeftComponent(createLeftPanel());
		splitPane.setRightComponent(createTabbedPane());

		return splitPane;
	}

	private Component createLeftPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));

        leftPanel.add(createLogoPanel(), BorderLayout.NORTH);
        leftPanel.add(createLogPanel(), BorderLayout.CENTER);
        leftPanel.add(createButtonsPanel(), BorderLayout.SOUTH);

		return leftPanel;
	}

	private Component createLogoPanel() {
        ImagePanel logoPanel = null;
		try {
            logoPanel = new ImagePanel("control/src/main/resources/HAIP_logo.png");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
        logoPanel.scaleImage(this.window.getWidth() / 7, this.window.getWidth() / 7);

		return logoPanel;
	}

	private Component createLogPanel() {
        this.logModel = new LogModel();
		return new LogPanel(logModel);
	}

	private Component createButtonsPanel() {
		JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
		panel.setBorder(new EmptyBorder(0, 10, 10, 10));

		this.connectButton = new JButton(this.connectAction);
		this.connectButton.setPreferredSize(new Dimension(panel.getWidth(), this.window.getHeight() / 15));
		panel.add(connectButton);

		this.logicButton = new JButton(this.logicAction);
		this.logicButton.setPreferredSize(new Dimension(panel.getWidth(), this.window.getHeight() / 15));
		//this.logicButton.setEnabled(false);
		panel.add(logicButton);

		return panel;
	}

	private Component createTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();
		Icon icon = null;

		JComponent panel1 = (JComponent) createTab1();
		tabbedPane.addTab("Port Map", icon, panel1, "Shows port map");
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

		JComponent panel2 = (JComponent) createTab2();
		tabbedPane.addTab("Ship Data Table", icon, panel2, "Shows ship data in a table");
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

		return tabbedPane;
	}

	private Component createTab1() {
		MapPanel mapPanel = new MapPanel(controllerLogic.getPort());
		controllerLogic.addObserver(mapPanel);
		return mapPanel;
	}

	private Component createTab2() {
		JScrollPane panelScrollTabla = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		panelScrollTabla.setViewportView(table);
		return panelScrollTabla;
	}

	private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
		menuBar.add(createSettingsMenu());
		menuBar.add(createExitMenu());
		return menuBar;
	}

	private JMenu createSettingsMenu() {
		JMenu settingsMenu = new JMenu("Settings");

		settingsMenu.addMenuListener(new MenuListener() {
			@Override
			public void menuSelected(MenuEvent e) {
				try {
					new SettingsDialog(window);
				} catch (IOException e1) {
					logModel.add(ERROR_READING_SETTINGS);
				}
				settingsMenu.setSelected(false);
			}
			@Override
			public void menuDeselected(MenuEvent e) {}
			@Override
			public void menuCanceled(MenuEvent e) {}
		});
		return settingsMenu;
	}

	private JMenu createExitMenu() {
        JMenu exitMenu = new JMenu("Exit");

		exitMenu.addMenuListener(new MenuListener() {
			@Override
			public void menuSelected(MenuEvent e) {
				onWindowClosing();
			}
			@Override
			public void menuDeselected(MenuEvent e) {}
			@Override
			public void menuCanceled(MenuEvent e) {}
		});
		return exitMenu;
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
		if (!this.serial.isConnected() && !this.controllerLogic.isRunning()) {
			this.exitProgram();
		} else {
			int dialogResult = JOptionPane.showConfirmDialog(this.window, ((this.controllerLogic.isRunning()) ?
							"System is initialized.\n" : "Serial connection is established.\n")
							+ "Do you really want to exit?",
					"Warning", JOptionPane.YES_NO_OPTION);

			if (dialogResult == JOptionPane.YES_OPTION) {
				this.controllerLogic.stopLogic();
				try {
					this.serial.closeConnection();
				} catch (SerialPortException e) {
					this.logModel.add(e.getMessage());
				}
				this.exitProgram();
			}
		}
	}

	private void initTable() {
        CellRenderer cellRenderer = new CellRenderer();
        ColumnModel columnModel = new ColumnModel(cellRenderer);
        this.tableModel = new TableModel(columnModel);

		this.table = new JTable(tableModel, columnModel);
		this.table.setRowHeight(this.window.getHeight() / 20);
		this.table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		this.table.getTableHeader().setFont(new Font("Comic Sans MS", Font.PLAIN, 20));
	}

	private void initActions() {
		connectAction = new ConnectAction("Connect to board",
				IconFontSwing.buildIcon(FontAwesome.PLUG, 32),
				"Connection", KeyEvent.VK_C);
		logicAction = new LogicAction("Initialize system",
				IconFontSwing.buildIcon(FontAwesome.TOGGLE_OFF, 32),
                "Initialize system", KeyEvent.VK_I);
	}

	private void exitProgram() {
		this.window.dispose();
		System.exit(0);
	}

	public class ConnectAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		String text;
		Icon icon;

		public ConnectAction(String text, Icon icon, String description, Integer mnemonic) {
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
					connectButton.setText("Disconnect from board");
					//logicButton.setEnabled(true);
					logModel.add(CONNECTION_ESTABLISHED);
				}
				catch (Exception e) {
					logModel.add(e.getMessage());
				}
			}
			else {
				try {
					serial.closeConnection();
					connectButton.setText("Connect to board");
					//logicButton.setEnabled(false);
					logModel.add(CONNECTION_CLOSED);
				}
				catch (SerialPortException e) {
					logModel.add(e.getMessage());
				}
			}
		}
	}

	public class LogicAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		String text;
		Icon icon;

		public LogicAction(String text, Icon icon, String description, Integer mnemonic) {
			super(text, icon);
			this.text = text;
			this.icon = icon;
			this.putValue(Action.SHORT_DESCRIPTION, description);
			this.putValue(Action.MNEMONIC_KEY, mnemonic);
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (!controllerLogic.isRunning()) {
				controllerLogic.startLogic();

				logicButton.setText("Stop system");
				logicButton.setIcon(IconFontSwing.buildIcon(FontAwesome.TOGGLE_ON, 32));
				//connectButton.setEnabled(false);
				logModel.add(SYSTEM_INITIALIZED);
			} else {
				controllerLogic.stopLogic();

				logicButton.setText("Initialize system");
				logicButton.setIcon(IconFontSwing.buildIcon(FontAwesome.TOGGLE_OFF, 32));
				//connectButton.setEnabled(true);
				logModel.add(SYSTEM_STOPPED);
			}
		}
	}
}
