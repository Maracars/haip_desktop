package ui.log;

import ui.panels.CheckBoxWithTextPanel;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.*;

public class AutoScrollListPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private JList<String> jList;
	private CheckBoxWithTextPanel autoScrollCheckBox;

	private LogListModel logListModel;
	
	public AutoScrollListPanel(LogListModel logListModel) {
		this.setLayout(new BorderLayout(10, 10));

		JScrollPane scrollPane = new JScrollPane();
		this.logListModel = logListModel;

		this.jList = new JList<>(logListModel);
		this.logListModel.addListDataListener(new AutoScrollListDataListener());
		this.jList.setCellRenderer(new LogListCellRenderer());

		this.autoScrollCheckBox = new CheckBoxWithTextPanel();

		scrollPane.setViewportView(jList);
		this.add(scrollPane, BorderLayout.CENTER);
		this.add(this.autoScrollCheckBox, BorderLayout.SOUTH);

		this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Log"));
	}

	public class AutoScrollListDataListener implements ListDataListener {
		@Override
		public void intervalAdded(ListDataEvent listDataEvent) {
			if (autoScrollCheckBox.isSelected()) {
				jList.ensureIndexIsVisible(logListModel.getSize() - 1);
			}
		}

		@Override
		public void intervalRemoved(ListDataEvent listDataEvent) {
			if (autoScrollCheckBox.isSelected()) {
				jList.ensureIndexIsVisible(logListModel.getSize() - 1);
			}
		}

		@Override
		public void contentsChanged(ListDataEvent listDataEvent) {
			if (autoScrollCheckBox.isSelected()) {
				jList.ensureIndexIsVisible(logListModel.getSize() - 1);
			}
		}
	}
}