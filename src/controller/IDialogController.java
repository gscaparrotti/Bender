package controller;

import javax.swing.JTable;

import view.ITableDialog;
import model.IDish;
import model.IMenu;

/**
 * @author Giacomo Scaparrotti
 * 
 * This is the controller of {@link ITableDialog}. Its purpose is adding, removing and resetting orders,
 * updating the dialog and printing the bill. Moreover, it dispatches the {@link IMenu}, 
 * whose use is necessary in the Dialog class. 
 *
 */
public interface IDialogController {
	
	/**
	 * @param td The {@link ITableDialog} instance used in the application
	 * 
	 * This method determines the TableDialog which is affected by this controller. This TableDialog
	 * will show the orders and the bill. Other TableDialogs may call this class' methods, but the behavior
	 * may be undefined, which is to say that only one Dialog may be updated.  
	 * 
	 */
	public void setView(ITableDialog td);

	/**
	 * @return An array containing all the {@link IDish} on the menu
	 * 
	 * This method provides an easy way to get all the dishes on the menu. 
	 */
	public IDish[] getMenu();

	/**
	 * @param tableNumber the number of the table you want to update the view
	 * 
	 * Update the graphical representation of all this table's orders on the previously set {@link ITableDialog}.
	 * If a TableDialog is not present, the behaviour is undetermined (e.g. an exception may be thrown).
	 */
	public void commandOrdersViewUpdate(int tableNumber);

	/**
	 * @param tableNumber the number of the table
	 * @param item a {@link IDish} object containing the dish to be added
	 * @param amount the amount of dishes to add.
	 * 
	 * Adds the specified dish to the orders of the selected table, in the specified amount.
	 */
	public void commandAdd(int tableNumber, IDish item, int amount);
	
	/**
	 * @param tableNumber the number of the table
	 * @param item a {@link IDish} object containing the dish to be removed
	 * @param amount the amount of dishes to remove.
	 * 
	 * Removes the specified dish to the orders of the selected table, in the specified amount.
	 */
	public void commandRemove(int tableNumber, IDish item, int amount);

	/**
	 * @param tableNumber the number of the table
	 * @param item a {@link IDish} object containing the dish to be removed
	 * 
	 * Update the number of the processed orders relative to the selected table and dish.
	 * This is useful if you want to distinguish between processed and unprocessed orders.
	 */
	public void commandUpdateProcessedOrders(int tableNumber, IDish item);

	/**
	 * @param tableNumber the number of the table
	 * @param c A {@link JTable} containing the orders to print
	 * @param up a {@link String} which will be printed at the top of the document
	 * @param down up a {@link String} which will be printed at the bottom of the document
	 * 
	 * This method helps you printing the bill of the selected table. It relays on the presence of
	 * a {@link JTable} containing all the orders, so it may not be useful if the implementation of TableDialog
	 * does not involve a JTable. 
	 * You can also specify two string, which will be printed at the top and at the bottom of the page. 
	 * For example, you can use them to print the number of the table or the name of the restaurant.
	 */
	public void commandPrint(int tableNumber, JTable c, String up,
			String down);

	/**
	 * @param tableNumber
	 * 
	 * This method erases all the orders of the selected table. 
	 */
	public void commandReset(int tableNumber);

}