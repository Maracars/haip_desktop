package ui.panels;

import helpers.Helpers;
import jiconfont.icons.FontAwesome;
import jiconfont.swing.IconFontSwing;
import models.Ship;
import models.Status;
import protocol.ProtocolProperties.ActionType;
import protocol.ProtocolProperties.PermissionType;
import protocol.ProtocolProperties.StatusType;
import protocol.ShipLogic;
import protocol.SimulationShipLogic;
import serial.Serial;
import ui.dialogs.SimulationDialog;
import ui.log.LogModel;
import ui.log.LogPanel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import static ui.panels.ActionMessages.*;

public class MainPanel implements ListSelectionListener, Observer{
	// Swing Elements
	private JFrame window;
	private LogModel logModel;
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
	private boolean shipDiscovered;

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

		this.shipDiscovered = false;
	}

	private void initActions() {
		exitAction = new ExitAction("Exit",
				IconFontSwing.buildIcon(FontAwesome.WINDOW_CLOSE, 32),
				"Exit", KeyEvent.VK_X);
		connectAction = new ConnectAction("Connect to board",
				IconFontSwing.buildIcon(FontAwesome.PLUG, 32),
				"Connection", KeyEvent.VK_C);
		logicAction = new WaitForDiscoveryAction("Wait for discovery",
				IconFontSwing.buildIcon(FontAwesome.WIFI, 32),
				"Wait for the ship to be discovered by the port controller", KeyEvent.VK_W);
		decisionAction = new DecisionAction("Save Action",
				IconFontSwing.buildIcon(FontAwesome.CHECK, 32),
				"Save Action", KeyEvent.VK_ACCEPT);
		simulationAction = new SimulationAction("Init Simulation",
				IconFontSwing.buildIcon(FontAwesome.ROCKET, 32), "Init Simulation", null);
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

		splitPane.setDividerLocation(this.window.getWidth() / 6);
		splitPane.setLeftComponent(createLeftPanel());
		splitPane.setRightComponent(createShipPanel());

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
			logoPanel = new ImagePanel("ship/src/main/resources/HAIP_logo.png");
		} catch (IOException e) {
			this.logModel.add(ERROR_READING_LOGO);
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

		this.logicButton = new JButton(this.logicAction);
		this.logicButton.setPreferredSize(new Dimension(panel.getWidth(), this.window.getHeight() / 15));
		//this.logicButton.setEnabled(false);

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
		this.actionButton.setPreferredSize(new Dimension(this.window.getHeight() / 3,
				this.window.getHeight() / 10));
		this.actionButton.setEnabled(false);
		panel.add(actionButton);
		checkActionButton();
		return panel;
	}

	private void checkActionButton() {
		if(ship.getActionList().size() == 0 ) {
			actionButton.setEnabled(true);
		}else {
			actionButton.setEnabled(false);
		}
	}

	private Component createDecisionInfoPanel() {
		JPanel decisionPanel = new JPanel(new GridLayout(1,2));
		statusList = new JList<String>(Helpers.getNames(StatusType.class));
		statusList.setSelectedValue(StatusType.getName(ship.getStatus().getStatus()).name(), true);
		statusList.addListSelectionListener(this);
		statusRenderer = new StatusListRenderer(StatusType.getName(ship.getStatus().getStatus()).name());
		statusList.setCellRenderer(statusRenderer);
		statusList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		statusList.setLayoutOrientation(JList.VERTICAL);
		statusList.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 3, Color.darkGray));
		decisionList = new JList<String>(Helpers.getNames(ActionType.class));
		decisionList.addListSelectionListener(this);
		DecisionListRenderer decisionRenderer = new DecisionListRenderer(statusList);
		decisionList.setCellRenderer(decisionRenderer);
		decisionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		decisionList.setLayoutOrientation(JList.VERTICAL);
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

	private void checkStatusLabel() {
		statusLabel.setText("Your status is: " + StatusType.getName(ship.getStatus().getStatus()).name()+
				"\nYou are asking for: " + ActionType.getName(ship.getStatus().getAction()).name());
	}

	private void checkPermissionsLabel() {
		permissionLabel.setText(PermissionType.getName(ship.getStatus().getPermission()).name());
		if(ship.getStatus().getPermission().equals(PermissionType.ALLOW.toString())) {
			permissionLabel.setBackground(new Color(74, 237, 49));
			permissionLabel.setOpaque(true);
		}else if (ship.getStatus().getPermission().equals(PermissionType.DENY.toString())) {
			permissionLabel.setBackground(new Color(255, 22, 73));
			permissionLabel.setOpaque(true);
		}else{
			permissionLabel.setBackground(new Color(244, 185, 66));
			permissionLabel.setOpaque(true);
		}
	}

	private Component crateStatusLabel(Border statusBorder) {
		statusLabel = new JLabel();
		checkStatusLabel();
		statusLabel.setBorder(statusBorder);
		statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
		statusLabel.setVerticalAlignment(SwingConstants.CENTER);
		return statusLabel;
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		repaintElements();
	}

	public void repaintElements() {
		decisionList.repaint();
		statusRenderer.setStatusType(StatusType.getName(ship.getStatus().getStatus()).name());
		statusList.setSelectedValue(StatusType.getName(ship.getStatus().getStatus()).name(), true);
		statusList.repaint();
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
					logicButton.setEnabled(true);
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
					logicButton.setEnabled(false);
					logModel.add(CONNECTION_CLOSED);
				}
				catch (Exception e) {
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

				logicButton.setText("Reject communications");
				connectButton.setEnabled(false);
				actionButton.setEnabled(true);
				logModel.add(LOGIC_INITIALIZED);
			}
			else {
				// Reject communications
				rejectCommunications();

				logicButton.setText("Wait for discovery");
				connectButton.setEnabled(true);
				actionButton.setEnabled(false);
				logModel.add(LOGIC_STOPPED);
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
			String a = ActionType.valueOf(decisionList.getSelectedValue()).toString();
			String s = checkNewAction();
			Status newStatus = new Status(s, a);
			ship.addAction(newStatus);
			ship.getStatus().setAction(a);
			ship.getStatus().setPermission(PermissionType.ASK.toString());
			actionButton.setEnabled(false);
		}

		private String checkNewAction() {
			String newStatus = null;
			ActionType at = ActionType.valueOf(decisionList.getSelectedValue());
			switch(at) {
			case ENTER:
				newStatus = StatusType.PARKING.toString();
				break;
			case LEAVE:
				newStatus = StatusType.SEA.toString();
				break;
			case IDLE:
				newStatus = ship.getStatus().getStatus();
				break;
			}
			return newStatus;
		}
	}

	public class SimulationAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		String text;
		Icon icon;

		public SimulationAction(String text, Icon icon, String description, Integer mnemonic) {
			super(text, icon);
			this.text = text;
			this.icon = icon;
			this.putValue(Action.SHORT_DESCRIPTION, description);
			this.putValue(Action.MNEMONIC_KEY, mnemonic);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			new SimulationDialog(window, simulationShipLogic);
			serial.addObserver(simulationShipLogic);
		}
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
		public void actionPerformed(ActionEvent actionEvent) {
			onWindowClosing();
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
		if (!this.serial.isConnected() && !shipDiscovered) {
			this.exitProgram();
		} else {
			int dialogResult = JOptionPane.showConfirmDialog(this.window, ((this.shipDiscovered) ?
							"System is initialized.\n" : "Serial connection is established.\n")
							+ "Do you really want to exit?",
					"Warning", JOptionPane.YES_NO_OPTION);

			if (dialogResult == JOptionPane.YES_OPTION) {
				rejectCommunications();
				try {
					this.serial.closeConnection();
				} catch (Exception e) {
					this.logModel.add(e.getMessage());
				}
				this.exitProgram();
			}
		}
	}

	private void exitProgram() {
		this.window.dispose();
		System.exit(0);
	}

}
