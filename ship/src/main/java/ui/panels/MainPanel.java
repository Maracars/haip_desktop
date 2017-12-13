package ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import helpers.Helpers;
import jiconfont.icons.FontAwesome;
import jiconfont.swing.IconFontSwing;
import jssc.SerialPortException;
import models.Ship;
import protocol.ProtocolProperties.ActionType;
import protocol.ProtocolProperties.StatusType;
import serial.Serial;
import ui.log.LogModel;

public class MainPanel {
	// Window
	JFrame window;

	// Panels
	JSplitPane splitPane;
	JTabbedPane tabbedPane;
	JPanel leftPanel;

	// Menu Bar Elements
	JMenuBar menuBar;
	JMenu menuExit;

	// Actions
	AbstractAction exitAction, commAction, initAction, decisionAction;

	// Buttons
	JButton commButton, initButton, actionButton;

	// Serial Communication
	Serial serial;

	//Ship
	Ship ship;

	// System Initialized
	boolean systemInitialized;

	public MainPanel(Serial serial, Ship ship) {
		window = new JFrame("Haip Ain't an Infor Project");
		window.setIconImage((new ImageIcon("control/src/main/resources/HAIP_squaredLogo.png").getImage()));
		window.setLocation(0, 0);
		window.setSize(1300, 700);
		window.setExtendedState(JFrame.MAXIMIZED_BOTH);

		this.initActions();
		this.serial = serial;
		this.ship = ship;
		this.systemInitialized = false;

		this.window.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.window.addWindowListener(this.createWindowClosingAdapter());
		this.window.setJMenuBar(this.createMenuBar());
		this.window.getContentPane().add(this.createSplitPane(), BorderLayout.CENTER);

		window.setVisible(true);
		this.window.setExtendedState(this.window.getExtendedState() | JFrame.MAXIMIZED_BOTH);
	}

	private Component createSplitPane() {
		JSplitPane splitPane = new JSplitPane();

		splitPane.setDividerLocation(this.window.getWidth() / 6);
		splitPane.setLeftComponent(createLeftPanel());
		splitPane.setRightComponent(createShipPanel());

		return splitPane;
	}

	private Component createShipPanel() {
		JPanel shipPanel = new JPanel(new BorderLayout());
		shipPanel.add(createInfoPanel(), BorderLayout.NORTH);
		shipPanel.add(createDecisionInfoPanel(), BorderLayout.CENTER);
		shipPanel.add(createDecisionActionPanel(), BorderLayout.SOUTH);
		return shipPanel;
	}

	private Component createDecisionActionPanel() {
		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(0, 10, 10, 10));

		this.actionButton = new JButton(this.decisionAction);
		this.actionButton.setPreferredSize(new Dimension(300,100));
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
		JLabel statusLabel = new JLabel("STATUS");
		Border statusBorder = BorderFactory.createLineBorder(Color.darkGray, 3);
		statusLabel.setBorder(statusBorder);
		statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
		statusLabel.setVerticalAlignment(SwingConstants.CENTER);
		infoPanel.add(statusLabel);
		JLabel permissionLabel = new JLabel("PERMISSION");
		permissionLabel.setBorder(statusBorder);
		permissionLabel.setBackground(new Color(255, 22, 73));
		permissionLabel.setOpaque(true);
		permissionLabel.setHorizontalAlignment(SwingConstants.CENTER);
		permissionLabel.setVerticalAlignment(SwingConstants.CENTER);
		infoPanel.add(permissionLabel);
		return infoPanel;

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
		logoPanel.scaleImage(this.window.getWidth() / 6, this.window.getWidth() / 6);

		return logoPanel;
	}

	private Component createLogPanel() {
		LogModel logModel = new LogModel();
		return new LogPanel(logModel);
	}

	private Component createButtonsPanel() {
		JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
		panel.setBorder(new EmptyBorder(0, 10, 10, 10));

		this.commButton = new JButton(this.commAction);
		this.commButton.setPreferredSize(new Dimension(panel.getWidth(), this.window.getHeight() / 15));
		panel.add(commButton);

		this.initButton = new JButton(this.initAction);
		this.initButton.setPreferredSize(new Dimension(panel.getWidth(), this.window.getHeight() / 15));
		this.initButton.setEnabled(false);
		panel.add(initButton);

		return panel;
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
		decisionAction = new DecisionAction("Save Action", IconFontSwing.buildIcon(FontAwesome.CHECK, 16),
				"Save Action", KeyEvent.VK_ACCEPT);
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
