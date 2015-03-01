package model;

import java.util.Arrays;
import java.util.LinkedList;

import benderUtilities.CheckNull;

/**
 * @author Giacomo Scaparrotti
 *
 */
public class Menu extends LinkedList<IDish> implements IMenu{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1111129590390041868L;
	
	/**
	 * Creates a new menu with no {@link IDish} objects in it.
	 */
	public Menu() {
	}
	
	public void addItems(IDish... items) {
		for(IDish i : items) {
			CheckNull.checkNull(i);
		}
		this.addAll(Arrays.asList(items));
	}
	
	public IDish[] getDishesArray() {
		this.sort((t1, t2) -> t1.getName().compareTo(t2.getName()));
		return this.toArray(new Dish[this.size()]);
	}

}
