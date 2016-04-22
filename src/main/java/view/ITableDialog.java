package view;

import model.IMenu;
import controller.IDialogController;

/**
 * 
 *         An interface modelling a table dialog. A table dialog is a dialog
 *         window which shows all the orders of a certain table, and allows you
 *         to add new ones, choosing from a provided {@link IMenu}.
 *
 */
public interface ITableDialog {

    /**
     * @param name
     *            The name of the dish you want to add to the dialog
     * @param price
     *            The price of the dish you want to add to the dialog
     * @param amount
     *            The amount of dishes to add
     * @param processed
     *            The amount of already processed dishes
     * 
     *            Adds the given order to the dialog. You have to specify a
     *            name, a cost, a total amount and the amount of already
     *            processed dishes, which is the amount of already served dishes
     *            of this kind.
     */
    void addOrderToView(String name, double price, int amount, int processed);

    /**
     * @param bill
     *            The total bill of this table
     * @param effectiveBill
     *            The cost of the already served dishes
     * 
     *            Updates the bill of the table
     */
    void billUpdate(double bill, double effectiveBill);

    /**
     * Erases all the entries from the tab containing all the orders.
     */
    void clearTab();

    /**
     * @param e
     *            An exception thrown somewhere in the code while computing
     *            something related to this dialog
     * 
     *            Shows the message of the given exception
     */
    void showError(Exception e);

    /**
     * @param message
     *            A {@link String} you want to show
     * 
     *            Shows a generic message
     */
    void showMessage(String message);

    /**
     * Clear all the error previously shown with the
     * {@link #showError(Exception)} method.
     */
    void clearErrors();

    /**
     * @param dialogCtrl
     *            the {@link IDialogController} which will control this dialog
     * 
     *            Sets the given {@link IDialogController} as this dialog's
     *            controller, then builds the entire dialog window.
     */
    void setControllerAndBuildView(IDialogController dialogCtrl);

}