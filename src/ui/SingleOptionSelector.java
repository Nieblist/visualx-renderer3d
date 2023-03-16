package ui;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class SingleOptionSelector extends Selector {

	String field;
	String value;
	String initOption = null;
	
	ButtonGroup buttonGroup;
    ArrayList< String > optionsName;
    ArrayList< String > optionsField;
    
    public SingleOptionSelector(String field) {
    	super(field);
    	optionsName = new ArrayList< String >();
    	optionsField = new ArrayList< String >();
	}
    
	public SingleOptionSelector(String field, String initOption) {
		super(field);
		optionsName = new ArrayList< String >();
		optionsField = new ArrayList< String >();
		this.initOption = initOption;
	}
	
	public void addOption(String name, String field) {
		optionsName.add(name);
		optionsField.add(field);
	}
	
	@Override
	public Component getSelectorComponent() {
		Component res = new JPanel();
		
		buttonGroup = new ButtonGroup();
		for (String s : optionsName) {
			JRadioButton button = new JRadioButton(s);
			if (initOption != null && initOption.equals(s)) {
				button.setSelected(true);
			}
			
			buttonGroup.add(button);
			((JPanel) res).add(button);
		}		
		
		return res;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public void setValue(Object value) {
		this.value = (String) value;
	}

	@Override
	public void selectValue() {
		int index = optionsName.indexOf(getSelectedButtonText(buttonGroup));
		value = optionsField.get(index);
	}
	
    private String getSelectedButtonText(ButtonGroup buttonGroup) {
        for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();

            if (button.isSelected()) {
                return button.getText();
            }
        }

        return null;
    }
}
