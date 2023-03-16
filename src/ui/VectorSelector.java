package ui;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import logic3D.Vec3;

public class VectorSelector extends Selector {

	private String title = null;
	private Vec3 value;
	JSpinner xSpinner;
	JSpinner ySpinner;
	JSpinner zSpinner;
	
	public VectorSelector(String field, Vec3 initValue) {
		super(field);
		value = new Vec3(initValue);
	}
	
	public VectorSelector(String field, Vec3 initValue, String title) {
		super(field);
		this.title = title;
		value = new Vec3(initValue);
	}
	
	@Override
	public Component getSelectorComponent() {
		Component res = new JPanel();

        SpinnerModel xModel = new SpinnerNumberModel(value.x, -Double.MAX_VALUE, Double.MAX_VALUE, 0.1);
        SpinnerModel yModel = new SpinnerNumberModel(value.y, -Double.MAX_VALUE, Double.MAX_VALUE, 0.1);
        SpinnerModel zModel = new SpinnerNumberModel(value.z, -Double.MAX_VALUE, Double.MAX_VALUE, 0.1);
        
        xSpinner = new JSpinner();
        xSpinner.setModel(xModel);
        ySpinner = new JSpinner();
        ySpinner.setModel(yModel);
        zSpinner = new JSpinner();
        zSpinner.setModel(zModel);
        
        JComponent c = ((JSpinner.DefaultEditor) xSpinner.getEditor());
        Dimension prefSize = c.getPreferredSize();
        prefSize = new Dimension(20, prefSize.height);
        c.setPreferredSize(prefSize);
        
        c = ((JSpinner.DefaultEditor) ySpinner.getEditor());
        c.setPreferredSize(prefSize);
        
        c = ((JSpinner.DefaultEditor) zSpinner.getEditor());
        c.setPreferredSize(prefSize);
        
        JLabel xLabel = new JLabel("X:");
        xLabel.setLabelFor(xSpinner);
        JLabel yLabel = new JLabel("Y:");
        yLabel.setLabelFor(ySpinner);
        JLabel zLabel = new JLabel("Z:");
        zLabel.setLabelFor(zSpinner);
        
        if (title != null) {
        	JLabel titleLabel = new JLabel(title);
            ((JPanel) res).add(titleLabel);
        }
        
        ((JPanel) res).add(xLabel);
        ((JPanel) res).add(xSpinner);
        
        ((JPanel) res).add(yLabel);
        ((JPanel) res).add(ySpinner);
        
        ((JPanel) res).add(zLabel);
        ((JPanel) res).add(zSpinner);
		return res;
	}

	@Override
	public Vec3 getValue() {
		return value;
	}

	@Override
	public void setValue(Object value) {
		this.value = (Vec3) value;
	}

	@Override
	public void selectValue() {
		value.x = (double) xSpinner.getValue();
		value.y = (double) ySpinner.getValue();
		value.z = (double) zSpinner.getValue();
	}

}
