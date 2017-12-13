package ui.panels;

import helpers.Helpers;
import jiconfont.icons.FontAwesome;
import jiconfont.swing.IconFontSwing;
import jssc.SerialPortException;
import models.Ship;
import protocol.ProtocolProperties.ActionType;
import protocol.ProtocolProperties.PermissionType;
import protocol.ProtocolProperties.StatusType;
import serial.Serial;
import ui.log.LogModel;
import ui.log.LogPanel;

import javax.swing.*;
import javax.swing.border.Border;
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

	// Log Elements
	LogModel logModel;

	// Buttons
	JButton connectButton, waitForDiscoveryAction, actionButton;

	// Actions
	AbstractAction exitAction, connectAction, initAction, decisionAction;

	// Serial Communication
	Serial serial;

	//Ship
	Ship ship;

	// System Initialized
	boolean shipDiscovered;

	//Labels for ship info
	JLabel permissionLabel;


	public MainPanel(Serial serial, Ship ship) {
		this.createJFrame();
		this.initThings(serial, ship);
		this.addContentToJFrame();
	}

	private void createJFrame() {
		this.window = new JFrame("Haip Ain't an Infor Project");
		this.window.setIconImage((new ImageIcon("ship/src/main/resources/HAIP_squaredLogo.png").getImage()));
		this.window.setLocation(0, 0);
		this.window.setSize(new Dimension(java.awt.Toolkit.getDefaultToolkit().getScreenSize()));
		this.window.setExtendedState(this.window.getExtendedState() | JFrame.MAXIMIZED_BOTH);
	}

	private void initThings(Serial serial, Ship ship) {
		IconFontSwing.register(FontAwesome.getIconFont());
		this.initActions();

		this.serial = serial;
		//this.serialObserver = new SerialObserver(this.serial, this.tableModel);
		//this.serial.addObserver(this.serialObserver);

		this.ship = ship;

		this.shipDiscovered = false;
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
		splitPane.setRightComponent(createShipPanel());

		return splitPane;
	}

	private Component createShipPanel() {
		JPanel shipPanel = new JPanel(new BorderLayout(10, 10));
		shipPanel.add(createInfoPanel(), BorderLayout.NORTH);
		shipPanel.add(createDecisionInfoPanel(), BorderLayout.CENTER);
		shipPanel.add(createDecisionActionPanel(), BorderLayout.SOUTH);
		return shipPanel;
	}

	private Component createDecisionActionPanel() {
		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(0, 10, 10, 10));

		this.actionButton = new JButton(this.decisionAction);
		this.actionButton.setPreferredSize(new Dimension(this.window.getHeight() / 3,
				this.window.getHeight() / 10));
		this.actionButton.setEnabled(false);
		panel.add(actionButton);
		return panel;
	}

	private Component createDecisionInfoPanel() {
		JPanel decisionPanel = new JPanel(new GridLayout(1,2));
		JList<String> statusList = new JList<String>(Helpers.getNames(StatusType.class));
		statusList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		statusList.setLayoutOrientation(JList.VERTICAL);
		statusList.setSelectedIndex(-1);
		statusList.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 3, Color.darkGray));
		JList<String> decisionList = new JList<String>(Helpers.getNames(ActionType.class));
		decisionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		decisionList.setLayoutOrientation(JList.VERTICAL);
		decisionList.setSelectedIndex(-1);
		decisionList.setBorder(BorderFactory.createMatteBorder(0,3,0,0, Color.darkGray));
		Border statusBorder = BorderFactory.createLineBorder(Color.darkGray, 3);
		decisionPanel.add(statusList);
		decisionPanel.add(decisionList);
		decisionPanel.setBorder(statusBorder);
		return decisionPanel;
	}

	private Component createInfoPanel() {
		JPanel infoPanel = new JPanel(new GridLayout(1,2));
		infoPanel.setPreferredSize(new Dimension(this.window.getWidth(), this.window.getHeight()/3));
		Border statusBorder = BorderFactory.createLineBorder(Color.darkGray, 3);

		infoPanel.add(crateStatusLabel(statusBorder));
		infoPanel.add(createPermissionsLabel(statusBorder));

		return infoPanel;

	}

	private Component createPermissionsLabel(Border statusBorder) {
		permissionLabel = new JLabel();
		checkPermissionsLabel();
		permissionLabel.setBorder(statusBorder);
		permissionLabel.setHorizontalAlignment(SwingConstants.CENTER);
		permissionLabel.setVerticalAlignment(SwingConstants.CENTER);
		return permissionLabel;
	}

	private void checkPermissionsLabel() {
		permissionLabel.setText(PermissionType.getName(ship.getStatus().getPermission()).name());
		if(ship.getStatus().getPermission().equals(PermissionType.ALLOW.toString())) {
			permissionLabel.setBackground(new Color(74, 237, 49));
			permissionLabel.setOpaque(true);
		}else if (ship.getStatus().getPermission().equals(PermissionType.DENY.toString())) {
			permissionLabel.setBackground(new Color(255, 22, 73));
			permissionLabel.setOpaque(true);
		}
	}

	private Component crateStatusLabel(Border statusBorder) {
		String statusText = "Your status is: " + StatusType.getName(ship.getStatus().getStatus()).name()+
				"\nYou are asking for: " + ActionType.getName(ship.getStatus().getAction()).name();
		JLabel statusLabel = new JLabel(statusText);
		statusLabel.setBorder(statusBorder);
		statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
		statusLabel.setVerticalAlignment(SwingConstants.CENTER);
		return statusLabel;
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
			logoPanel = new ImagePanel("ship/src/main/resources/HAIP_logo.png");
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

		this.waitForDiscoveryAction = new JButton(this.initAction);
		this.waitForDiscoveryAction.setPreferredSize(new Dimension(panel.getWidth(), this.window.getHeight() / 15));
		this.waitForDiscoveryAction.setEnabled(false);
		panel.add(waitForDiscoveryAction);

		return panel;
	}

	private void initActions() {
		exitAction = new ExitAction("Exit",
				IconFontSwing.buildIcon(FontAwesome.WINDOW_CLOSE, 32),
				"Exit", KeyEvent.VK_X);
		connectAction = new ConnectAction("Connect to board",
				IconFontSwing.buildIcon(FontAwesome.PLUG, 32),
				"Connection", KeyEvent.VK_C);
		initAction = new WaitForDiscoveryAction("Wait for discovery",
				IconFontSwing.buildIcon(FontAwesome.WIFI, 32),
				"Wait for the ship to be discovered by the port controller", KeyEvent.VK_W);
		decisionAction = new DecisionAction("Save Action",
				IconFontSwing.buildIcon(FontAwesome.CHECK, 32),
				"Save Action", KeyEvent.VK_ACCEPT);
	}

	private WindowAdapter createWindowClosingAdapter() {
		return new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				/* Before closing window, check if communication and system are disabled
				 * If not, disable and close them before exiting */
				if (!serial.isConnected() && !shipDiscovered) {
					((JFrame)e.getSource()).dispose();
				}
				else {
					int dialogResult = JOptionPane.showConfirmDialog(window,
							((shipDiscovered) ?
									"Communications are ongoing.\n" : "Serial connection is established.\n")
							+ "Do you really want to exit?",
							"Warning",
							JOptionPane.YES_NO_OPTION);

					if (dialogResult == JOptionPane.YES_OPTION) {
						rejectCommunications();
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

	public class DecisionAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		String text;
		Icon icon;

		public DecisionAction(String text, Icon icon, String description, Integer mnemonic) {
			super(text, icon);
			this.text = text;
			this.icon = icon;
			this.putValue(Action.SHORT_DESCRIPTION, description);
			this.putValue(Action.MNEMONIC_KEY, mnemonic);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub

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
					waitForDiscoveryAction.setEnabled(true);
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
					waitForDiscoveryAction.setEnabled(false);
					logModel.add(CONNECTION_CLOSED);
				}
				catch (SerialPortException e) {
					logModel.add(e.getMessage());
				}
			}
		}
	}

	public class WaitForDiscoveryAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		String text;
		Icon icon;

		public WaitForDiscoveryAction(String text, Icon icon, String description, Integer mnemonic) {
			super(text, icon);
			this.text = text;
			this.icon = icon;
			this.putValue(Action.SHORT_DESCRIPTION, description);
			this.putValue(Action.MNEMONIC_KEY, mnemonic);
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (!shipDiscovered) {
				// Wait for discovery
				waitForDiscovery();

				waitForDiscoveryAction.setText("Reject communications");
				connectButton.setEnabled(false);
				actionButton.setEnabled(true);
				logModel.add(SYSTEM_INITIALIZED);
			}
			else {
				// Reject communications
				rejectCommunications();

				waitForDiscoveryAction.setText("Wait for discovery");
				connectButton.setEnabled(true);
				actionButton.setEnabled(false);
				logModel.add(SYSTEM_STOPPED);
			}
		}
	}

	public void waitForDiscovery() {
		// TODO
		shipDiscovered = true;
	}

	public void rejectCommunications() {
		// TODO
		shipDiscovered = false;
	}
}
