package ui.log;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class LogAdapter implements ListCellRenderer<String> {
	
	@Override
	public Component getListCellRendererComponent(JList<? extends String> lista, String text, int index,
			boolean selected, boolean focus) {
		
		JLabel label = new JLabel();
		label.setText(text);
		
		if (text.equals("ERROR: No hay ningun cable serial conectado.")
				|| text.equals("ERROR: No se ha elegido ningun puerto serial.")
				|| text.equals("ERROR: Puerto ocupado. Reinicia el programa.")) {
			
			label.setForeground(Color.red);
		}
		else {
			label.setForeground(Color.black);
		}
		return label;
	}
}