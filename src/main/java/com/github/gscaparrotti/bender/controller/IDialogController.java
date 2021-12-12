package com.github.gscaparrotti.bender.controller;

import com.github.gscaparrotti.bender.view.ITableDialog;
import com.github.gscaparrotti.bendermodel.model.IDish;
import com.github.gscaparrotti.bendermodel.model.IMenu;
import java.util.SortedSet;
import javax.swing.*;

/**
 * 
 *         This is the application.controller of {@link ITableDialog}. Its purpose is
 *         adding, removing and resetting orders, updating the dialog and
 *         printing the bill. Moreover, it dispatches the {@link IMenu}, whose
 *         use is necessary in the Dialog class.
 *
 */
public interface IDialogController {

    /**
     * @param td
     *            The {@link ITableDialog} instance used in the application
     * 
     *            This method determines the TableDialog which is affected by
     *            this application.controller. This TableDialog will show the orders and the
     *            bill. Other TableDialogs may call this class' methods, but the
     *            behavior may be undefined, which is to say that only one
     *            Dialog may be updated.
     * 
     */
    void setView(ITableDialog td);

    /**
     * Removes a previously associated application.view. If there is no application.view, then nothing happens.
     */
    void detachView();

    /**
     * @return An array containing all the {@link IDish} on the menu
     * 
     *         This method provides an easy way to get all the dishes on the
     *         menu.
     */
    IDish[] getMenu();

    /**
     * @param tableNumber
     *            the number of the table you want to update the application.view
     * 
     *            Update the graphical representation of all this table's orders
     *            on the previously set {@link ITableDialog}. If a TableDialog
     *            is not present, the behaviour is undetermined (e.g. an
     *            exception may be thrown).
     */
    void updateOrdersInView(int tableNumber);

    /**
     * Updates the orders of the currently displayed table
     */
    void updateOrdersInView();

    /**
     * @param tableNumber
     *            the number of the table
     * @param item
     *            a {@link IDish} object containing the dish to be added
     * @param amount
     *            the amount of dishes to add.
     * 
     *            Adds the specified dish to the orders of the selected table,
     *            in the specified amount.
     */
    void commandAdd(int tableNumber, IDish item, int amount);

    /**
     * @param tableNumber
     *            the number of the table
     * @param item
     *            a {@link IDish} object containing the dish to be removed
     * @param amount
     *            the amount of dishes to remove.
     * 
     *            Removes the specified dish to the orders of the selected
     *            table, in the specified amount.
     */
    void commandRemove(int tableNumber, IDish item, int amount);
    
    /**
     * @param table the table
     * @param name the new name for the table or null if you want to remove the name
     */
    void commandSetTableName(int table, String name);

    /**
     * @param tableName the table
     */
    void commandRemoveTableName(String tableName);
    
    /**
     * @param table
     * @return the name associated to this table
     */
    String commandGetTableName(int table);

    /**
     * @param table
     * @return The names of all the customers associated to this table
     */
    SortedSet<String> commandGetAllTableNames(int table);
    
    /**
     * Updates the displayed name of the given table
     * 
     * @param table
     */
    void updateTableNameInView(int table);

    /**
     * Updates the displayed name of the current table
     */
    void updateTableNameInView();

    /**
     * @param tableNumber
     *            the number of the table
     * @param item
     *            a {@link IDish} object containing the dish to be removed
     * 
     *            Update the number of the processed orders relative to the
     *            selected table and dish. This is useful if you want to
     *            distinguish between processed and unprocessed orders.
     */
    void commandUpdateProcessedOrders(int tableNumber, IDish item);

    /**
     * @param tableNumber
     *            the number of the table
     * @param c
     *            A {@link JTable} containing the orders to print
     * @param up
     *            a {@link String} which will be printed at the top of the
     *            document
     * @param down
     *            up a {@link String} which will be printed at the bottom of the
     *            document
     * 
     *            This method helps you printing the bill of the selected table.
     *            It relays on the presence of a {@link JTable} containing all
     *            the orders, so it may not be useful if the implementation of
     *            TableDialog does not involve a JTable. You can also specify
     *            two string, which will be printed at the top and at the bottom
     *            of the page. For example, you can use them to print the number
     *            of the table or the name of the restaurant.
     */
    void commandPrint(int tableNumber, JTable c, String up, String down);

    /**
     * @param tableNumber
     * 
     *            This method erases all the orders of the current customer of the selected table.
     */
    void commandReset(int tableNumber);

    /**
     * Erases all the orders for the selected table.
     *
     * @param tableNumber The table number
     */
    void commandHardReset(int tableNumber);

}