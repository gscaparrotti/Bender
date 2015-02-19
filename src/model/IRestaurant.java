package model;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Jack
 *
 */
public interface IRestaurant extends Serializable{
	
	public int addTable();
	
	public int removeTable();
	
	/**
	 * Adds a new order to the provived table
	 */
	public void addOrder(int table, IDish item, int quantity);
	
	/**
	 * Removes a order from the provived table
	 */	
	public void removeOrder(int table, IDish item, int quantity);
	
	/**
	 * @param table
	 * @param item
	 * 
	 * Set the provided order as processed. More formally, this
	 * method sets the number of processed items to the value of 
	 * the ordered items. You cannot specify the amount of processed
	 * items.
	 */
	public void setOrderAsProcessed(int table, IDish item);
	
	/**
	 * @param table
	 * @return a map representing all the orders
	 * 
	 * Provides all the orders of the selected table
	 */
	public Map<IDish, Pair<Integer, Integer>> getOrders(int table);
	
	/**
	 * @param table
	 * 
	 * Deletes all the orders for the selected table
	 */
	public void resetTable(int table);
	
	/**
	 * @return the number of present tables
	 */
	public int getTablesAmount();
}
