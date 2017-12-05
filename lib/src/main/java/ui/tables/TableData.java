package ui.tables;

public class TableData {
	
	String name;
	double value;
	
	public TableData(String name, double valor) {
		this.name = name;
		this.value = valor;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}
	
	public Class<?> getFieldClass(int index) {
		switch (index) {
		case 0: return Integer.class;
		case 1: return Boolean.class;
		default: return String.class; 
		}
	}

	public Object getFieldAt(int column) {
		switch (column) {
		case 0: return name;
		case 1: return value;
		default: return null; 
		}
	}
}