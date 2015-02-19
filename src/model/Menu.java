package model;

import java.util.Arrays;
import java.util.LinkedList;

public class Menu extends LinkedList<IDish> implements IMenu{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1111129590390041868L;
	
	public Menu() {
		testAdds();
	}
	
	public void addItems(IDish... items) {
		this.addAll(Arrays.asList(items));
	}
	
	public Dish[] getDishesArray() {
		return this.toArray(new Dish[this.size()]);
	}
	
	private void testAdds() {
		this.add(new Dish("Coca-Cola", 2.50));
		this.add(new Dish("Piadina vuota", 3.00));
		this.add(new Dish("Pizza margherita", 5.00));
	}

}
