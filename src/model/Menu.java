package model;

import java.util.Arrays;
import java.util.LinkedList;

import benderAccessories.CheckNull;

public class Menu extends LinkedList<IDish> implements IMenu{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1111129590390041868L;
	
	public Menu() {
	}
	
	public void addItems(IDish... items) {
		for(IDish i : items) {
			CheckNull.checkNull(i);
		}
		this.addAll(Arrays.asList(items));
	}
	
	public Dish[] getDishesArray() {
		this.sort((t1, t2) -> t1.getName().compareTo(t2.getName()));
		return this.toArray(new Dish[this.size()]);
	}

}
