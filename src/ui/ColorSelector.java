package ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ColorSelector extends Selector {

	private String title = null;
	private int value;
	
	private Component res;
	private ColorIcon colorIcon;
	private int iconSize = 20;
	private JButton colorButton;

	 private static class ColorIcon implements Icon {

	        private int size;
	        private Color color;

	        public ColorIcon(final int size, final Color color) {
	            this.size = size;
	            this.color = color;
	        }

	        @Override
	        public void paintIcon(Component c, Graphics g, int x, int y) {
	            Graphics2D g2d = (Graphics2D) g;
	            g2d.setRenderingHint(
	                RenderingHints.KEY_ANTIALIASING,
	                RenderingHints.VALUE_ANTIALIAS_ON);
	            g2d.setColor(color);
	            g2d.fillOval(x, y, size, size);
	            g2d.setColor(Color.BLACK);
	            g2d.drawOval(x, y, size, size);
	        }
	        
	        public void setColor(Color color) {
	        	this.color = color;
	        }
	        
	        public Color getColor() {
	        	return color;
	        }

	        @Override
	        public int getIconWidth() {
	            return size;
	        }

	        @Override
	        public int getIconHeight() {
	            return size;
	        }
	    }
	
	
	public ColorSelector(String field, int initValue) {
		super(field);
		this.value = initValue;
	}
	
	public ColorSelector(String field, int initValue, String title, int iconSize) {
		super(field);
		this.value = initValue;
		this.title = title;
		this.iconSize = iconSize;
	}
	
	@Override
	public Component getSelectorComponent() {
		res = new JPanel();

		colorIcon = new ColorIcon(iconSize, new Color(value));
		
		colorButton = new JButton();
		colorButton.setIcon(colorIcon);
		colorButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
        		Color color = JColorChooser.showDialog(res, "Select a color", new Color(value));
        		if (color != null) {
        			value = color.getRGB();
        			colorIcon.setColor(new Color(value));
        		}
            }
        });
		
        if (title != null) {
        	JLabel titleLabel = new JLabel(title);
            ((JPanel) res).add(titleLabel);
        }
        
        ((JPanel) res).add(colorButton);
        
		return res;
	}

	@Override
	public Integer getValue() {
		return colorIcon.getColor().getRGB();
	}

	@Override
	public void setValue(Object value) {
		this.value = (Integer) value;
	}

	@Override
	public void selectValue() {

	}

}
