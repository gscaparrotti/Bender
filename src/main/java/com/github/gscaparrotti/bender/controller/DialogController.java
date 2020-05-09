package com.github.gscaparrotti.bender.controller;

import com.github.gscaparrotti.bender.entities.Customer;
import com.github.gscaparrotti.bender.springControllers.RestaurantController;
import com.github.gscaparrotti.bender.view.ITableDialog;
import com.github.gscaparrotti.bendermodel.model.IDish;
import com.github.gscaparrotti.bendermodel.model.IMenu;
import com.github.gscaparrotti.bendermodel.model.IRestaurant;
import com.github.gscaparrotti.bendermodel.model.Pair;
import java.awt.print.PrinterException;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import javax.swing.*;

import static com.github.gscaparrotti.bender.legacy.LegacyHelper.ctrl;
import static com.github.gscaparrotti.bender.legacy.LegacyHelper.ifBodyNotNull;

/**
 *
 */
public class DialogController implements IDialogController {

    private ITableDialog tableDialog;
    private final IMainController ctrl;

    /**
     * @param newCtrl
     *            A {@link IMainController} instance
     * 
     *            Creates a new DialogController object. It need a
     *            MainController as an argument, since it is needed to get the
     *            {@link IMenu} and the {@link IRestaurant}.
     */
    public DialogController(final IMainController newCtrl) {
        Objects.requireNonNull(newCtrl);
        this.ctrl = newCtrl;
    }

    @Override
    public void setView(final ITableDialog td) {
        if (td != null) {
            this.tableDialog = td;
            td.setControllerAndBuildView(this);
        }
    }

    @Override
    public void detachView() {
        this.tableDialog = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see application.controller.IDialogController#getMenu()
     */
    @Override
    public IDish[] getMenu() {
        return ctrl.getMenu().getDishesArray();
    }

    /*
     * (non-Javadoc)
     * 
     * @see application.controller.IDialogController#commandOrdersViewUpdate(int)
     */
    @Override
    public void updateOrdersInView(final int tableNumber) {
        if (tableDialog != null && tableDialog.getTable() == tableNumber) {
            synchronized (ctrl.getRestaurant()) {
                double bill = 0;
                double effectiveBill = 0;
                tableDialog.clearTab();
                for (final Map.Entry<IDish, Pair<Integer, Integer>> entry : ctrl.getRestaurant().getOrders(tableNumber).entrySet()) {
                    tableDialog.addOrderToView(entry.getKey().getName(), entry.getKey().getPrice(), entry.getValue().getX(),
                            entry.getValue().getY());
                    bill += entry.getKey().getPrice() * entry.getValue().getX();
                    effectiveBill += entry.getKey().getPrice() * entry.getValue().getY();
                }
                tableDialog.billUpdate(bill, effectiveBill);
            }
        }
    }

    @Override
    public void updateOrdersInView() {
        if (tableDialog != null) {
            updateOrdersInView(tableDialog.getTable());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see application.controller.IDialogController#commandAdd(int, model.Dish, int)
     */
    @Override
    public void commandAdd(final int tableNumber, final IDish item, final int amount) {
        Objects.requireNonNull(item);
        if (tableDialog != null && tableDialog.getTable() == tableNumber) {
            try {
                ctrl.getRestaurant().addOrder(tableNumber, item, amount);
                tableDialog.clearErrors();
            } catch (Exception e) {
                commandErrorUpdate(e);
            }
            updateStatus(tableNumber);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see application.controller.IDialogController#commandRemove(int, model.Dish, int)
     */
    @Override
    public void commandRemove(final int tableNumber, final IDish item, final int amount) {
        Objects.requireNonNull(item);
        if (tableDialog != null && tableDialog.getTable() == tableNumber) {
            try {
                ctrl.getRestaurant().removeOrder(tableNumber, item, amount);
                tableDialog.clearErrors();
            } catch (Exception e) {
                commandErrorUpdate(e);
            }
            updateStatus(tableNumber);
        }
    }
    
    @Override
    public void commandSetTableName(final int table, final String name) {
        ctrl.getRestaurant().setTableName(table, name);
        updateTableNameInView(table);
        updateStatus(table);
        ctrl.autoSave();
    }

    @Override
    public String commandGetTableName(final int table) {
        return ctrl.getRestaurant().getTableName(table);
    }

    @Override
    public SortedSet<String> commandGetAllTableNames(final int table) {
        return ifBodyNotNull(ctrl(RestaurantController.class).getCustomers(table), customers -> customers.stream().map(Customer::getName).collect(Collectors.toCollection(TreeSet::new)), TreeSet::new);
    }

    @Override
    public void updateTableNameInView(final int table) {
        if(tableDialog != null && tableDialog.getTable() == table) {
            tableDialog.updateTableNameInDialog();
        }
    }

    @Override
    public void updateTableNameInView() {
        if (tableDialog != null) {
            tableDialog.updateTableNameInDialog();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see application.controller.IDialogController#commandUpdateProcessedOrders(int,
     * model.Dish)
     */
    @Override
    public void commandUpdateProcessedOrders(final int tableNumber, final IDish item) {
        Objects.requireNonNull(item);
        if (tableDialog != null && tableDialog.getTable() == tableNumber) {
            try {
                ctrl.getRestaurant().setOrderAsProcessed(tableNumber, item);
                tableDialog.clearErrors();
            } catch (Exception e) {
                commandErrorUpdate(e);
            }
            updateStatus(tableNumber);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see application.controller.IDialogController#commandPrint(int, javax.swing.JTable,
     * java.lang.String, java.lang.String)
     */
    @Override
    public void commandPrint(final int tableNumber, final JTable c, final String up, final String down) {
        Objects.requireNonNull(c);
        if (tableDialog != null && tableDialog.getTable() == tableNumber) {
            if (verifyRemaining(tableNumber)) {
                tableDialog
                        .showMessage("Attenzione: si sta per stampare il conto di un tavolo con ordini non ancora evasi.");
            }
            printBillFromJTable(c, up, down);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see application.controller.IDialogController#commandReset(int)
     */
    @Override
    public void commandReset(final int tableNumber) {
        if (tableDialog != null && tableDialog.getTable() == tableNumber) {
            ctrl.getRestaurant().resetTable(tableNumber);
            updateStatus(tableNumber);
            updateTableNameInView(tableNumber);
        }
    }

    private void updateStatus(final int tableNumber) {
        ctrl.autoSave();
        this.updateOrdersInView(tableNumber);
    }

    private boolean verifyRemaining(final int tableNumber) {
        synchronized (ctrl.getRestaurant()) {
            boolean remaining = false;
            for (final Map.Entry<IDish, Pair<Integer, Integer>> entry : ctrl.getRestaurant().getOrders(tableNumber).entrySet()) {
                if (!entry.getValue().getX().equals(entry.getValue().getY())) {
                    remaining = true;
                    break;
                }
            }
            return remaining; 
        }
    }

    private void printBillFromJTable(final JTable c, final String up, final String down) {
        try {
            if (up == null || down == null) {
                c.print(JTable.PrintMode.FIT_WIDTH);

            } else {
                c.print(JTable.PrintMode.FIT_WIDTH, new MessageFormat(up), new MessageFormat(down));
            }
        } catch (PrinterException e) {
            tableDialog.showError(e);
        }
    }

    private void commandErrorUpdate(final Exception e) {
        Objects.requireNonNull(e);
        tableDialog.showError(e);
    }

}
