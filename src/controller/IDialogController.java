package controller;

import javax.swing.JTable;

import view.ITableDialog;
import model.IDish;

public interface IDialogController {
	
	public void setView(ITableDialog td);
	
	public void updateReferences();

	public IDish[] getMenu();

	public void commandOrdersViewUpdate(int tableNumber);

	public void commandAdd(int tableNumber, IDish item, int amount);

	public void commandRemove(int tableNumber, IDish item, int amount);

	public void commandUpdateProcessedOrders(int tableNumber, IDish item);

	public void commandPrint(int tableNumber, JTable c, String up,
			String down);

	public void commandReset(int tableNumber);

}