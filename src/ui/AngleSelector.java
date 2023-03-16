package ui;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

public class AngleSelector extends Selector {

	private String title = null;
	private double value;
	private double initValue, min, max;
	
	JSpinner angleSpinner;
	
	
	public AngleSelector(String field, double initValue) {
		super(field);
		min = 0;
		max = 360;
		this.initValue = initValue;
	}
	
	public AngleSelector(String field, double initValue, double min, double max) {
		super(field);
		this.min = min;
		this.max = max;
		this.initValue = initValue;
	}
	
	public AngleSelector(String field, double initValue, double min, double max, String title) {
		super(field);
		this.min = min;
		this.max = max;
		this.initValue = initValue;
		this.title = title;
	}
	
	@Override
	public Component getSelectorComponent() {
		Component res = new JPanel();

        SpinnerModel angleModel = new SpinnerNumberModel(initValue, min, max, 10);
        
        angleSpinner = new JSpinner(angleModel);
        angleSpinner.setEditor(new JSpinner.NumberEditor(angleSpinner, "#"));
        
        JLabel degreesLabel = new JLabel("degrees");
        degreesLabel.setLabelFor(angleSpinner);
		
        if (title != null) {
        	JLabel titleLabel = new JLabel(title);
            ((JPanel) res).add(titleLabel);
        }
        
        ((JPanel) res).add(angleSpinner);
        ((JPanel) res).add(degreesLabel);
        
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
		value = (double) angleSpinner.getValue();
	}

}
