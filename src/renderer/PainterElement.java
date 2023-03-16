package renderer;

import java.util.ArrayList;

public class PainterElement {
	private double priority;
	Paintable element;
	
	public PainterElement(Paintable element, double priorityGreaterLast) {
		this.element = element;
		priority = priorityGreaterLast;
	}

	public Paintable getElement() {
		return element;
	}
	
	void addToList(ArrayList< PainterElement > list) {
		int i = 0;
		while (i < list.size() && list.get(i).priority < priority) {
			i++;
		}
		list.add(i, this);
	}
}
