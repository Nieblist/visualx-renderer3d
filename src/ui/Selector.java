package ui;

import java.awt.Component;

public abstract class Selector {
	private String field;
	
	public Selector(String field) {
		this.field = field;
	}
	
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	
	public abstract Component getSelectorComponent();
	public abstract Object getValue();
	public abstract void setValue(Object value);
	public abstract void selectValue();
}
