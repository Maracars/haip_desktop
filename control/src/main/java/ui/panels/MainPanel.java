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

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
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
		this.initActions();
		this.initTable();
		this.systemInitialized = false;

		window = new JFrame("Haip Ain't an Infor Project");
		window.setLocation(0, 0);
		window.setSize(1300, 700);
		window.setExtendedState(JFrame.MAXIMIZED_BOTH);
		window.setIconImage((new ImageIcon("control/src/main/resources/HAIP_squaredLogo.png").getImage()));

		window.setJMenuBar(createMenuBar());
		window.getContentPane().add(createSplitPane(), BorderLayout.CENTER);

		window.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				/* Before closing window, check if communication and system are disabled
				* If not, disable and close them before exiting */
				if (serial != null) {
					try {
						serial.closeConnection();
					} catch (SerialPortException e1) {
						e1.printStackTrace();
					}
					serial = null;
				}
				if (systemInitialized) {
					systemInitialized = false;
				}
				((JFrame)e.getSource()).dispose();
			}
		});

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

	private void initActions() {
		exitAction = new ExitAction("Exit", new ImageIcon("control/src/main/resources/icons/exit.png"),
				"Exit", KeyEvent.VK_X);
		commAction = new CommAction("Connect", new ImageIcon("control/src/main/resources/icons/comm.png"),
				"Connection", KeyEvent.VK_C);
		initAction = new InitAction("Initialize system", new ImageIcon("control/src/main/resources/icons/start.png"),
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
			if (serial == null) {
				serial = new Serial();
				try {
					serial.startConnection();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				commButton.setText("Disconnect");
				initButton.setEnabled(true);
			}
			else {
				try {
					serial.closeConnection();
				}
				catch (SerialPortException e) {
					e.printStackTrace();
				}
				serial = null;
				commButton.setText("Connect");
				initButton.setEnabled(false);
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
				systemInitialized = true;

				initButton.setText("Stop system");
				initButton.setIcon(new ImageIcon("control/src/main/resources/icons/start.png"));
				commButton.setEnabled(false);
			}
			else {
				// Stop System
				systemInitialized = false;

				initButton.setText("Initialize system");
				initButton.setIcon(new ImageIcon("control/src/main/resources/icons/stop.png"));
				commButton.setEnabled(true);
			}
		}
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
}
