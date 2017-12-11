package ui.panels;

import ui.log.LogAdapter;
import ui.log.LogModel;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JScrollPane;

public class LogPanel extends JScrollPane {
	private static final long serialVersionUID = 1L;
	
	LogModel logModel;
	JList<String> jList;
	
	public LogPanel(LogModel logModel) {
		this.logModel = logModel;
		this.jList = new JList<>(logModel);
		this.jList.setModel(logModel);
		this.jList.setCellRenderer(new LogAdapter());
		this.setViewportView(jList);
		this.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Log"),
				BorderFactory.createLineBorder(Color.GRAY)));
	}
}