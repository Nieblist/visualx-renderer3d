package ui;

import java.util.ArrayList;

public interface SelectorListener {
	public void valuesSelected(Object mainSelector, ArrayList< Selector > valueSelectors);
	public void selectorClosed();
}
