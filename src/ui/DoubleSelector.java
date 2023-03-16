package ui;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

public class DoubleSelector extends Selector {

	private String title = null;
	private double value;
	private double initValue, min, max;
	
	JSpinner doubleSpinner;
	
	
	public DoubleSelector(String field, double initValue) {
		super(field);
		min = -Double.MAX_VALUE;
		max =  Double.MAX_VALUE;
		this.initValue = initValue;
	}
	
	public DoubleSelector(String field, double initValue, double min, double max) {
		super(field);
		this.min = min;
		this.max = max;
		this.initValue = initValue;
	}
	
	public DoubleSelector(String field, double initValue, double min, double max, String title) {
		super(field);
		this.min = min;
		this.max = max;
		this.initValue = initValue;
		this.title = title;
	}
	
	@Override
	public Component getSelectorComponent() {
		Component res = new JPanel();

        SpinnerModel doubleModel = new SpinnerNumberModel(initValue, min, max, 0.01);
        
        doubleSpinner = new JSpinner();
        doubleSpinner.setModel(doubleModel);
		
        JComponent c = ((JSpinner.DefaultEditor) doubleSpinner.getEditor());
        Dimension prefSize = c.getPreferredSize();
        prefSize = new Dimension(20, prefSize.height);
        c.setPreferredSize(prefSize);
        
        if (title != null) {
        	JLabel titleLabel = new JLabel(title);
            ((JPanel) res).add(titleLabel);
        }
        
        ((JPanel) res).add(doubleSpinner);
		return res;
	}

	@Override
	public Double getValue() {
		return value;
	}

	@Override
	public void setValue(Object value) {
		this.value = (Double) value;
	}

	@Override
	public void selectValue() {
		value = (double) doubleSpinner.getValue();
	}

}
