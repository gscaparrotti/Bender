package controller;

import view.IRestaurantView;

/**
 * @author Giacomo Scaparrotti
 * 
 * This is the {@link IRestaurantView} controller. Since that class only deal with very basic
 * informations, this class is very simple.
 *
 */
public interface IMainViewController {

	/**
	 * @return the ID of the new table
	 * 
	 * Adds a new table to the model.
	 */
	public int addTable();

	/**
	 * @return true if the removal succeded, false otherwise
	 * 
	 * Removes the last added table from the model.
	 */
	public boolean removeTable();

}