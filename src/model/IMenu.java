package model;

import java.io.Serializable;

/**
 * @author Giacomo Scaparrotti
 * 
 * An interface with the necessary methods to model a restaurant menu
 *
 */
public interface IMenu extends Serializable{
	
	/**
	 * @param items A variable number of {@link IDish} to add
	 * 
	 * Adds all the given {@link IDish} to this menu.
	 */
	public void addItems(IDish... items);
	
	/**
	 * @return An array containing all the {@link IDish} in this menu
	 */
	public IDish[] getDishesArray();

}
