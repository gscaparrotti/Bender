package com.github.gscaparrotti.bender.controller;

import com.github.gscaparrotti.bender.view.IRestaurantView;
import com.github.gscaparrotti.bendermodel.model.IDish;

/**
 * 
 *         This is the {@link IRestaurantView} application.controller. Since that class only
 *         deal with very basic informations, this class is very simple.
 *
 */
public interface IMainViewController {

    /**
     * @param view
     * 
     *            Set the provided application.view as the affected application.view.
     */
    void setView(IRestaurantView view);

    /**
     * @return the ID of the new table
     * 
     *         Adds a new table to the model.
     */
    int addTable();

    /**
     * @return true if the removal succeded, false otherwise
     * 
     *         Removes the last added table from the model.
     */
    boolean removeTable();

    /**
     * Updateds the unprocessed orders on the application.view.
     */
    void updateUnprocessedOrders();
    
    /**
     * @param table the table the dish you want to set as processed belongs to
     * @param dishName  the dish you want to set as processed
     */
    void commandUpdateUnprocessedOrder(int table, IDish dish);
    
    /**
     * Tells the application.view to update the name of each table. You should call this method when you change the name of one
     * or more tables. 
     */
    void updateTableNames();

}