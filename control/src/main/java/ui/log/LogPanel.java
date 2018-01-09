package ui.log;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.*;

public class LogPanel extends JScrollPane {
	private static final long serialVersionUID = 1L;
	
	AutoScrollListModel autoScrollListModel;
	JList<String> jList;
	
	public LogPanel(AutoScrollListModel autoScrollListModel) {
		this.autoScrollListModel = autoScrollListModel;
		this.jList = new JList<>(autoScrollListModel);
		this.jList.setModel(autoScrollListModel);
		this.autoScrollListModel.addListDataListener(new AutoScrollListDataListener());
		this.jList.setCellRenderer(new LogListCellRenderer());
		this.setViewportView(jList);
		this.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Log"),
				BorderFactory.createLineBorder(Color.GRAY)));
	}

	public class AutoScrollListDataListener implements ListDataListener {
		@Override
		public void intervalAdded(ListDataEvent listDataEvent) {
			jList.ensureIndexIsVisible(autoScrollListModel.getSize() - 1);
		}

		@Override
		public void intervalRemoved(ListDataEvent listDataEvent) {
			jList.ensureIndexIsVisible(autoScrollListModel.getSize() - 1);
		}

		@Override
		public void contentsChanged(ListDataEvent listDataEvent) {
			jList.ensureIndexIsVisible(autoScrollListModel.getSize() - 1);
		}
	}
}