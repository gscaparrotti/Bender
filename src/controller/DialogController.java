package controller;

import java.awt.print.PrinterException;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.swing.JTable;

import benderUtilities.CheckNull;
import view.ITableDialog;
import model.IDish;
import model.IMenu;
import model.IRestaurant;
import model.Pair;

public class DialogController implements IDialogController {
	
	private ITableDialog tableDialog;
	private IMainController ctrl;
	
	/**
	 * @param ctrl A {@link IMainController} instance
	 * 
	 * Creates a new DialogController object. It need a MainController as an argument,
	 * since it is needed to get the {@link IMenu} and the {@link IRestaurant}.
	 */
	public DialogController(IMainController ctrl) {
		CheckNull.checkNull(ctrl);
		this.ctrl = ctrl;
	}
	
	/* (non-Javadoc)
	 * @see controller.IDialogController#getMenu()
	 */
	@Override
	public IDish[] getMenu() {
		return ctrl.getMenu().getDishesArray();
	}
	
	/* (non-Javadoc)
	 * @see controller.IDialogController#commandOrdersViewUpdate(int)
	 */
	@Override
	public void commandOrdersViewUpdate(final int tableNumber) {
		Iterator<Entry<IDish, Pair<Integer, Integer>>> i = ctrl.getRestaurant().getOrders(tableNumber).entrySet().iterator();
		double bill = 0;
		double effectiveBill = 0;
		tableDialog.clearTab();
		while (i.hasNext()) {
			Entry<IDish, Pair<Integer, Integer>> entry = i.next();
			tableDialog.addOrderToView(entry.getKey().getName(), entry.getKey().getPrice(), entry.getValue().getX(), entry.getValue().getY());
			bill += entry.getKey().getPrice() * entry.getValue().getX();
			effectiveBill += entry.getKey().getPrice() * entry.getValue().getY();
		}
		tableDialog.billUpdate(bill, effectiveBill);
	}
	
	/* (non-Javadoc)
	 * @see controller.IDialogController#commandAdd(int, model.Dish, int)
	 */
	@Override
	public void commandAdd(int tableNumber, IDish item, int amount) {
		CheckNull.checkNull(item);
		try {
			ctrl.getRestaurant().addOrder(tableNumber, item, amount);
			tableDialog.clearErrors();
		} catch (Exception e) {
			commandErrorUpdate(e);
		}
		updateStatus(tableNumber);
	}
	
	/* (non-Javadoc)
	 * @see controller.IDialogController#commandRemove(int, model.Dish, int)
	 */
	@Override
	public void commandRemove(int tableNumber, IDish item, int amount) {
		CheckNull.checkNull(item);
		try {
			ctrl.getRestaurant().removeOrder(tableNumber, item, amount);
			tableDialog.clearErrors();
		} catch (Exception e) {
			commandErrorUpdate(e);
		}
		updateStatus(tableNumber);
	}
	
	/* (non-Javadoc)
	 * @see controller.IDialogController#commandUpdateProcessedOrders(int, model.Dish)
	 */
	@Override
	public void commandUpdateProcessedOrders(int tableNumber, IDish item) {
		CheckNull.checkNull(item);
		try {
			ctrl.getRestaurant().setOrderAsProcessed(tableNumber, item);
		} catch (Exception e) {
			commandErrorUpdate(e);
		}
		updateStatus(tableNumber);
	}
	
	/* (non-Javadoc)
	 * @see controller.IDialogController#commandPrint(int, javax.swing.JTable, java.lang.String, java.lang.String)
	 */
	@Override
	public void commandPrint(int tableNumber, JTable c, String up, String down) {
		CheckNull.checkNull(c);
		if (verifyRemaining(tableNumber)) {
			tableDialog.showMessage("Attenzione: si sta per stampare il conto di un tavolo con ordini non ancora evasi.");	
		}
		printBillFromJTable(c, up, down);
	}
	
	/* (non-Javadoc)
	 * @see controller.IDialogController#commandReset(int)
	 */
	@Override
	public void commandReset(int tableNumber) {
		ctrl.getRestaurant().resetTable(tableNumber);
		updateStatus(tableNumber);
	}
	
	@Override
	public void setView(ITableDialog td) {
		if(td==null) {
			throw new NullPointerException();
		}
		this.tableDialog = td;
		td.setControllerAndBuildView(this);
	}
	
	private void updateStatus(int tableNumber) {
		ctrl.autoSave();
		this.commandOrdersViewUpdate(tableNumber);
	}
	
	private boolean verifyRemaining(int tableNumber) {
		Iterator<Entry<IDish, Pair<Integer, Integer>>> i = ctrl.getRestaurant().getOrders(tableNumber).entrySet().iterator();
		boolean remaining = false;
		while (i.hasNext()) {
			Entry<IDish, Pair<Integer, Integer>> entry = i.next();
			if (entry.getValue().getX() != entry.getValue().getY()) {
				remaining = true;
				break;
			}
		}
		return remaining;
	}
	
	private void printBillFromJTable(JTable c, String up, String down) {
		try {
			if(up!=null && down !=null) {
				c.print(JTable.PrintMode.FIT_WIDTH, new MessageFormat(up), new MessageFormat(down));
			} else {
				c.print(JTable.PrintMode.FIT_WIDTH);
			}
		} catch (PrinterException e) {
			tableDialog.showError(e);
		}
	}
	
	
	private void commandErrorUpdate(Exception e) {
		CheckNull.checkNull(e);
		tableDialog.showError(e);
	}

}
