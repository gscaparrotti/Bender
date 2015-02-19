package controller;

import java.awt.print.PrinterException;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.swing.JOptionPane;
import javax.swing.JTable;

import view.ITableDialog;
import model.Dish;
import model.IDish;
import model.IMenu;
import model.IRestaurant;
import model.Pair;

public class DialogController implements IDialogController {
	
	private ITableDialog tableDialog;
	private IMainController ctrl;
	private IMenu menu;
	private IRestaurant model;
	
	public DialogController(ITableDialog td, IMainController ctrl) {
		this.tableDialog = td;
		this.menu = ctrl.getMenu();
		this.model = ctrl.getRestaurant();
		this.ctrl = ctrl;
	}
	
	/* (non-Javadoc)
	 * @see controller.IDialogController#getMenu()
	 */
	@Override
	public Dish[] getMenu() {
		return menu.getDishesArray();
	}
	
	/* (non-Javadoc)
	 * @see controller.IDialogController#commandOrdersViewUpdate(int)
	 */
	@Override
	public void commandOrdersViewUpdate(final int tableNumber) {
		Iterator<Entry<IDish, Pair<Integer, Integer>>> i = model.getOrders(tableNumber).entrySet().iterator();
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
		try {
			model.addOrder(tableNumber, item, amount);
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
		try {
			model.removeOrder(tableNumber, item, amount);
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
		try {
			model.setOrderAsProcessed(tableNumber, item);
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
		boolean remaining;
		remaining = verifyRemaining(tableNumber);
		if (!remaining) {
			printBillFromJTable(c, up, down);
		} else {
			final int n = JOptionPane.showConfirmDialog(c.getParent(), 
					      "Attenzione, ci sono ancora ordini da evadere. Continuare?", "Stampa",  JOptionPane.YES_NO_OPTION);
			if (n == JOptionPane.YES_OPTION) {
				printBillFromJTable(c, up, down);
			}	
		}
	}
	
	/* (non-Javadoc)
	 * @see controller.IDialogController#commandReset(int)
	 */
	@Override
	public void commandReset(int tableNumber) {
		model.resetTable(tableNumber);
		updateStatus(tableNumber);
	}
	
	private void updateStatus(int tableNumber) {
		ctrl.autoSave();
		this.commandOrdersViewUpdate(tableNumber);
	}
	
	private boolean verifyRemaining(int tableNumber) {
		Iterator<Entry<IDish, Pair<Integer, Integer>>> i = model.getOrders(tableNumber).entrySet().iterator();
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
			c.print(JTable.PrintMode.FIT_WIDTH, new MessageFormat(up), new MessageFormat(down));
		} catch (PrinterException e) {
			JOptionPane.showMessageDialog(c.getParent(), "Errore nella stampa.");
		}
	}
	
	
	private void commandErrorUpdate(Exception e) {
		tableDialog.showError(e);
	}

}
