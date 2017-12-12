package ui.panels;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import jssc.SerialPortException;
import models.Frame;
import protocol.SerialObserver;
import serial.Serial;
import ui.log.LogModel;
import ui.tables.CellRenderer;
import ui.tables.ColumnModel;
import ui.tables.TableData;
import ui.tables.TableModel;

public class MainPanel implements Observer {
	// Window
	JFrame window;

	// Actions
	AbstractAction exitAction, commAction, initAction;

	// Table Elements
	JTable table;
	List<TableData> tableDataList;
	Frame frame;

	// Buttons
	JButton commButton, initButton;

	// Serial Communication
	Serial serial;
	//SerialObserver serialObserver;

	// System Initialized
	boolean systemInitialized;

	public MainPanel(Serial serial) {
        this.window = new JFrame("Haip Ain't an Infor Project");
        this.window.setIconImage((new ImageIcon("control/src/main/resources/HAIP_squaredLogo.png").getImage()));
        this.window.setLocation(0, 0);
        this.window.setSize(new Dimension(java.awt.Toolkit.getDefaultToolkit().getScreenSize()));

        this.initActions();
        this.initTable();
        this.serial = serial;
		this.serial.addObserver(this);
		this.frame = null;
        //this.serialObserver = new SerialObserver(this.serial, this.tableDataList);
        this.systemInitialized = false;

        this.window.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.window.addWindowListener(this.createWindowClosingAdapter());
        this.window.setJMenuBar(this.createMenuBar());
        this.window.getContentPane().add(this.createSplitPane(), BorderLayout.CENTER);

        this.window.setVisible(true);
        this.window.setExtendedState(this.window.getExtendedState() | JFrame.MAXIMIZED_BOTH);

		this.tableDataList.add(new TableData(
				Integer.parseInt("10101010", 2),
				Integer.parseInt("10", 2),
				Integer.parseInt("01", 2),
				false));
	}

	private Component createSplitPane() {
		JSplitPane splitPane = new JSplitPane();

		splitPane.setDividerLocation(this.window.getWidth() / 6);
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
		this.tableDataList = new ArrayList<>();
        CellRenderer cellRenderer = new CellRenderer();
        ColumnModel columnModel = new ColumnModel(cellRenderer);
        TableModel tableModel = new TableModel(columnModel, tableDataList);

		this.table = new JTable(tableModel, columnModel);
		this.table.setRowHeight(this.window.getHeight() / 20);
		this.table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		this.table.getTableHeader().setFont(new Font("Comic Sans MS", Font.PLAIN, 20));
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

	@Override
	public void update(Observable observable, Object object) {
		if (object.getClass().equals(models.Frame.class)) {
			this.frame = (Frame) object;

			int shipID = Integer.parseInt(this.frame.getOriginId(), 2);
			int status = Integer.parseInt(this.frame.getData().getStatus().getStatus(), 2);
			int action = Integer.parseInt(this.frame.getData().getStatus().getAction(), 2);

			TableData tableData = new TableData(shipID, status, action, false);
			this.tableDataList.add(tableData);

			/*this.tableDataList.add(new TableData(
					Integer.parseInt("10101011", 2),
					Integer.parseInt("10", 2),
					Integer.parseInt("01", 2),
					false));
			this.table.repaint();*/
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
