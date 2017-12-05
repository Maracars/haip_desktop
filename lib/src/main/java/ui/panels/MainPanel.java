package ui.panels;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import jssc.SerialPortException;
import serial.Serial;
import ui.tables.CellRenderer;
import ui.tables.ColumnModel;
import ui.tables.TableData;
import ui.tables.TableModel;

public class MainPanel {

	//Ventana
	JFrame window;

	//Paneles
	JTabbedPane mainPanel;

	//Elementos Barra
	JMenuBar menuBar;
	JMenu menuExit;

	//Acciones
	AbstractAction exitAction;

	//Elementos
	JTable table;
	CellRenderer cellRenderer;
	TableModel tableModel;
	ColumnModel columnModel;
	List<TableData> tableDataList;

	//Boton de empezar/parar
	JButton CommButton;

	//Comunicacion con la FPGA
	Serial serial;

	public MainPanel() {
		this.initActions();
		this.initTable();

		window = new JFrame("Haip Ain't an Infor Project");
		window.setLocation(0, 0);
		window.setSize(1300, 700);
		window.setExtendedState(JFrame.MAXIMIZED_BOTH);
		window.setIconImage((new ImageIcon("lib/src/main/resources/HAIP_logo.png").getImage()));

		window.setJMenuBar(createMenuBar());
		window.getContentPane().add(createMainPanel(), BorderLayout.CENTER);

		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
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

	private Component createMainPanel() {
		this.mainPanel = new JTabbedPane();
		Icon icon = null;

		JComponent panel1 = (JComponent) createTab1();
		mainPanel.addTab("Tab 1", icon, panel1, "Does nothing");
		mainPanel.setMnemonicAt(0, KeyEvent.VK_1);

		JComponent panel2 = (JComponent) createTab2();
		mainPanel.addTab("Tab 2", icon, panel2, "Does nothing");
		mainPanel.setMnemonicAt(0, KeyEvent.VK_2);

		return mainPanel;
	}

	private Component createTab1() {
		return new ImagePanel("lib/src/main/resources/HAIP_logo.png");
	}

	private Component createTab2() {
		JScrollPane panelScrollTabla = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		panelScrollTabla.setViewportView(table);
		return panelScrollTabla;
	}

	/*private Component crearPanelLog() {
		modeloLog = new ModeloLog();
		return new PanelLog(modeloLog);
	}*/

	/*private Component crearPanelBotones() {
		JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
		ExitAction accCom = new ExitAction("Establecer comunicaci�n", new ImageIcon("icons/com.png"), "Comunicacion", KeyEvent.VK_ENTER);

		CommButton = new JButton(accCom);
		panel.add(CommButton);

		return panel;
	}*/

	private JMenuBar createMenuBar() {
		menuBar = new JMenuBar();
		menuBar.add(createExitMenu());
		return menuBar;
	}

	private JMenu createExitMenu() {
		menuExit = new JMenu("Salir");
		menuExit.add(exitAction);
		return menuExit;
	}

	private void initActions() {
		exitAction = new ExitAction("Salir", new ImageIcon("icons/salir.png"), "Salir", KeyEvent.VK_S);
	}

	public class ExitAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		String text;

		public ExitAction(String text, Icon icon, String descripcion, Integer nemonic) {
			super(text);
			this.text = text;
			this.putValue(Action.SHORT_DESCRIPTION, descripcion);
			this.putValue(Action.MNEMONIC_KEY, nemonic);
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

		public CommAction(String text, Icon icon, String description, Integer nemonic) {
			super(text);
			this.text = text;
			this.putValue(Action.SHORT_DESCRIPTION, description);
			this.putValue(Action.MNEMONIC_KEY, nemonic);
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (serial == null) {
				serial = new Serial();

				if (serial.isConnected()) {
					CommButton.setText("Disconnect");
				}
			}
			else {
				CommButton.setText("Connect");
			}
		}
	}

	public void closeConnection() {
		if (serial != null) {
			try {
				serial.closeConnection();
			}
			catch (SerialPortException e) {
				e.printStackTrace();
			}
			serial = null;
		}
	}

}
