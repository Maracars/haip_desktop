package ui.log;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.util.ArrayList;
import java.util.List;

public class LogModel implements ListModel<String> {

	List<String> messageList;
	List<ListDataListener> listenerList;
	
	public LogModel() {
		messageList = new ArrayList<>();
		listenerList = new ArrayList<>();
	}
	
	@Override
	public void addListDataListener(ListDataListener listener) {
		listenerList.add(listener);
	}

	@Override
	public String getElementAt(int index) {
		return messageList.get(index);
	}

	@Override
	public int getSize() {
		return messageList.size();
	}

	@Override
	public void removeListDataListener(ListDataListener listener) {
		listenerList.remove(listener);
	}

	public void add(String text) {
		messageList.add(text);
		for (ListDataListener listener : listenerList) {
			listener.contentsChanged(
					new ListDataEvent(messageList, ListDataEvent.CONTENTS_CHANGED, 0, messageList.size()));
		}
	}

	public void remove(int index) {
		messageList.remove(index);
		for (ListDataListener listener : listenerList) {
			listener.contentsChanged(
					new ListDataEvent(messageList, ListDataEvent.CONTENTS_CHANGED, index, index));
		}
	}
}