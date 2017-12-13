package ui.panels;

import jiconfont.icons.FontAwesome;
import jiconfont.swing.IconFontSwing;
import jssc.SerialPortException;
import protocol.SerialObserver;
import serial.Serial;
import ui.log.LogModel;
import ui.log.LogPanel;
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
import java.io.IOException;

import static ui.panels.ActionMessages.*;

public class MainPanel {
	// Window
	JFrame window;

	// Table Elements
	JTable table;
	TableModel tableModel;

	// Log Elements
	LogModel logModel;

	// Buttons
	JButton connectButton, initButton;

	// Actions
	AbstractAction exitAction, connectAction, initAction;

	// Serial Communication
	Serial serial;
	SerialObserver serialObserver;

	// System Initialized
	boolean systemInitialized;

	public MainPanel(Serial serial) {
		this.createJFrame();
        this.initThings(serial);
        this.addContentToJFrame();
	}

	private void createJFrame() {
		this.window = new JFrame("Haip Ain't an Infor Project");
		this.window.setIconImage((new ImageIcon("control/src/main/resources/HAIP_squaredLogo.png").getImage()));
		this.window.setLocation(0, 0);
		this.window.setSize(new Dimension(java.awt.Toolkit.getDefaultToolkit().getScreenSize()));
		this.window.setExtendedState(this.window.getExtendedState() | JFrame.MAXIMIZED_BOTH);
	}

	private void initThings(Serial serial) {
		IconFontSwing.register(FontAwesome.getIconFont());
		this.initActions();
		this.initTable();

		this.serial = serial;
		this.serialObserver = new SerialObserver(this.serial, this.tableModel);
		this.serial.addObserver(this.serialObserver);

		this.systemInitialized = false;
	}

	private void addContentToJFrame() {
		this.window.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
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

		this.initButton = new JButton(this.initAction);
		this.initButton.setPreferredSize(new Dimension(panel.getWidth(), this.window.getHeight() / 15));
		this.initButton.setEnabled(false);
		panel.add(initButton);

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
        JMenuBar menuBar = new JMenuBar();
		menuBar.add(createExitMenu());
		return menuBar;
	}

	private JMenu createExitMenu() {
        JMenu menuExit = new JMenu("Exit");
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
		exitAction = new ExitAction("Exit",
				IconFontSwing.buildIcon(FontAwesome.WINDOW_CLOSE, 32),
                "Exit", KeyEvent.VK_X);
		connectAction = new ConnectAction("Connect to board",
				IconFontSwing.buildIcon(FontAwesome.PLUG, 32),
				"Connection", KeyEvent.VK_C);
		initAction = new InitAction("Initialize system",
				IconFontSwing.buildIcon(FontAwesome.TOGGLE_ON, 32),
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
					initButton.setEnabled(true);
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
					initButton.setEnabled(false);
					logModel.add(CONNECTION_CLOSED);
				}
				catch (SerialPortException e) {
					logModel.add(e.getMessage());
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
				initButton.setIcon(IconFontSwing.buildIcon(FontAwesome.TOGGLE_OFF, 32));
				connectButton.setEnabled(false);
				logModel.add(SYSTEM_INITIALIZED);
			}
			else {
				// Stop System
				stopSystem();

				initButton.setText("Initialize system");
				initButton.setIcon(IconFontSwing.buildIcon(FontAwesome.TOGGLE_ON, 32));
				connectButton.setEnabled(true);
				logModel.add(SYSTEM_STOPPED);
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
