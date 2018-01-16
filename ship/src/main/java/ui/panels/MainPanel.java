package ui.panels;

import helpers.Helpers;
import jiconfont.icons.FontAwesome;
import jiconfont.swing.IconFontSwing;
import models.Ship;
import models.Status;
import protocol.DecisionMaker;
import protocol.ProtocolProperties.ActionType;
import protocol.ProtocolProperties.PermissionType;
import protocol.ProtocolProperties.StatusType;
import protocol.ShipLogic;
import protocol.SimulationShipLogic;
import serial.Serial;
import ui.dialogs.SimulationDialog;
import ui.log.AutoScrollListPanel;
import ui.log.LogListModel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import static ui.panels.ActionMessages.*;

public class MainPanel implements Observer {
	// Swing Elements
	private JFrame window;
	private LogListModel logListModel;
	private JButton connectButton, logicButton, actionButton;
	private AbstractAction exitAction, connectAction, logicAction, decisionAction, simulationAction;
	private JLabel permissionLabel, statusLabel;
	private JList<String> statusList, decisionList;
	private StatusListRenderer statusRenderer;

	// Serial Communication
	private Serial serial;

	// Ship
	private Ship ship;

	// Ship Logic and Simulation Ship Logic
	private ShipLogic shipLogic;
	private SimulationShipLogic simulationShipLogic;

	// System Initialized
	private boolean waitingForDiscovery;


	public MainPanel(Serial serial, Ship ship, ShipLogic shipLogic, SimulationShipLogic simulationShipLogic) {
		this.createFrame();
		this.initThings(serial, ship, shipLogic, simulationShipLogic);
		this.addContentToJFrame();
	}

	private void createFrame() {
		this.window = new JFrame("Haip Ain't an Infor Project");
		this.window.setIconImage((new ImageIcon("ship/src/main/resources/HAIP_squaredLogo.png").getImage()));
		this.window.setLocation(0, 0);
		this.window.setSize(new Dimension(java.awt.Toolkit.getDefaultToolkit().getScreenSize()));
		this.window.setExtendedState(this.window.getExtendedState() | JFrame.MAXIMIZED_BOTH);
	}

	private void initThings(Serial serial, Ship ship, ShipLogic shipLogic, SimulationShipLogic simulationShipLogic) {
		IconFontSwing.register(FontAwesome.getIconFont());
		this.initActions();

		this.serial = serial;

		this.ship = ship;

		this.shipLogic = shipLogic;
		this.simulationShipLogic = simulationShipLogic;

		this.waitingForDiscovery = false;
	}

	private void initActions() {
		this.exitAction = new ExitAction("Exit", IconFontSwing.buildIcon(FontAwesome.WINDOW_CLOSE, 32),
				"Exit", KeyEvent.VK_X);
		this.exitAction.setEnabled(true);

		this.connectAction = new ConnectAction("Connect to board", IconFontSwing.buildIcon(FontAwesome.PLUG, 32),
				"Connection", KeyEvent.VK_C);
		this.connectAction.setEnabled(true);

		this.logicAction = new WaitForDiscoveryAction("Wait for discovery", IconFontSwing.buildIcon(FontAwesome.WIFI, 32),
				"Wait for the ship to be discovered by the port controller", KeyEvent.VK_I);
		this.logicAction.setEnabled(false);

		this.decisionAction = new DecisionAction("Save Action", IconFontSwing.buildIcon(FontAwesome.CHECK, 32),
				"Save Action", KeyEvent.VK_ACCEPT);
		this.decisionAction.setEnabled(false);

		simulationAction = new SimulationAction("Init Simulation", IconFontSwing.buildIcon(FontAwesome.ROCKET, 32),
				"Init Simulation", KeyEvent.VK_S);
		this.simulationAction.setEnabled(false);
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

		splitPane.setDividerLocation(320);
		splitPane.setLeftComponent(createLeftPanel());
		splitPane.setRightComponent(createShipPanel());

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
		this.logListModel = new LogListModel();
		return new AutoScrollListPanel(logListModel);
	}

	private Component createButtonsPanel() {
		JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
		panel.setBorder(new EmptyBorder(0, 10, 10, 10));

		this.connectButton = new JButton(this.connectAction);
		this.connectButton.setPreferredSize(new Dimension(panel.getWidth(), 72));

		this.logicButton = new JButton(this.logicAction);
		this.logicButton.setPreferredSize(new Dimension(panel.getWidth(), 72));

		panel.add(connectButton);
		panel.add(logicButton);

		return panel;
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
		this.actionButton.setPreferredSize(new Dimension(360, 110));
		this.actionButton.setEnabled(false);
		panel.add(actionButton);
		checkActionButton();
		return panel;
	}

	private void checkActionButton() {
		if (ship.getActionList().size() == 0) {
			actionButton.setEnabled(true);
		} else {
			actionButton.setEnabled(false);
		}
	}

	private Component createDecisionInfoPanel() {
		JPanel decisionPanel = new JPanel(new GridLayout(1, 2));
		Border statusBorder = BorderFactory.createLineBorder(Color.darkGray, 3);
		decisionPanel.add(createStatusList());
		decisionPanel.add(createDecisionList());
		decisionPanel.setBorder(statusBorder);
		return decisionPanel;
	}

	private Component createDecisionList() {
		decisionList = new JList<>(Helpers.getNames(ActionType.class));
		DecisionListRenderer decisionRenderer = new DecisionListRenderer(statusRenderer);
		decisionList.setCellRenderer(decisionRenderer);
		decisionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		decisionList.setLayoutOrientation(JList.VERTICAL);
		decisionList.setBorder(BorderFactory.createMatteBorder(0, 3, 0, 0, Color.darkGray));
		return decisionList;
	}

	private Component createStatusList() {
		statusList = new JList<>(Helpers.getNames(StatusType.class));
		String shipStatus = StatusType.getName(ship.getStatus().getStatus()).name();
		statusRenderer = new StatusListRenderer(shipStatus);
		statusList.setCellRenderer(statusRenderer);
		statusList.setLayoutOrientation(JList.VERTICAL);
		statusList.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 3, Color.darkGray));
		return statusList;
	}

	private Component createInfoPanel() {
		JPanel infoPanel = new JPanel(new GridLayout(1, 2));
		infoPanel.setPreferredSize(new Dimension(this.window.getWidth(), this.window.getHeight() / 3));
		Border statusBorder = BorderFactory.createLineBorder(Color.darkGray, 3);
		infoPanel.add(createStatusLabel(statusBorder));
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

	private void checkStatusLabel() {
		statusLabel.setText("<html>Your status is: " + StatusType.getName(ship.getStatus().getStatus()).name() +
				"<br>You are asking for: " + ActionType.getName(ship.getStatus().getAction()).name() + "</html>");
	}

	private void checkPermissionsLabel() {
		permissionLabel.setText(PermissionType.getName(ship.getStatus().getPermission()).name());
		if (ship.getStatus().getPermission().equals(PermissionType.ALLOW.toString())) {
			permissionLabel.setBackground(new Color(74, 237, 49));
			permissionLabel.setOpaque(true);
		} else if (ship.getStatus().getPermission().equals(PermissionType.DENY.toString())) {
			permissionLabel.setBackground(new Color(255, 22, 73));
			permissionLabel.setOpaque(true);
		} else {
			permissionLabel.setBackground(new Color(244, 185, 66));
			permissionLabel.setOpaque(true);
		}
	}

	private Component createStatusLabel(Border statusBorder) {
		statusLabel = new JLabel();
		checkStatusLabel();
		statusLabel.setBorder(statusBorder);
		statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
		statusLabel.setVerticalAlignment(SwingConstants.CENTER);
		return statusLabel;
	}

	private void repaintElements() {
		statusRenderer.setStatusType(StatusType.getName(ship.getStatus().getStatus()).name());
		statusList.repaint();
		decisionList.repaint();
		repaintLabels();
	}

	private void repaintLabels() {
		checkPermissionsLabel();
		checkStatusLabel();
	}

	@Override
	public void update(Observable o, Object arg) {
		checkActionButton();
		repaintElements();
	}

	private JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(createFileMenu());
		return menuBar;
	}

	private JMenu createFileMenu() {
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		fileMenu.add(simulationAction);
		fileMenu.add(exitAction);
		return fileMenu;
	}

	private void connect() {
		try {
			this.serial.openConnection();
			this.connectButton.setText("Disconnect from board");
			this.logicButton.setEnabled(true);
			this.simulationAction.setEnabled(true);
			LogListModel.add(CONNECTION_ESTABLISHED);
		} catch (Exception e) {
			LogListModel.add(e.getMessage());
		}
	}

	private void disconnect() {
		try {
			this.serial.closeConnection();
			this.simulationAction.setEnabled(false);
			this.connectButton.setText("Connect to board");
			this.logicButton.setEnabled(false);
			LogListModel.add(CONNECTION_CLOSED);
		} catch (Exception e) {
			LogListModel.add(e.getMessage());
		}
	}

	private void waitForDiscovery() {
		this.waitingForDiscovery = true;
		this.serial.addObserver(this.shipLogic);

		this.logicButton.setText("Reject communications");
		this.connectButton.setEnabled(false);
		this.actionButton.setEnabled(true);
		LogListModel.add(LOGIC_INITIALIZED);
	}

	private void rejectCommunications() {
		this.waitingForDiscovery = false;
		this.serial.deleteObserver(this.shipLogic);

		this.logicButton.setText("Wait for discovery");
		this.connectButton.setEnabled(true);
		this.actionButton.setEnabled(false);
		LogListModel.add(LOGIC_STOPPED);
	}

	private void decide() {
		ActionType at = ActionType.valueOf(this.decisionList.getSelectedValue());
		Status newStatus = DecisionMaker.getNewPossibleAction(at);
		this.ship.addAction(newStatus);
		this.ship.getStatus().setAction(at.toString());
		this.ship.getStatus().setPermission(PermissionType.ASK.toString());
		this.actionButton.setEnabled(false);
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
			if (JOptionPane.showConfirmDialog(this.window, ((this.waitingForDiscovery) ?
							"The ship is communicating.\n" : "Serial connection is established.\n")
							+ "Do you really want to exit?",
					"Warning", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				if (this.waitingForDiscovery) rejectCommunications();
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

	public class WaitForDiscoveryAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		String text;
		Icon icon;

		WaitForDiscoveryAction(String text, Icon icon, String description, Integer mnemonic) {
			super(text, icon);
			this.text = text;
			this.icon = icon;
			this.putValue(Action.SHORT_DESCRIPTION, description);
			this.putValue(Action.MNEMONIC_KEY, mnemonic);
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (!waitingForDiscovery) waitForDiscovery();
			else rejectCommunications();
		}
	}

	public class DecisionAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		String text;
		Icon icon;

		DecisionAction(String text, Icon icon, String description, Integer mnemonic) {
			super(text, icon);
			this.text = text;
			this.icon = icon;
			this.putValue(Action.SHORT_DESCRIPTION, description);
			this.putValue(Action.MNEMONIC_KEY, mnemonic);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			decide();
		}
	}

	public class SimulationAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		String text;
		Icon icon;

		SimulationAction(String text, Icon icon, String description, Integer mnemonic) {
			super(text, icon);
			this.text = text;
			this.icon = icon;
			this.putValue(Action.SHORT_DESCRIPTION, description);
			this.putValue(Action.MNEMONIC_KEY, mnemonic);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			new SimulationDialog(window, simulationShipLogic);
		}
	}

	public class ExitAction extends AbstractAction {
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
