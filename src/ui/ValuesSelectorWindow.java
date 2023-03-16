package ui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;

public class ValuesSelectorWindow extends JFrame {
	
	private static final long serialVersionUID = -1303685251229019226L;
	
	private ArrayList< SelectorListener > listeners;
	private WindowAdapter windowAdapter;
	
	private ArrayList< Selector > selectors;
	private String title;
	
	private JButton buttonAccept;
	
	public ValuesSelectorWindow(String title) {
		this.title = title;
		
		listeners = new ArrayList< SelectorListener >();
		this.selectors = new ArrayList< Selector >();
		
		windowAdapter = new WindowAdapter()
        {
            public void windowClosing(WindowEvent we)
            {
            	for (SelectorListener l : listeners) {
            		l.selectorClosed();
            	}            	
            	dispose();
            }
        };
        addWindowListener(windowAdapter);
        
        KeyListener keyboardActions = new KeyListener() {
 			@Override
 			public void keyPressed(KeyEvent ke) {
 				int key = ke.getKeyCode();
 				switch (key) {
 					case KeyEvent.VK_ENTER:
 						acceptValues();
 					break;
 				}
 				
 			}
 	
 			@Override
 			public void keyReleased(KeyEvent ke) { }
 	
 			@Override
 			public void keyTyped(KeyEvent ke) { }
     	   
        };
        
        addKeyListener(keyboardActions);
        
		buttonAccept = new JButton();
		buttonAccept.setText("Accept");
		buttonAccept.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
            	acceptValues();
            }
        });
		buttonAccept.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
	}
	
	public void addSelector(Selector selector) {
		selectors.add(selector);
		getContentPane().add(selector.getSelectorComponent());	
	}
	
	public void displayWindow() {
		getContentPane().add(buttonAccept);
		
		setTitle(title);
		setResizable(false);
		//setUndecorated(true);
		pack();
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
		
		setVisible(true);
	}

	protected void acceptValues() {
		for (Selector s : selectors) {
			s.selectValue();
		}
		
    	for (SelectorListener l : listeners) {
    		l.valuesSelected(this, selectors);
    	}
    	
    	dispose();
	}

	public void subscribe(SelectorListener listener) {
		listeners.add(listener);
	}
}



